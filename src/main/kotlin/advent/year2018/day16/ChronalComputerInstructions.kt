package advent.year2018.day16

import java.io.File

enum class ChronalOpCode {

    addr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] + registers[inputB]
        }
    },

    addi {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] + inputB
        }
    },

    mulr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] * registers[inputB]
        }
    },

    muli {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] * inputB
        }
    },

    banr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] and registers[inputB]
        }
    },

    bani {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] and inputB
        }
    },

    borr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] or registers[inputB]
        }
    },

    bori {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] or inputB
        }
    },

    setr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA]
        }
    },

    seti {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = inputA
        }
    },

    gtir {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (inputA > registers[inputB]) 1 else 0
        }
    },

    gtri {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (registers[inputA] > inputB) 1 else 0
        }
    },

    gtrr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (registers[inputA] > registers[inputB]) 1 else 0
        }
    },

    eqir {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (inputA == registers[inputB]) 1 else 0
        }
    },

    eqri {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (registers[inputA] == inputB) 1 else 0
        }
    },

    eqrr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (registers[inputA] == registers[inputB]) 1 else 0
        }
    };

    abstract fun execute(registers: MutableList<Int>,
                         inputA: Int,
                         inputB: Int,
                         output: Int)
}

data class Instruction(val opCode: Int,
                       val inputA: Int,
                       val inputB: Int,
                       val output: Int) {
    companion object {
        fun parse(input: String): Instruction {
            val numbers = input.split(" ").map { it.toInt() }
            return Instruction(numbers[0], numbers[1], numbers[2], numbers[3])
        }
    }

    fun execute(registers: MutableList<Int>, codeNumberToCodes: Map<Int, ChronalOpCode>) {
        val op = codeNumberToCodes[opCode] ?: throw IllegalArgumentException("Missing op code $opCode from solution")
        op.execute(registers, inputA, inputB, output)
    }
}

data class SampleComputation(val beforeRegisters: List<Int>,
                             val afterRegisters: List<Int>,
                             val instruction: Instruction) {
    companion object {

        fun parse(input: String): SampleComputation {
            val lines = input.split("\n")
            val beforeRegisters = lines[0].substringBetween('[', ']').split(", ").map { it.toInt() }
            val afterRegisters = lines[2].substringBetween('[', ']').split(", ").map { it.toInt() }

            return SampleComputation(beforeRegisters,
                    afterRegisters,
                    Instruction.parse(lines[1]))
        }

        private fun String.substringBetween(start: Char, end: Char): String {
            val startIndex = this.indexOf(start)
            val endIndex = this.indexOf(end, startIndex = startIndex)
            return this.substring(startIndex + 1, endIndex)
        }
    }

    fun behavesLike(opCode: ChronalOpCode): Boolean {
        val registers = beforeRegisters.toMutableList()
        opCode.execute(registers, instruction.inputA, instruction.inputB, instruction.output)

        return registers == afterRegisters
    }

    fun matchingOpCodes() = ChronalOpCode.values().filter { this.behavesLike(it) }.toSet()

    fun behavesLikeCount() = matchingOpCodes().size
}

/**
 * Given a list of computations all for the same op code number, find the possible op codes that number could have.
 */
private fun List<SampleComputation>.possibleOpCodes() = this.map { it.matchingOpCodes() }
        .reduce { acc, possibilities -> acc.intersect(possibilities) }

private fun List<SampleComputation>.codeNumbersToCodes(): Map<Int, ChronalOpCode> {
    val samplesByCodeNumber: Map<Int, List<SampleComputation>> = this.groupBy { it.instruction.opCode }
    val codeNumberToCodes = samplesByCodeNumber.mapValues { it.value.possibleOpCodes() }
    return solveMapping(codeNumberToCodes)
}

/**
 * Given a bunch of possibilities for each number code, figure out what the unique solution is. (This makes casual
 * assumptions that at each step there will be a number code that has only one option and we can reduce from there.
 * This assumption does hold for my input.)
 */
private fun solveMapping(codeNumberToCodes: Map<Int, Collection<ChronalOpCode>>) =
        solveMapping(codeNumberToCodes, emptyMap())

private tailrec fun solveMapping(codeNumberToCodes: Map<Int, Collection<ChronalOpCode>>,
                                 solvedCodes: Map<Int, ChronalOpCode>): Map<Int, ChronalOpCode> {
    return if (codeNumberToCodes.isEmpty()) {
        solvedCodes
    } else {
        // Find the number that has only one possible opcode.
        val (number, singleOp) = codeNumberToCodes.entries.first { it.value.size == 1 }
        val op = singleOp.first()

        // Add that number to the solution, and remove it from the unsolved numbers. (Both as a key, and in the value
        // lists.)
        solveMapping(codeNumberToCodes.filter { it.key != number }.mapValues { (_, codes) -> codes.filter { it != op } },
                solvedCodes + (number to op))
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day16/input.txt")
            .readText()

    val parts = input.split("\n\n\n\n")

    val samples = parts[0].split("\n\n").map { SampleComputation.parse(it) }
    val testProgram = parts[1].trim().split("\n").map { Instruction.parse(it) }

    val behavesLikeThreeCount = samples.count { it.behavesLikeCount() >= 3 }
    println("Samples that behave like three or more opcodes: $behavesLikeThreeCount")

    val registers = mutableListOf(0, 0, 0, 0)
    testProgram.forEach { it.execute(registers, samples.codeNumbersToCodes()) }
    println("Register 0: ${registers[0]}")
}