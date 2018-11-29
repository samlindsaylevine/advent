package advent.year2017.day23

import java.io.File

class Coprocessor(private val registers: MutableMap<String, Long> = mutableMapOf()) {

    fun get(name: String): Long {
        return try {
            name.toLong()
        } catch (e: NumberFormatException) {
            registers.computeIfAbsent(name) { 0L }
        }
    }

    fun set(name: String, value: Long) {
        registers[name] = value
    }

    /**
     * Returns the number of multiplications executed.
     */
    fun execute(instructionStrings: List<String>): Int {
        val instructions = instructionStrings.map { CoprocessorInstruction.parse(it) }
        var index = 0
        var numMultiplications = 0
        var steps = 0

        while (index >= 0 && index < instructions.size) {
            val instruction = instructions[index]
            if (instruction is Multiply) numMultiplications++
            index += instruction.execute(this)

            steps++
            if (steps % 1_000_000 == 0) println(steps)
        }

        return numMultiplications
    }

    private interface CoprocessorInstruction {
        companion object {
            private val SET_REGEX = "set (\\w+) (-?\\w+)".toRegex()
            private val SUB_REGEX = "sub (\\w+) (-?\\w+)".toRegex()
            private val MUL_REGEX = "mul (\\w+) (-?\\w+)".toRegex()
            private val JNZ_REGEX = "jnz (\\w+) (-?\\w+)".toRegex()

            fun parse(input: String): CoprocessorInstruction {
                SET_REGEX.matchEntire(input)?.let { match ->
                    return Set(match.groupValues[1], match.groupValues[2])
                }

                SUB_REGEX.matchEntire(input)?.let { match ->
                    return Subtract(match.groupValues[1], match.groupValues[2])
                }

                MUL_REGEX.matchEntire(input)?.let { match ->
                    return Multiply(match.groupValues[1], match.groupValues[2])
                }

                JNZ_REGEX.matchEntire(input)?.let { match ->
                    return JumpConditional(match.groupValues[1], match.groupValues[2])
                }

                throw IllegalArgumentException("Unparseable instruction $input")
            }
        }

        /**
         * Returns how many steps to advance forward or backward.
         */
        fun execute(coprocessor: Coprocessor): Int
    }

    private data class Set(val x: String, val y: String) : CoprocessorInstruction {
        override fun execute(coprocessor: Coprocessor): Int {
            coprocessor.set(x, coprocessor.get(y))
            return 1
        }
    }

    private data class Subtract(val x: String, val y: String) : CoprocessorInstruction {
        override fun execute(coprocessor: Coprocessor): Int {
            coprocessor.set(x, coprocessor.get(x) - coprocessor.get(y))
            return 1
        }
    }

    private data class Multiply(val x: String, val y: String) : CoprocessorInstruction {
        override fun execute(coprocessor: Coprocessor): Int {
            coprocessor.set(x, coprocessor.get(x) * coprocessor.get(y))
            return 1
        }
    }

    private data class JumpConditional(val x: String, val y: String) : CoprocessorInstruction {
        override fun execute(coprocessor: Coprocessor): Int {
            return if (coprocessor.get(x) != 0L) coprocessor.get(y).toInt() else 1

        }
    }
}

fun Int.isComposite(): Boolean {
    val possibleDivisors = (sequenceOf(2) + generateSequence(3) { it + 2 })
            .takeWhile { it * it <= this }

    return possibleDivisors.any { this % it == 0 }
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day23/input.txt")
            .readText()
            .trim()
            .split("\n")

    val coprocessor = Coprocessor()
    val multiplies = coprocessor.execute(input)
    println(multiplies)

    // See annotated input.txt for an explanation of the program and why this is equivalent to its output.
    val output = generateSequence(109300) { it + 17 }
            .takeWhile { it <= 126300 }
            .count { it.isComposite() }
    println(output)

}