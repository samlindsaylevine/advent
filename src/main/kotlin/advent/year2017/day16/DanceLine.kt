package advent.year2017.day16

import java.io.File

/**
 * --- Day 16: Permutation Promenade ---
 * You come upon a very unusual sight; a group of programs here appear to be dancing.
 * There are sixteen programs in total, named a through p. They start by standing in a line: a stands in position 0, b
 * stands in position 1, and so on until p, which stands in position 15.
 * The programs' dance consists of a sequence of dance moves:
 * 
 * Spin, written sX, makes X programs move from the end to the front, but maintain their order otherwise. (For example,
 * s3 on abcde produces cdeab).
 * Exchange, written xA/B, makes the programs at positions A and B swap places.
 * Partner, written pA/B, makes the programs named A and B swap places.
 * 
 * For example, with only five programs standing in a line (abcde), they could do the following dance:
 * 
 * s1, a spin of size 1: eabcd.
 * x3/4, swapping the last two programs: eabdc.
 * pe/b, swapping programs e and b: baedc.
 * 
 * After finishing their dance, the programs end up in order baedc.
 * You watch the dance for a while and record their dance moves (your puzzle input). In what order are the programs
 * standing after their dance?
 * 
 * --- Part Two ---
 * Now that you're starting to get a feel for the dance moves, you turn your attention to the dance as a whole.
 * Keeping the positions they ended up in from their previous dance, the programs perform it again and again: including
 * the first dance, a total of one billion (1000000000) times.
 * In the example above, their second dance would begin with the order baedc, and use the same dance moves:
 * 
 * s1, a spin of size 1: cbaed.
 * x3/4, swapping the last two programs: cbade.
 * pe/b, swapping programs e and b: ceadb.
 * 
 * In what order are the programs standing after their billion dances?
 * 
 */
data class DanceLine(val order: String) {

    constructor(count: Int) : this((0 until count).map { 'a' + it }.joinToString(separator = ""))

    fun spin(count: Int): DanceLine = DanceLine(order.substring(order.length - count) +
            order.substring(0, order.length - count))

    fun exchange(posA: Int, posB: Int): DanceLine {
        val min = Math.min(posA, posB)
        val max = Math.max(posA, posB)

        val str = order.substring(0, min) +
                order[max] +
                order.substring(min + 1, max) +
                order[min] +
                order.substring(max + 1)

        return DanceLine(str)
    }

    fun partner(nameA: Char, nameB: Char): DanceLine = exchange(order.indexOf(nameA), order.indexOf(nameB))

    fun dance(moves: List<String>): DanceLine = danceMoves(moves.map { DanceMove(it) })

    private fun danceMoves(moves: List<DanceMove>) = moves.fold(this, { line, move -> line.dance(move) })

    private fun dance(move: DanceMove): DanceLine = move.action(this)

    fun dance(moveStrings: List<String>, times: Int): DanceLine {
        val moves = moveStrings.map { DanceMove(it) }

        // The trick here is that there is some cycle in which the order repeats, so we have to proceed forward until
        // we see the first ordering that we have seen before, then calculate how many more cycles our input would
        // have gone through, and where in the cycle the final result will be.

        val seen = mutableListOf(this)
        var current = this

        for (i in 1..times) {
            current = current.danceMoves(moves)
            val existingIndexOfCurrent = seen.indexOf(current)
            if (existingIndexOfCurrent >= 0) {
                // We hit a cycle! We can short-circuit to the end now.
                val cycleLength = i - existingIndexOfCurrent
                val targetIndex = (times - existingIndexOfCurrent) % cycleLength + existingIndexOfCurrent
                return seen[targetIndex]
            }
            seen.add(current)
        }

        return current
    }

    /**
     * We map the strings into objects so that we only have to do regex parsing once, and keep the action around
     * after that, even if we are applying the same move thousands of times.
     */
    class DanceMove(move: String) {
        val action: (DanceLine) -> DanceLine = actionLambda(move)

        companion object {
            private val SPIN_REGEX = "s(\\d+)".toRegex()
            private val EXCHANGE_REGEX = "x(\\d+)/(\\d+)".toRegex()
            private val PARTNER_REGEX = "p(\\w+)/(\\w+)".toRegex()

            private fun actionLambda(move: String): (DanceLine) -> DanceLine {
                SPIN_REGEX.matchEntire(move)?.let { match ->
                    return { line -> line.spin(match.groupValues[1].toInt()) }
                }

                EXCHANGE_REGEX.matchEntire(move)?.let { match ->
                    return { line -> line.exchange(match.groupValues[1].toInt(), match.groupValues[2].toInt()) }
                }

                PARTNER_REGEX.matchEntire(move)?.let { match ->
                    return { line -> line.partner(match.groupValues[1].first(), match.groupValues[2].first()) }
                }

                throw IllegalArgumentException("Unrecognized move $move")
            }
        }
    }


}

fun main() {
    val line = DanceLine(16)

    val input = File("src/main/kotlin/advent/year2017/day16/input.txt")
            .readText()
            .trim()
            .split(",")

    val result = line.dance(input)

    println(result.order)

    val oneBillion = line.dance(input, 1_000_000_000)

    println(oneBillion.order)
}