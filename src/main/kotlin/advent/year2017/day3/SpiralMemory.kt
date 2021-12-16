package advent.year2017.day3

/**
 * --- Day 3: Spiral Memory ---
 * You come across an experimental new kind of memory stored on an infinite two-dimensional grid.
 * Each square on the grid is allocated in a spiral pattern starting at a location marked 1 and then counting up while
 * spiraling outward. For example, the first few squares are allocated like this:
 * 17  16  15  14  13
 * 18   5   4   3  12
 * 19   6   1   2  11
 * 20   7   8   9  10
 * 21  22  23---> ...
 * 
 * While this is very space-efficient (no squares are skipped), requested data must be carried back to square 1 (the
 * location of the only access port for this memory system) by programs that can only move up, down, left, or right.
 * They always take the shortest path: the Manhattan Distance between the location of the data and square 1.
 * For example:
 * 
 * Data from square 1 is carried 0 steps, since it's at the access port.
 * Data from square 12 is carried 3 steps, such as: down, left, left.
 * Data from square 23 is carried only 2 steps: up twice.
 * Data from square 1024 must be carried 31 steps.
 * 
 * How many steps are required to carry the data from the square identified in your puzzle input all the way to the
 * access port?
 * 
 * --- Part Two ---
 * As a stress test on the system, the programs here clear the grid and then store the value 1 in square 1. Then, in
 * the same allocation order as shown above, they store the sum of the values in all adjacent squares, including
 * diagonals.
 * So, the first few squares' values are chosen as follows:
 * 
 * Square 1 starts with the value 1.
 * Square 2 has only one adjacent filled square (with value 1), so it also stores 1.
 * Square 3 has both of the above squares as neighbors and stores the sum of their values, 2.
 * Square 4 has all three of the aforementioned squares as neighbors and stores the sum of their values, 4.
 * Square 5 only has the first and fourth squares as neighbors, so it gets the value 5.
 * 
 * Once a square is written, its value does not change. Therefore, the first few squares would receive the following
 * values:
 * 147  142  133  122   59
 * 304    5    4    2   57
 * 330   10    1    1   54
 * 351   11   23   25   26
 * 362  747  806--->   ...
 * 
 * What is the first value written that is larger than your puzzle input?
 * 
 */
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
    fun stressTestValues(): Sequence<Int> = sequence {
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

fun main() {
    val input = 277678
    println(SpiralMemory().distanceFromOrigin(input))
    println(SpiralMemory().stressTestValues().find { it > input })
}