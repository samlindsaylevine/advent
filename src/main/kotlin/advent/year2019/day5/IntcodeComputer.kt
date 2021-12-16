package advent.year2019.day5

import advent.utils.digits
import java.io.File

/**
 * Enough has changed from Day 2 that I'm just going to clean re-implement and leave that as it is.
 *
 * --- Day 5: Sunny with a Chance of Asteroids ---
 * You're starting to sweat as the ship makes its way toward Mercury.  The Elves suggest that you get the air
 * conditioner working by upgrading your ship computer to support the Thermal Environment Supervision Terminal.
 * The Thermal Environment Supervision Terminal (TEST) starts by running a diagnostic program (your puzzle input).  The
 * TEST diagnostic program will run on your existing Intcode computer after a few modifications:
 * First, you'll need to add two new instructions:
 *
 * Opcode 3 takes a single integer as input and saves it to the position given by its only parameter. For example, the
 * instruction 3,50 would take an input value and store it at address 50.
 * Opcode 4 outputs the value of its only parameter. For example, the instruction 4,50 would output the value at
 * address 50.
 *
 * Programs that use these instructions will come with documentation that explains what should be connected to the
 * input and output. The program 3,0,4,0,99 outputs whatever it gets as input, then halts.
 * Second, you'll need to add support for parameter modes:
 * Each parameter of an instruction is handled based on its parameter mode.  Right now, your ship computer already
 * understands parameter mode 0, position mode, which causes the parameter to be interpreted as a position - if the
 * parameter is 50, its value is the value stored at address 50 in memory. Until now, all parameters have been in
 * position mode.
 * Now, your ship computer will also need to handle parameters in mode 1, immediate mode. In immediate mode, a
 * parameter is interpreted as a value - if the parameter is 50, its value is simply 50.
 * Parameter modes are stored in the same value as the instruction's opcode.  The opcode is a two-digit number based
 * only on the ones and tens digit of the value, that is, the opcode is the rightmost two digits of the first value in
 * an instruction. Parameter modes are single digits, one per parameter, read right-to-left from the opcode: the first
 * parameter's mode is in the hundreds digit, the second parameter's mode is in the thousands digit, the third
 * parameter's mode is in the ten-thousands digit, and so on. Any missing modes are 0.
 * For example, consider the program 1002,4,3,4,33.
 * The first instruction, 1002,4,3,4, is a multiply instruction - the rightmost two digits of the first value, 02,
 * indicate opcode 2, multiplication.  Then, going right to left, the parameter modes are 0 (hundreds digit), 1
 * (thousands digit), and 0 (ten-thousands digit, not present and therefore zero):
 * ABCDE
 *  1002
 *
 * DE - two-digit opcode,      02 == opcode 2
 *  C - mode of 1st parameter,  0 == position mode
 *  B - mode of 2nd parameter,  1 == immediate mode
 *  A - mode of 3rd parameter,  0 == position mode,
 *                                   omitted due to being a leading zero
 *
 * This instruction multiplies its first two parameters.  The first parameter, 4 in position mode, works like it did
 * before - its value is the value stored at address 4 (33). The second parameter, 3 in immediate mode, simply has
 * value 3. The result of this operation, 33 * 3 = 99, is written according to the third parameter, 4 in position mode,
 * which also works like it did before - 99 is written to address 4.
 * Parameters that an instruction writes to will never be in immediate mode.
 * Finally, some notes:
 *
 * It is important to remember that the instruction pointer should increase by the number of values in the instruction
 * after the instruction finishes. Because of the new instructions, this amount is no longer always 4.
 * Integers can be negative: 1101,100,-1,4,0 is a valid program (find 100 + -1, store the result in position 4).
 *
 * The TEST diagnostic program will start by requesting from the user the ID of the system to test by running an input
 * instruction - provide it 1, the ID for the ship's air conditioner unit.
 * It will then perform a series of diagnostic tests confirming that various parts of the Intcode computer, like
 * parameter modes, function correctly. For each test, it will run an output instruction indicating how far the result
 * of the test was from the expected value, where 0 means the test was successful.  Non-zero outputs mean that a
 * function is not working correctly; check the instructions that were run before the output instruction to see which
 * one failed.
 * Finally, the program will output a diagnostic code and immediately halt. This final output isn't an error; an output
 * followed immediately by a halt means the program finished.  If all outputs were zero except the diagnostic code, the
 * diagnostic program ran successfully.
 * After providing 1 to the only input instruction and passing all the tests, what diagnostic code does the program
 * produce?
 *
 * --- Part Two ---
 * The air conditioner comes online! Its cold air feels good for a while, but then the TEST alarms start to go off.
 * Since the air conditioner can't vent its heat anywhere but back into the spacecraft, it's actually making the air
 * inside the ship warmer.
 * Instead, you'll need to use the TEST to extend the thermal radiators. Fortunately, the diagnostic program (your
 * puzzle input) is already equipped for this.  Unfortunately, your Intcode computer is not.
 * Your computer is only missing a few opcodes:
 *
 * Opcode 5 is jump-if-true: if the first parameter is non-zero, it sets the instruction pointer to the value from the
 * second parameter. Otherwise, it does nothing.
 * Opcode 6 is jump-if-false: if the first parameter is zero, it sets the instruction pointer to the value from the
 * second parameter. Otherwise, it does nothing.
 * Opcode 7 is less than: if the first parameter is less than the second parameter, it stores 1 in the position given
 * by the third parameter.  Otherwise, it stores 0.
 * Opcode 8 is equals: if the first parameter is equal to the second parameter, it stores 1 in the position given by
 * the third parameter.  Otherwise, it stores 0.
 *
 * Like all instructions, these instructions need to support parameter modes as described above.
 * Normally, after an instruction is finished, the instruction pointer increases by the number of values in that
 * instruction. However, if the instruction modifies the instruction pointer, that value is used and the instruction
 * pointer is not automatically increased.
 * For example, here are several programs that take one input, compare it to the value 8, and then produce one output:
 *
 * 3,9,8,9,10,9,4,9,99,-1,8 - Using position mode, consider whether the input is equal to 8; output 1 (if it is) or 0
 * (if it is not).
 * 3,9,7,9,10,9,4,9,99,-1,8 - Using position mode, consider whether the input is less than 8; output 1 (if it is) or 0
 * (if it is not).
 * 3,3,1108,-1,8,3,4,3,99 - Using immediate mode, consider whether the input is equal to 8; output 1 (if it is) or 0
 * (if it is not).
 * 3,3,1107,-1,8,3,4,3,99 - Using immediate mode, consider whether the input is less than 8; output 1 (if it is) or 0
 * (if it is not).
 *
 * Here are some jump tests that take an input, then output 0 if the input was zero or 1 if the input was non-zero:
 *
 * 3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9 (using position mode)
 * 3,3,1105,-1,9,1101,0,0,12,4,12,99,1 (using immediate mode)
 *
 * Here's a larger example:
 * 3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
 * 1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
 * 999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99
 *
 * The above example program uses an input instruction to ask for a single number.  The program will then output 999 if
 * the input value is below 8, output 1000 if the input value is equal to 8, or output 1001 if the input value is
 * greater than 8.
 * This time, when the TEST diagnostic program runs its input instruction to get the ID of the system to test, provide
 * it 5, the ID for the ship's thermal radiator controller. This diagnostic test suite only outputs one number, the
 * diagnostic code.
 * What is the diagnostic code for system ID 5?
 *
 */
