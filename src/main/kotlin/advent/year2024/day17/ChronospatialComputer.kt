package advent.year2024.day17

import advent.meta.readInput

data class ChronospatialComputer(
    var registerA: Int,
    var registerB: Int = 0,
    var registerC: Int = 0,
    var instructionPointer: Int = 0,

    val program: List<Int>,
    val output: MutableList<Int> = mutableListOf()
) {
    companion object {
        fun of(input: String): ChronospatialComputer {
            val lines = input.trim().lines()
            val registerRegex = "Register .: (\\d+)".toRegex()
            fun String.findRegisterValue() = registerRegex.matchEntire(this)?.groupValues?.get(1)?.toInt()
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
            override fun execute(computer: ChronospatialComputer, operandValue: Int) {
                // This is 2^operand value.
                val denominator = 1 shl operandValue
                val result = computer.registerA / denominator
                computer.registerA = result
            }
        },
        BITWISE_OR(1, OperandType.LITERAL) {
            override fun execute(computer: ChronospatialComputer, operandValue: Int) {
                computer.registerB = computer.registerB xor operandValue
            }
        },
        WRITE_TO_B(2, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Int) {
                computer.registerB = operandValue % 8
            }
        },
        JUMP_IF_NONZERO(3, OperandType.LITERAL) {
            override fun execute(computer: ChronospatialComputer, operandValue: Int) = Unit
            override fun nextInstructionPointerValue(
                computer: ChronospatialComputer,
                operandValue: Int,
                currentInstructionPointerValue: Int
            ) = if (computer.registerA == 0) (currentInstructionPointerValue + 2) else operandValue
        },
        BITWISE_XOR(4, OperandType.LITERAL) {
            override fun execute(computer: ChronospatialComputer, operandValue: Int) {
                computer.registerB = computer.registerB xor computer.registerC
            }
        },
        OUTPUT(5, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Int) {
                computer.output.add(operandValue % 8)
            }
        },
        DIVISION_TO_B(6, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Int) {
                val denominator = 1 shl operandValue
                val result = computer.registerA / denominator
                computer.registerB = result
            }
        },
        DIVISION_TO_C(7, OperandType.COMBO) {
            override fun execute(computer: ChronospatialComputer, operandValue: Int) {
                val denominator = 1 shl operandValue
                val result = computer.registerA / denominator
                computer.registerC = result
            }
        };

        companion object {
            private val instructionsByOpcode: Map<Int, Instruction> = Instruction.values().associateBy { it.opcode }
            fun of(opcode: Int) =
                instructionsByOpcode[opcode] ?: throw IllegalArgumentException("No instruction with code $opcode")
        }

        abstract fun execute(computer: ChronospatialComputer, operandValue: Int)
        open fun nextInstructionPointerValue(
            computer: ChronospatialComputer,
            operandValue: Int,
            currentInstructionPointerValue: Int
        ) =
            currentInstructionPointerValue + 2
    }

    enum class OperandType { LITERAL, COMBO }

    private fun comboOperandValue(number: Int): Int = when (number) {
        in 0..3 -> number
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
            OperandType.LITERAL -> operandNumber
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
    fun quineValue(): Int = generateSequence(0) { it + 1 }
        .first { initialA ->
            if (initialA % 1_000_000 == 0) println(initialA)
            val computer = this.copy(registerA = initialA, output = mutableListOf())
            computer.execute(haltIfOutputCannotBeProgram = true)
            computer.output == computer.program
        }
}

fun main() {
    val originalComputer = ChronospatialComputer.of(readInput())

    val computer = originalComputer.copy()
    computer.execute()

    println(computer.output.joinToString(","))
    println(originalComputer.quineValue())
}