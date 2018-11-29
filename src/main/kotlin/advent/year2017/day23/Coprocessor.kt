package advent.year2017.day23

import java.io.File

class Coprocessor {
    private val registers: MutableMap<String, Long> = mutableMapOf()

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

        while (index >= 0 && index < instructions.size) {
            val instruction = instructions[index]
            if (instruction is Multiply) numMultiplications++
            index += instruction.execute(this)
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

    private class Multiply(val x: String, val y: String) : CoprocessorInstruction {
        override fun execute(coprocessor: Coprocessor): Int {
            coprocessor.set(x, coprocessor.get(x) * coprocessor.get(y))
            return 1
        }
    }

    private class JumpConditional(val x: String, val y: String) : CoprocessorInstruction {
        override fun execute(coprocessor: Coprocessor): Int {
            return if (coprocessor.get(x) != 0L) coprocessor.get(y).toInt() else 1

        }
    }
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day23/input.txt")
            .readText()
            .trim()

    val coprocessor = Coprocessor()

    val multiplies = coprocessor.execute(input.split("\n"))
    println(multiplies)
}