class IntcodeComputer {

  fun execute(
    program: List<Long>,
    input: () -> Long = { throw IllegalStateException("no input available") }
  ): ProgramResult {

    val output = mutableListOf<Long>()

    val finalState = execute(program, input) { output.add(it) }

    return ProgramResult(finalState, output)
  }

  fun execute(
    program: List<Long>,
    input: () -> Long,
    output: (Long) -> Unit
  ): Memory {
    val initialState = program.withIndex()
      .associate { it.index.toLong() to it.value }
      .toMutableMap()
    val memory = Memory(initialState)
    var instructionPointer = 0L
    var relativeBase = 0L

    try {
      while (true) {
        val instructionAndModes = InstructionAndModes.parse(memory[instructionPointer])
        val values = ValueReader(
          memory,
          instructionPointer,
          relativeBase,
          instructionAndModes.modes
        )

        val instructionResult = instructionAndModes.instruction.execute(
          values,
          memory,
          input
        )

        if (instructionResult.outputValue != null) output(instructionResult.outputValue)

        instructionPointer = instructionResult.pointerUpdate(instructionPointer)
        relativeBase += instructionResult.relativeBaseChange
      }
    } catch (halt: ProgramHaltedException) {
      return memory
    }
  }

}

/**
 * Given a list of values, returns a closure that samples from the list in order, suitable for using as input to an
 * execution.
 */
fun <T> List<T>.asInput(): () -> T {
  var index = 0
  return {
    this[index++]
  }
}

private class ValueReader(
  private val memory: Memory,
  private val instructionPointer: Long,
  private val relativeBase: Long,
  private val modes: List<ParameterMode>
) {
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
  abstract fun execute(
    values: ValueReader,
    memory: Memory,
    input: () -> Long
  ): InstructionResult
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

private class InstructionResult(
  val pointerUpdate: (Long) -> Long,
  val outputValue: Long? = null,
  val relativeBaseChange: Long = 0
)

private class ProgramHaltedException : Exception("The program halted.")

private enum class ParameterMode { POSITION, IMMEDIATE, RELATIVE }


private data class InstructionAndModes(
  val instruction: IntcodeInstruction,
  val modes: List<ParameterMode>
) {
  companion object {
    private val instructionsByOpCode = mapOf(
      1 to Add,
      2 to Multiply,
      3 to Input,
      4 to Output,
      5 to JumpIfTrue,
      6 to JumpIfFalse,
      7 to LessThan,
      8 to Equals,
      9 to RelativeBaseOffset,
      99 to Halt
    )

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

data class ProgramResult(
  val finalState: Memory,
  val output: List<Long>
)


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