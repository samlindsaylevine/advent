package advent.year2017.day16

import java.io.File

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

fun main(args: Array<String>) {
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