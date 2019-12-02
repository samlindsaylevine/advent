package advent.year2019.day2

import java.io.File

class IntcodeProgram(val values: List<Int>) {

    companion object {
        private const val ADD = 1
        private const val MULTIPLY = 2
        private const val HALT = 99
    }

    val noun = values[1]
    val verb = values[2]

    fun execute(): List<Int> {
        val state = values.toMutableList()
        var index = 0

        while (state[index] != HALT) {
            val opcode = state[index]
            val inputA = state[index + 1]
            val inputB = state[index + 2]
            val output = state[index + 3]

            when (opcode) {
                ADD -> state[output] = state[inputA] + state[inputB]
                MULTIPLY -> state[output] = state[inputA] * state[inputB]
                else -> throw IllegalArgumentException("Unknown opcode $opcode")
            }
            index += 4
        }

        return state
    }

    fun withInputs(noun: Int, verb: Int): IntcodeProgram {
        val modifiedValues = values.toMutableList()
        modifiedValues[1] = noun
        modifiedValues[2] = verb
        return IntcodeProgram(modifiedValues)
    }

    fun findInput(output: Int): IntcodeProgram =
            (0..99).asSequence()
                    .flatMap { noun ->
                        (0..99).asSequence().map { verb ->
                            this.withInputs(noun, verb)
                        }
                    }
                    .first { it.execute()[0] == output }

}

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day2/input.txt")
            .readText()
            .trim()
            .split(",")
            .map { it.toInt() }

    val program = IntcodeProgram(input)

    val result = program.withInputs(12, 2).execute()

    println(result[0])

    val desiredProgram = program.findInput(output = 19690720)

    println(100 * desiredProgram.noun + desiredProgram.verb)
}