package advent.year2024.day17

import advent.meta.readInput

/**
 * --- Day 17: Chronospatial Computer ---
 * The Historians push the button on their strange device, but this time, you all just feel like you're falling.
 * "Situation critical", the device announces in a familiar voice. "Bootstrapping process failed. Initializing
 * debugger...."
 * The small handheld device suddenly unfolds into an entire computer! The Historians look around nervously before one
 * of them tosses it to you.
 * This seems to be a 3-bit computer: its program is a list of 3-bit numbers (0 through 7), like 0,1,2,3. The computer
 * also has three registers named A, B, and C, but these registers aren't limited to 3 bits and can instead hold any
 * integer.
 * The computer knows eight instructions, each identified by a 3-bit number (called the instruction's opcode). Each
 * instruction also reads the 3-bit number after it as an input; this is called its operand.
 * A number called the instruction pointer identifies the position in the program from which the next opcode will be
 * read; it starts at 0, pointing at the first 3-bit number in the program. Except for jump instructions, the
 * instruction pointer increases by 2 after each instruction is processed (to move past the instruction's opcode and
 * its operand). If the computer tries to read an opcode past the end of the program, it instead halts.
 * So, the program 0,1,2,3 would run the instruction whose opcode is 0 and pass it the operand 1, then run the
 * instruction having opcode 2 and pass it the operand 3, then halt.
 * There are two types of operands; each instruction specifies the type of its operand. The value of a literal operand
 * is the operand itself. For example, the value of the literal operand 7 is the number 7. The value of a combo operand
 * can be found as follows:
 *
 * Combo operands 0 through 3 represent literal values 0 through 3.
 * Combo operand 4 represents the value of register A.
 * Combo operand 5 represents the value of register B.
 * Combo operand 6 represents the value of register C.
 * Combo operand 7 is reserved and will not appear in valid programs.
 *
 * The eight instructions are as follows:
 * The adv instruction (opcode 0) performs division. The numerator is the value in the A register. The denominator is
 * found by raising 2 to the power of the instruction's combo operand. (So, an operand of 2 would divide A by 4 (2^2);
 * an operand of 5 would divide A by 2^B.) The result of the division operation is truncated to an integer and then
 * written to the A register.
 * The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's literal operand, then
 * stores the result in register B.
 * The bst instruction (opcode 2) calculates the value of its combo operand modulo 8 (thereby keeping only its lowest 3
 * bits), then writes that value to the B register.
 * The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is not zero, it jumps
 * by setting the instruction pointer to the value of its literal operand; if this instruction jumps, the instruction
 * pointer is not increased by 2 after this instruction.
 * The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then stores the result in
 * register B. (For legacy reasons, this instruction reads an operand but ignores it.)
 * The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value. (If a
 * program outputs multiple values, they are separated by commas.)
 * The bdv instruction (opcode 6) works exactly like the adv instruction except that the result is stored in the B
 * register. (The numerator is still read from the A register.)
 * The cdv instruction (opcode 7) works exactly like the adv instruction except that the result is stored in the C
 * register. (The numerator is still read from the A register.)
 * Here are some examples of instruction operation:
 *
 * If register C contains 9, the program 2,6 would set register B to 1.
 * If register A contains 10, the program 5,0,5,1,5,4 would output 0,1,2.
 * If register A contains 2024, the program 0,1,5,4,3,0 would output 4,2,5,6,7,7,7,7,3,1,0 and leave 0 in register A.
 * If register B contains 29, the program 1,7 would set register B to 26.
 * If register B contains 2024 and register C contains 43690, the program 4,0 would set register B to 44354.
 *
 * The Historians' strange device has finished initializing its debugger and is displaying some information about the
 * program it is trying to run (your puzzle input). For example:
 * Register A: 729
 * Register B: 0
 * Register C: 0
 *
 * Program: 0,1,5,4,3,0
 *
 * Your first task is to determine what the program is trying to output. To do this, initialize the registers to the
 * given values, then run the given program, collecting any output produced by out instructions. (Always join the
 * values produced by out instructions with commas.) After the above program halts, its final output will be
 * 4,6,3,5,6,3,5,2,1,0.
 * Using the information provided by the debugger, initialize the registers to the given values, then run the program.
 * Once it halts, what do you get if you use commas to join the values it output into a single string?
 *
 * --- Part Two ---
 * Digging deeper in the device's manual, you discover the problem: this program is supposed to output another copy of
 * the program! Unfortunately, the value in register A seems to have been corrupted. You'll need to find a new value to
 * which you can initialize register A so that the program's output instructions produce an exact copy of the program
 * itself.
 * For example:
 * Register A: 2024
 * Register B: 0
 * Register C: 0
 *
 * Program: 0,3,5,4,3,0
 *
 * This program outputs a copy of itself if register A is instead initialized to 117440. (The original initial value of
 * register A, 2024, is ignored.)
 * What is the lowest positive initial value for register A that causes the program to output a copy of itself?
 *
 */
