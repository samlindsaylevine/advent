package advent.year2024.day17

import advent.meta.readInput

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