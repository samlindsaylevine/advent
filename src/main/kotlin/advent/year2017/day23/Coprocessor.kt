package advent.year2017.day23

import java.io.File

/**
 * --- Day 23: Coprocessor Conflagration ---
 * You decide to head directly to the CPU and fix the printer from there. As you get close, you find an experimental
 * coprocessor doing so much work that the local programs are afraid it will halt and catch fire. This would cause
 * serious issues for the rest of the computer, so you head in and see what you can do.
 * The code it's running seems to be a variant of the kind you saw recently on that tablet. The general functionality
 * seems very similar, but some of the instructions are different:
 * 
 * set X Y sets register X to the value of Y.
 * sub X Y decreases register X by the value of Y.
 * mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
 * jnz X Y jumps with an offset of the value of Y, but only if the value of X is not zero. (An offset of 2 skips the
 * next instruction, an offset of -1 jumps to the previous instruction, and so on.)
 * Only the instructions listed above are used. The eight registers here, named a through h, all start at 0.
 * 
 * The coprocessor is currently set to some kind of debug mode, which allows for testing, but prevents it from doing
 * any meaningful work.
 * If you run the program (your puzzle input), how many times is the mul instruction invoked?
 * 
 * --- Part Two ---
 * Now, it's time to fix the problem.
 * The debug mode switch is wired directly to register a.  You flip the switch, which makes register a now start at 1
 * when the program is executed.
 * Immediately, the coprocessor begins to overheat.  Whoever wrote this program obviously didn't choose a very
 * efficient implementation.  You'll need to optimize the program if it has any hope of completing before Santa needs
 * that printer working.
 * The coprocessor's ultimate goal is to determine the final value left in register h once the program completes.
 * Technically, if it had that... it wouldn't even need to run the program.
 * After setting register a to 1, if the program were to run to completion, what value would be left in register h?
 * 
 */
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

fun main() {
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