package advent.year2017.day3

import kotlin.coroutines.experimental.buildSequence

class SpiralMemory {

    /**
     * For the first part, returns the position of the given number as x, y coordinates.
     * By convention square 1 is 0, 0.
     */
    fun position(squareNumber: Int): Pair<Int, Int> {
        /**
         * It is possible to calculate this in O(1) time, so we are going to do that rather than iterate up to
         * squareNumber. It does require some fairly complicated formulae worked out at http://archive.is/6yiGd
         * See https://github.com/JendaPlhak/math_in_python/blob/master/JendasWork/1_task/spirals.py for an example
         * of implementing the same algorithm.
         *
         * Note that this is kind of gold-plating the solution - I wrote my own whole (integer coefficient) complex
         * class - but it was fun, and the result is actually positive from an end-user point of view, because it's O(1)
         * not O(N).
         */

        // The original algorithm was solved for starting at 0, not 1.
        val n = squareNumber - 1

        // This represents which number line segment we are on.
        val p = Math.floor(Math.sqrt((4 * n + 1).toDouble())).toInt()

        // This represents which point on that line segment we are on.
        val q = n - ((p * p) / 4)

        val z = q * Complex.i.pow(p) + Complex((p + 2) / 4, -(p + 1) / 4) * Complex.i.pow(p - 1)

        // This is the solution that starts by going up. We want the solution that starts by going to the right - that's
        // a multiplication by -i.
        val result = z * -Complex.i

        return Pair(result.real, result.complex)
    }

    fun distanceFromOrigin(squareNumber: Int): Int {
        val position = position(squareNumber)
        return Math.abs(position.first) + Math.abs(position.second)
    }

    /**
     * Let's try out Kotlin's coroutines for building sequences!
     *
     * Seems like a good project for testing out experimental features.
     *
     * This is an infinite sequence of all the stress test values.
     */
    fun stressTestValues(): Sequence<Int> = buildSequence {
        val grid = mutableMapOf<Pair<Int, Int>, Int>()
        grid.put(Pair(0, 0), 1)
        var currentSquare = 1
        yield(1)

        while (true) {
            currentSquare++
            val currentPosition = position(currentSquare)
            val newValue = neighbors(currentPosition).mapNotNull { grid[it] }.sum()
            grid.put(currentPosition, newValue)
            yield(newValue)
        }
    }

    private fun neighbors(position: Pair<Int, Int>): Set<Pair<Int, Int>> = setOf(
            Pair(position.first - 1, position.second + 1),
            Pair(position.first, position.second + 1),
            Pair(position.first + 1, position.second + 1),
            Pair(position.first - 1, position.second),
            Pair(position.first + 1, position.second),
            Pair(position.first - 1, position.second - 1),
            Pair(position.first, position.second - 1),
            Pair(position.first + 1, position.second - 1))

}

fun main(args: Array<String>) {
    val input = 277678
    println(SpiralMemory().distanceFromOrigin(input))
    println(SpiralMemory().stressTestValues().find { it > input })
}