package advent.year2018.day16

import java.io.File

/**
 * --- Day 16: Chronal Classification ---
 * As you see the Elves defend their hot chocolate successfully, you go back to falling through time. This is going to
 * become a problem.
 * If you're ever going to return to your own time, you need to understand how this device on your wrist works. You
 * have a little while before you reach your next destination, and with a bit of trial and error, you manage to pull up
 * a programming manual on the device's tiny screen.
 * According to the manual, the device has four registers (numbered 0 through 3) that can be manipulated by
 * instructions containing one of 16 opcodes. The registers start with the value 0.
 * Every instruction consists of four values: an opcode, two inputs (named A and B), and an output (named C), in that
 * order. The opcode specifies the behavior of the instruction and how the inputs are interpreted. The output, C, is
 * always treated as a register.
 * In the opcode descriptions below, if something says "value A", it means to take the number given as A literally.
 * (This is also called an "immediate" value.) If something says "register A", it means to use the number given as A to
 * read from (or write to) the register with that number. So, if the opcode addi adds register A and value B, storing
 * the result in register C, and the instruction addi 0 7 3 is encountered, it would add 7 to the value contained by
 * register 0 and store the sum in register 3, never modifying registers 0, 1, or 2 in the process.
 * Many opcodes are similar except for how they interpret their arguments. The opcodes fall into seven general
 * categories:
 * Addition:
 * 
 * addr (add register) stores into register C the result of adding register A and register B.
 * addi (add immediate) stores into register C the result of adding register A and value B.
 * 
 * Multiplication:
 * 
 * mulr (multiply register) stores into register C the result of multiplying register A and register B.
 * muli (multiply immediate) stores into register C the result of multiplying register A and value B.
 * 
 * Bitwise AND:
 * 
 * banr (bitwise AND register) stores into register C the result of the bitwise AND of register A and register B.
 * bani (bitwise AND immediate) stores into register C the result of the bitwise AND of register A and value B.
 * 
 * Bitwise OR:
 * 
 * borr (bitwise OR register) stores into register C the result of the bitwise OR of register A and register B.
 * bori (bitwise OR immediate) stores into register C the result of the bitwise OR of register A and value B.
 * 
 * Assignment:
 * 
 * setr (set register) copies the contents of register A into register C. (Input B is ignored.)
 * seti (set immediate) stores value A into register C. (Input B is ignored.)
 * 
 * Greater-than testing:
 * 
 * gtir (greater-than immediate/register) sets register C to 1 if value A is greater than register B. Otherwise,
 * register C is set to 0.
 * gtri (greater-than register/immediate) sets register C to 1 if register A is greater than value B. Otherwise,
 * register C is set to 0.
 * gtrr (greater-than register/register) sets register C to 1 if register A is greater than register B. Otherwise,
 * register C is set to 0.
 * 
 * Equality testing:
 * 
 * eqir (equal immediate/register) sets register C to 1 if value A is equal to register B. Otherwise, register C is set
 * to 0.
 * eqri (equal register/immediate) sets register C to 1 if register A is equal to value B. Otherwise, register C is set
 * to 0.
 * eqrr (equal register/register) sets register C to 1 if register A is equal to register B. Otherwise, register C is
 * set to 0.
 * 
 * Unfortunately, while the manual gives the name of each opcode, it doesn't seem to indicate the number. However, you
 * can monitor the CPU to see the contents of the registers before and after instructions are executed to try to work
 * them out.  Each opcode has a number from 0 through 15, but the manual doesn't say which is which. For example,
 * suppose you capture the following sample:
 * Before: [3, 2, 1, 1]
 * 9 2 1 2
 * After:  [3, 2, 2, 1]
 * 
 * This sample shows the effect of the instruction 9 2 1 2 on the registers. Before the instruction is executed,
 * register 0 has value 3, register 1 has value 2, and registers 2 and 3 have value 1. After the instruction is
 * executed, register 2's value becomes 2.
 * The instruction itself, 9 2 1 2, means that opcode 9 was executed with A=2, B=1, and C=2. Opcode 9 could be any of
 * the 16 opcodes listed above, but only three of them behave in a way that would cause the result shown in the sample:
 * 
 * Opcode 9 could be mulr: register 2 (which has a value of 1) times register 1 (which has a value of 2) produces 2,
 * which matches the value stored in the output register, register 2.
 * Opcode 9 could be addi: register 2 (which has a value of 1) plus value 1 produces 2, which matches the value stored
 * in the output register, register 2.
 * Opcode 9 could be seti: value 2 matches the value stored in the output register, register 2; the number given for B
 * is irrelevant.
 * 
 * None of the other opcodes produce the result captured in the sample. Because of this, the sample above behaves like
 * three opcodes.
 * You collect many of these samples (the first section of your puzzle input). The manual also includes a small test
 * program (the second section of your puzzle input) - you can ignore it for now.
 * Ignoring the opcode numbers, how many samples in your puzzle input behave like three or more opcodes?
 * 
 * --- Part Two ---
 * Using the samples you collected, work out the number of each opcode and execute the test program (the second section
 * of your puzzle input).
 * What value is contained in register 0 after executing the test program?
 * 
 */
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