package advent.year2019.day5

import java.io.File

/**
 * Enough has changed from Day 2 that I'm just going to clean re-implement and leave that as it is.
 */
class IntcodeComputer {

    fun execute(program: List<Int>,
                input: () -> Int = { throw IllegalStateException("no input available") }): ProgramResult {

        val output = mutableListOf<Int>()

        val finalState = execute(program, input) { output.add(it) }

        return ProgramResult(finalState, output)
    }

    fun execute(program: List<Int>,
                input: () -> Int,
                output: (Int) -> Unit): List<Int> {
        val state = program.toMutableList()
        var instructionPointer = 0

        try {
            while (true) {
                val instructionAndModes = InstructionAndModes.parse(state[instructionPointer])
                val values = ValueReader(state, instructionPointer, instructionAndModes.modes)

                val instructionResult = instructionAndModes.instruction.execute(values,
                        state,
                        input)

                if (instructionResult.outputValue != null) output(instructionResult.outputValue)

                instructionPointer = instructionResult.pointerUpdate(instructionPointer)
            }
        } catch (halt: ProgramHaltedException) {
            return state
        }
    }

}

private class ValueReader(private val state: List<Int>,
                          private val instructionPointer: Int,
                          private val modes: List<ParameterMode>) {
    private fun getMode(index: Int) = if (index > modes.size) ParameterMode.POSITION else modes[index - 1]

    /**
     * Get the value to be used as a parameter, this many steps after the current instruction pointer.
     */
    operator fun get(index: Int) = when (getMode(index)) {
        ParameterMode.POSITION -> state[state[instructionPointer + index]]
        ParameterMode.IMMEDIATE -> state[instructionPointer + index]
    }

    /**
     * Get the target index to be used for setting values.
     */
    fun getTarget(index: Int) = state[instructionPointer + index]
}


private sealed class IntcodeInstruction {
    /**
     * @return A value to be added to the output, if any.
     */
    abstract fun execute(values: ValueReader,
                         state: MutableList<Int>,
                         input: () -> Int): InstructionResult
}

private object Add : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        state[values.getTarget(3)] = values[1] + values[2]
        return InstructionResult({ it + 4 })
    }
}

private object Multiply : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        state[values.getTarget(3)] = values[1] * values[2]
        return InstructionResult({ it + 4 })
    }
}

private object Input : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        state[values.getTarget(1)] = input()
        return InstructionResult({ it + 2 })
    }
}

private object Output : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        return InstructionResult({ it + 2 }, values[1])
    }
}

private object JumpIfTrue : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        return if (values[1] != 0) InstructionResult({ values[2] }) else InstructionResult({ it + 3 })
    }
}

private object JumpIfFalse : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        return if (values[1] == 0) InstructionResult({ values[2] }) else InstructionResult({ it + 3 })
    }
}

private object LessThan : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        state[values.getTarget(3)] = if (values[1] < values[2]) 1 else 0
        return InstructionResult({ it + 4 })
    }
}

private object Equals : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        state[values.getTarget(3)] = if (values[1] == values[2]) 1 else 0
        return InstructionResult({ it + 4 })
    }
}

private object Halt : IntcodeInstruction() {
    override fun execute(values: ValueReader, state: MutableList<Int>, input: () -> Int): InstructionResult {
        throw ProgramHaltedException()
    }
}

private class InstructionResult(val pointerUpdate: (Int) -> Int,
                                val outputValue: Int? = null)

private class ProgramHaltedException : Exception("The program halted.")

private enum class ParameterMode { POSITION, IMMEDIATE }


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
                99 to Halt)

        fun parse(number: Int): InstructionAndModes {
            val instruction = instructionsByOpCode[number % 100]
                    ?: throw java.lang.IllegalArgumentException("Bad instruction ${number % 100}")
            val modes = number.digits().reversed().drop(2).map {
                when (it) {
                    0 -> ParameterMode.POSITION
                    1 -> ParameterMode.IMMEDIATE
                    else -> throw IllegalArgumentException("Bad mode $it")
                }
            }
            return InstructionAndModes(instruction, modes)
        }

        private fun Int.digits() = this.toString().split("").filter { it.isNotEmpty() }.map { it.toInt() }
    }
}

data class ProgramResult(val finalState: List<Int>,
                         val output: List<Int>)


fun main() {
    val program = File("src/main/kotlin/advent/year2019/day5/input.txt")
            .readText()
            .trim()
            .split(",")
            .map { it.toInt() }

    val result = IntcodeComputer().execute(program, input = { 1 })
    println(result.output)

    val secondResult = IntcodeComputer().execute(program, input = { 5 })
    println(secondResult.output)
}