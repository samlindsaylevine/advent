package advent.year2019.day5

import advent.utils.digits
import java.io.File

/**
 * Enough has changed from Day 2 that I'm just going to clean re-implement and leave that as it is.
 */
class IntcodeComputer {

    fun execute(program: List<Long>,
                input: () -> Long = { throw IllegalStateException("no input available") }): ProgramResult {

        val output = mutableListOf<Long>()

        val finalState = execute(program, input) { output.add(it) }

        return ProgramResult(finalState, output)
    }

    fun execute(program: List<Long>,
                input: () -> Long,
                output: (Long) -> Unit): Memory {
        val initialState = program.withIndex()
                .associate { it.index.toLong() to it.value }
                .toMutableMap()
        val memory = Memory(initialState)
        var instructionPointer = 0L
        var relativeBase = 0L

        try {
            while (true) {
                val instructionAndModes = InstructionAndModes.parse(memory[instructionPointer])
                val values = ValueReader(memory,
                        instructionPointer,
                        relativeBase,
                        instructionAndModes.modes)

                val instructionResult = instructionAndModes.instruction.execute(values,
                        memory,
                        input)

                if (instructionResult.outputValue != null) output(instructionResult.outputValue)

                instructionPointer = instructionResult.pointerUpdate(instructionPointer)
                relativeBase += instructionResult.relativeBaseChange
            }
        } catch (halt: ProgramHaltedException) {
            return memory
        }
    }

}

private class ValueReader(private val memory: Memory,
                          private val instructionPointer: Long,
                          private val relativeBase: Long,
                          private val modes: List<ParameterMode>) {
    private fun getMode(index: Int) = if (index > modes.size) ParameterMode.POSITION else modes[index - 1]

    /**
     * Get the value to be used as a parameter, this many steps after the current instruction pointer.
     */
    operator fun get(index: Int): Long = when (getMode(index)) {
        ParameterMode.POSITION -> memory[getImmediate(index)]
        ParameterMode.IMMEDIATE -> getImmediate(index)
        ParameterMode.RELATIVE -> memory[getImmediate(index) + relativeBase]
    }

    private fun getImmediate(index: Int) = memory[instructionPointer + index]

    /**
     * Get the target index to be used for setting values.
     */
    fun getTarget(index: Int) = when (getMode(index)) {
        ParameterMode.IMMEDIATE -> getImmediate(index)
        ParameterMode.POSITION -> getImmediate(index)
        ParameterMode.RELATIVE -> getImmediate(index) + relativeBase
    }
}

data class Memory(private val state: MutableMap<Long, Long> = mutableMapOf()) {
    operator fun get(index: Long) = state[index] ?: 0
    operator fun set(index: Long, value: Long) {
        state[index] = value
    }

    fun asList(): List<Long> {
        val max = state.keys.maxOrNull() ?: 0
        if (max + 1 > Int.MAX_VALUE) throw IllegalStateException("Can't represent as list - too large! $max")
        val output = MutableList<Long>(max.toInt() + 1) { 0 }

        state.forEach { (index, value) -> output[index.toInt()] = value }

        return output
    }
}

private sealed class IntcodeInstruction {
    /**
     * @return A value to be added to the output, if any.
     */
    abstract fun execute(values: ValueReader,
                         memory: Memory,
                         input: () -> Long): InstructionResult
}

private object Add : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        memory[values.getTarget(3)] = values[1] + values[2]
        return InstructionResult({ it + 4 })
    }
}

private object Multiply : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        memory[values.getTarget(3)] = values[1] * values[2]
        return InstructionResult({ it + 4 })
    }
}

private object Input : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        memory[values.getTarget(1)] = input()
        return InstructionResult({ it + 2 })
    }
}

private object Output : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        return InstructionResult({ it + 2 }, values[1])
    }
}

private object JumpIfTrue : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        return if (values[1] != 0L) InstructionResult({ values[2] }) else InstructionResult({ it + 3 })
    }
}

private object JumpIfFalse : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        return if (values[1] == 0L) InstructionResult({ values[2] }) else InstructionResult({ it + 3 })
    }
}

private object LessThan : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        memory[values.getTarget(3)] = if (values[1] < values[2]) 1 else 0
        return InstructionResult({ it + 4 })
    }
}

private object Equals : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        memory[values.getTarget(3)] = if (values[1] == values[2]) 1 else 0
        return InstructionResult({ it + 4 })
    }
}

private object RelativeBaseOffset : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        return InstructionResult({ it + 2 }, relativeBaseChange = values[1])
    }

}

private object Halt : IntcodeInstruction() {
    override fun execute(values: ValueReader, memory: Memory, input: () -> Long): InstructionResult {
        throw ProgramHaltedException()
    }
}

private class InstructionResult(val pointerUpdate: (Long) -> Long,
                                val outputValue: Long? = null,
                                val relativeBaseChange: Long = 0)

private class ProgramHaltedException : Exception("The program halted.")

private enum class ParameterMode { POSITION, IMMEDIATE, RELATIVE }


private data class InstructionAndModes(val instruction: IntcodeInstruction,
                                       val modes: List<ParameterMode>) {
    companion object {
        private val instructionsByOpCode = mapOf(1 to Add,
                2 to Multiply,
                3 to Input,
                4 to Output,
                5 to JumpIfTrue,
                6 to JumpIfFalse,
                7 to LessThan,
                8 to Equals,
                9 to RelativeBaseOffset,
                99 to Halt)

        fun parse(number: Long): InstructionAndModes {
            val instruction = instructionsByOpCode[(number % 100).toInt()]
                    ?: throw java.lang.IllegalArgumentException("Bad instruction ${number % 100}")
            val modes = number.digits().reversed().drop(2).map {
                when (it) {
                    0 -> ParameterMode.POSITION
                    1 -> ParameterMode.IMMEDIATE
                    2 -> ParameterMode.RELATIVE
                    else -> throw IllegalArgumentException("Bad mode $it")
                }
            }
            return InstructionAndModes(instruction, modes)
        }

        private fun Long.digits() = this.toString().digits()
    }
}

data class ProgramResult(val finalState: Memory,
                         val output: List<Long>)


fun main() {
    val program = File("src/main/kotlin/advent/year2019/day5/input.txt")
            .readText()
            .trim()
            .split(",")
            .map { it.toLong() }

    val result = IntcodeComputer().execute(program, input = { 1 })
    println(result.output)

    val secondResult = IntcodeComputer().execute(program, input = { 5 })
    println(secondResult.output)
}