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
}