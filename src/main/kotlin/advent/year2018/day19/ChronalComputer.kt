package advent.year2018.day19

import advent.year2015.day24.Ticker
import advent.year2018.day16.ChronalOpCode
import java.io.File

class ChronalComputer(registerCount: Int = 6) {
    val registers = MutableList(registerCount) { 0 }
    var instructionPointerRegister = 0


    fun execute(program: ChronalProgram) {
        instructionPointerRegister = program.instructionPointer

        val ticker = Ticker(100_000_000)
        while (registers[instructionPointerRegister] in 0 until program.instructions.size) {
            val instruction = program.instructions[registers[instructionPointerRegister]]
            instruction.op.execute(registers, instruction.inputA, instruction.inputB, instruction.output)
            registers[instructionPointerRegister]++
            ticker.tick()
        }
    }
}

class ChronalProgram(val instructionPointer: Int,
                     val instructions: List<ChronalInstruction>) {
    companion object {
        fun parse(input: String): ChronalProgram {
            val lines = input.lines()
            val instructionPointer = lines.first().substringAfterLast(" ").toInt()
            val instructions = lines.drop(1).map { ChronalInstruction.parse(it) }
            return ChronalProgram(instructionPointer, instructions)
        }
    }
}

class ChronalInstruction(val op: ChronalOpCode,
                         val inputA: Int,
                         val inputB: Int,
                         val output: Int) {
    companion object {
        private val REGEX = "(.*) (\\d+) (\\d+) (\\d+)".toRegex()

        fun parse(input: String): ChronalInstruction {
            val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable instruction $input")
            return ChronalInstruction(ChronalOpCode.valueOf(match.groupValues[1]),
                    match.groupValues[2].toInt(),
                    match.groupValues[3].toInt(),
                    match.groupValues[4].toInt())
        }
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day19/input.txt")
            .readText()
            .trim()

    val computer = ChronalComputer()
    val program = ChronalProgram.parse(input)
    computer.execute(program)

    println(computer.registers[0])

    // The second half could not execute in a reasonable amount of time. Instead here is my analysis of the program.

    // SECTION: Program start -- jump ahead to initialization.
    // 0   addi 5 16 5  Jump to line 17

    // SECTION: Main loop.
    // Upon entering line 1, the initialization section (below) will have set:
    // Registers 0, 1, and 2 to 0
    // 3 and 4 to some initial values.
    // However, register 3 gets reassigned in line 3, so essentially this main loop is a function of the initial value
    // of register 4. The result of this function is output to register 0.
    // As we enter the loop, we set both register 1 and register 2 to 1.
    // 1   seti 1 1 2   Set register 2 to 1
    // 2   seti 1 8 1   Set register 1 to 1

    // SECTION: The check.
    // If register 2 times register 1 equals register 4, then add register 2 to our running sum (that is contained in
    // register 0).
    // 3   mulr 2 1 3   Set register 3 to (register 2 * register 1)
    // 4   eqrr 3 4 3   Set register 3 to (register 3 == register 4)
    // 5   addr 3 5 5   If register 4 was == 2, jump to line 7
    // 6   addi 5 1 5   Jump to line 8
    // 7   addr 2 0 0   Set register 0 to (register 0 + register 2)

    // SECTION: Inner loop, on register 1.
    // Increment register 1. If it is still less than or equal to our original value register 4, jump to SECTION: The
    // check. Otherwise, continue to SECTION: Outer loop.
    // 8   addi 1 1 1   Set register 1 to (register 1 + 1)
    // 9   gtrr 1 4 3   Set register 3 to (register 1 > register 4)
    // 10   addr 5 3 5  If register 1 was > register 4, jump to line 12
    // 11   seti 2 6 5  Jump to line 3

    // SECTION: Outer loop, on register 2.
    // Increment register 2. If it is now greater than our original register 4, end the program. Otherwise, jump to line
    // 2, keeping the value in register 2 but again starting a new loop on register 1, beginning at 1.
    // 12   addi 2 1 2  Set register 2 to (register 2 + 1)
    // 13   gtrr 2 4 3  Set register 3 to (register 2 > register 4)
    // 14   addr 3 5 5  If register 2 was > register 4, jump to line 16
    // 15   seti 1 2 5  Jump to line 2
    // 16   mulr 5 5 5  End program (by jumping to line 256).

    // SECTION: Initialization (for both halves). We only come here right when the program starts.
    // 17   addi 4 2 4  Set register 4 to (uninitialized register 4 + 2) = 2
    // 18   mulr 4 4 4  Set register 4 to 2 * 2 = 4
    // 19   mulr 5 4 4  Set register 4 to 19 * 4 = 76
    // 20   muli 4 11 4 Set register 4 to 76 * 11 = 836
    // 21   addi 3 2 3  Set register 3 to (uninitialized register 3 + 2) = 2
    // 22   mulr 3 5 3  Set register 3 to 2 * 22 = 44
    // 23   addi 3 13 3 Set register 3 to 44 + 13 = 57
    // 24   addr 4 3 4  Set register 4 to 836 + 57 = 893

    // SECTION: Conditional based on which half of the problem we are in.
    // 25   addr 5 0 5  Jump ahead by (1 + register 0) - i.e., if we are in the second half of the problem go to line
    //                  27.
    // 26   seti 0 8 5  Jump to line 1.

    // SECTION: Initialization for only the second half of the problem.
    // 27   setr 5 5 3  Set register 3 to 27.
    // 28   mulr 3 5 3  Set register 3 to (28 * 27) = 756.
    // 29   addr 5 3 3  Set register 3 to (29 + 756) = 785.
    // 30   mulr 5 3 3  Set register 3 to (30 * 785) = 23550.
    // 31   muli 3 14 3 Set register 3 to (14 * 23550) = 329700.
    // 32   mulr 3 5 3  Set register 3 to (32 * 329700) = 10550400.
    // 33   addr 4 3 4  Set register 4 to 893 + 10550400 = 10551293.
    // 34   seti 0 9 0  Set register 0 to 0.
    // 35   seti 0 9 5  Jump to line 1.

    // So, from a more high level view, the program is:
    // reg_0 = 0
    // reg_4 = (some value)
    // for (reg_2 in 1..X):
    //   for (reg_1 in 1..X):
    //     if (reg_1 * reg_2 = reg_4):
    //       reg_0 += reg2

    // Just a little bit of thought will indicate that this is the sum of the divisors of our initial input value, which
    // we will implement in a vastly more efficient way (this implementation is O(N^2), and our easy implementation will
    // be O(N)).
    println(sumOfDivisors(10551293))
}

fun sumOfDivisors(input: Int) = (1..input).filter { input % it == 0 }.sum()