data class ChronospatialComputer(
    var registerA: Long,
    var registerB: Long = 0,
    var registerC: Long = 0,
    var instructionPointer: Int = 0,

    val program: List<Int>,
    val output: MutableList<Int> = mutableListOf()
) {
    companion object {
        fun of(input: String): ChronospatialComputer {
            val lines = input.trim().lines()
            val registerRegex = "Register .: (\\d+)".toRegex()
            fun String.findRegisterValue() = registerRegex.matchEntire(this)?.groupValues?.get(1)?.toLong()
                ?: throw IllegalArgumentException("Unable to parse register line $this")

            val registerA = lines[0].findRegisterValue()
            val registerB = lines[1].findRegisterValue()
            val registerC = lines[2].findRegisterValue()

            val program = lines.last()
                .substringAfterLast(" ")
                .split(",")
                .map { it.toInt() }

            return ChronospatialComputer(registerA, registerB, registerC, program = program)
        }
    }

    enum class Instruction(val opcode: Int, val operandType: OperandType) {
        DIVISION(0, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Long) {
                // This is 2^operand value.
                val denominator = 1 shl operandValue.toInt()
                val result = computer.registerA / denominator
                computer.registerA = result
            }
        },
        BITWISE_OR(1, OperandType.LITERAL) {
            override fun execute(computer: ChronospatialComputer, operandValue: Long) {
                computer.registerB = computer.registerB xor operandValue
            }
        },
        WRITE_TO_B(2, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Long) {
                computer.registerB = operandValue % 8
            }
        },
        JUMP_IF_NONZERO(3, OperandType.LITERAL) {
            override fun execute(computer: ChronospatialComputer, operandValue: Long) = Unit
            override fun nextInstructionPointerValue(
                computer: ChronospatialComputer,
                operandValue: Long,
                currentInstructionPointerValue: Int
            ) = if (computer.registerA == 0L) (currentInstructionPointerValue + 2) else operandValue.toInt()
        },
        BITWISE_XOR(4, OperandType.LITERAL) {
            override fun execute(computer: ChronospatialComputer, operandValue: Long) {
                computer.registerB = computer.registerB xor computer.registerC
            }
        },
        OUTPUT(5, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Long) {
                computer.output.add((operandValue % 8).toInt())
            }
        },
        DIVISION_TO_B(6, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Long) {
                val denominator = 1 shl operandValue.toInt()
                val result = computer.registerA / denominator
                computer.registerB = result
            }
        },
        DIVISION_TO_C(7, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Long) {
                val denominator = 1 shl operandValue.toInt()
                val result = computer.registerA / denominator
                computer.registerC = result
            }
        };

        companion object {
            private val instructionsByOpcode: Map<Int, Instruction> = Instruction.values().associateBy { it.opcode }
            fun of(opcode: Int) =
                instructionsByOpcode[opcode] ?: throw IllegalArgumentException("No instruction with code $opcode")
        }

        abstract fun execute(computer: ChronospatialComputer, operandValue: Long)
        open fun nextInstructionPointerValue(
            computer: ChronospatialComputer,
            operandValue: Long,
            currentInstructionPointerValue: Int
        ) =
            currentInstructionPointerValue + 2
    }

    enum class OperandType { LITERAL, COMBO }

    private fun comboOperandValue(number: Int): Long = when (number) {
        in 0..3 -> number.toLong()
        4 -> registerA
        5 -> registerB
        6 -> registerC
        else -> throw IllegalArgumentException("Illegal combo operand value $number")
    }

    tailrec fun execute(haltIfOutputCannotBeProgram: Boolean = false) {
        if (instructionPointer < 0 || instructionPointer >= program.size - 1) return
        if (haltIfOutputCannotBeProgram && program.slice(output.indices) != output) return

        val instruction = Instruction.of(program[instructionPointer])
        val operandNumber = program[instructionPointer + 1]

        val operandValue = when (instruction.operandType) {
            OperandType.LITERAL -> operandNumber.toLong()
            OperandType.COMBO -> comboOperandValue(operandNumber)
        }

        instruction.execute(this, operandValue)
        instructionPointer = instruction.nextInstructionPointerValue(this, operandValue, instructionPointer)

        execute(haltIfOutputCannotBeProgram)
    }

    /**
     * Returns the lowest value for register A for which this outputs its own program.
     *
     * This works great for the example, but for the real input it actually iterates all the way up through MAX_INT!
     * So, we are going to have to be a bit smarter and actually comprehend the program being run.
     */
    fun quineValue(): Long = generateSequence(0L) { it + 1 }
        .first { initialA ->
            if (initialA % 1_000_000 == 0L) println(initialA)
            val computer = this.copy(registerA = initialA, output = mutableListOf())
            computer.execute(haltIfOutputCannotBeProgram = true)
            computer.output == computer.program
        }

    /**
     * Solve the quine problem, not as a general solution, but for the exact program in our input.
     *
     * Our program is:
     *
     * 2,4,1,1,7,5,0,3,1,4,4,0,5,5,3,0
     * or split up
     * 2,4
     * 1,1
     * 7,5
     * 0,3
     * 1,4
     * 4,0
     * 5,5
     * 3,0
     *
     * That's:
     * A % 8 -> B
     * XOR B with 1 -> B
     * A / 2^B -> C
     * A / 8 -> A
     * XOR B with 4 -> B
     * XOR B with C -> B
     * Output B % 8
     * If A is non-zero, return to the beginning; else halt
     *
     * Notes:
     * A is only ever mutated by dividing by 8, once each loop (i.e., discarding the 3 rightmost binary digits).
     * The values of B and C are not preserved in each loop (they are reset before they are read).
     * Only the lowest 3 binary digits of C ever matter, because we XOR it with B and then output B % 8.
     * Since we are looking to output 16 values, our input is going to be a 48-bit integer (i.e., no wonder we didn't
     * find it up until Int.MAX_VALUE, it is much larger than that).
     * Can we treat A solely as groups of 3 binary digits? It looks like that pesky A / 2^B -> C "crosses the boundary"
     * between groups. So we are going to have to deal with individual binary digits.
     *
     * Even though we can't treat A solely as groups of binary digits, if we start from the LEFT, we will be able to
     * pick out individual groups of 3, and our C values will always be calculable from what we already have!
     * So we can guess-and-check each group of 3 binary digits to see if the calculation matches up with the value
     * that has to be in the output.
     *
     * We'll check inputs, starting with an empty block of input. Then we'll try successive groups of three
     * digits (i.e., 0 to 7), and when it matches the end of the output, we'll use it (i.e., multiply by 8)
     * and try new groups.
     */
    fun reverseEngineeredQuineValue(): Long {
        return findQuineValue(null) ?: throw IllegalStateException("Couldn't find a value!")
    }

    private fun findQuineValue(a: Long?): Long? {
        val result = a?.let(::run)
        val target = program.reversed().take(result?.size ?: 0)
        return when {
            result == program -> a
            (result == null || result == target.reversed()) -> (0..7).asSequence()
                .map { threeDigits -> findQuineValue(8 * (a ?: 0) + threeDigits) }
                .filterNotNull()
                .firstOrNull()

            else -> null
        }
    }

    private fun run(initialA: Long): List<Int> {
        val copy = this.copy(registerA = initialA, output = mutableListOf())
        copy.execute()
        return copy.output
    }
}

fun main() {
    val originalComputer = ChronospatialComputer.of(readInput())

    val computer = originalComputer.copy()
    computer.execute()

    println(computer.output.joinToString(","))
    println(originalComputer.reverseEngineeredQuineValue())
}