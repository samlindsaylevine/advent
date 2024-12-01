package advent.year2023.day21

import advent.utils.Point
import advent.utils.Rational
import advent.utils.times
import java.io.File
import java.lang.Math.floorMod

class GardenMap(
    val start: Point,
    val width: Int,
    val height: Int,
    private val rocks: Set<Point>,
    val infinite: Boolean
) {
    companion object {
        fun of(input: String, infinite: Boolean = false): GardenMap {
            val lines = input.trim().lines()
            val pointToChar: Map<Point, Char> = lines.flatMapIndexed { y, line ->
                line.mapIndexed { x, c -> Point(x, y) to c }
            }.toMap()
            val start = pointToChar.entries.first { it.value == 'S' }.key
            val width = lines.maxOf { it.length }
            val height = lines.size
            val rocks = pointToChar.entries.filter { it.value == '#' }.map { it.key }.toSet()
            return GardenMap(start, width, height, rocks, infinite)
        }
    }

    // Had to optimize this and only check new points on the perimeter, instead of all points, in order to calculate
    // fast enough.
    data class GardenTraversal(
        val thisParityInteriorCount: Int,
        val oppositeParityInteriorCount: Int,
        val previousPerimeter: Set<Point>,
        val perimeter: Set<Point>
    ) {
        fun size() = thisParityInteriorCount + perimeter.size
    }

    fun reachable(): Sequence<GardenTraversal> =
        generateSequence(GardenTraversal(0, 0, emptySet(), setOf(start))) { next(it) }

    private fun next(traversal: GardenTraversal): GardenTraversal {
        val nextPerimeter = traversal.perimeter
            .flatMap { it.adjacentNeighbors }
            .filter { it.wrapped() !in rocks && it !in traversal.previousPerimeter }
            .toSet()

        return GardenTraversal(
            thisParityInteriorCount = traversal.oppositeParityInteriorCount + traversal.previousPerimeter.size,
            oppositeParityInteriorCount = traversal.thisParityInteriorCount,
            previousPerimeter = traversal.perimeter,
            perimeter = nextPerimeter
        )
    }

    private fun Point.wrapped() = Point(floorMod(this.x, width), floorMod(this.y, height))

    fun countAfter(numSteps: Int) = reachable().drop(numSteps).first().size().toLong()

    /**
     * My first attempt was to look for a linear recurrence, but that did not work. Then I thought that since we are
     * more or less expanding the area of a square, we should expect the values to go like a quadratic polynomial.
     * Taking a bunch of data points, I searched for a quadratic recurrence, where every N values, we saw them increase
     * as a quadratic. I found recurrences in my real input file of period 131 (which, hmmm, was the same as the width).
     * So, we can take our number of steps, reduce mod down to the appropriate start, find its polynomial, and then
     * evaluate.
     */
    fun countByRecurrence(numSteps: Int, loadDataFromFile: Boolean = false): Long {
        val guessWhenLoopsStart = 50

        val sizes = if (loadDataFromFile) loadDatapoints() else reachable().map { it.size() }.take(100).toList()

        fun datapoint(index: Int) = Point(index, sizes[index])

        val period = (1 until (sizes.size - guessWhenLoopsStart) / 3).firstOrNull { possiblePeriod ->
            val datapoints = listOf(
                datapoint(guessWhenLoopsStart),
                datapoint(guessWhenLoopsStart + possiblePeriod),
                datapoint(guessWhenLoopsStart + 2 * possiblePeriod)
            )
            val quadratic = Quadratic.fit(datapoints[0], datapoints[1], datapoints[2])
            quadratic != null && quadratic.matches(datapoint(guessWhenLoopsStart + 3 * possiblePeriod))
        } ?: throw IllegalStateException("No quadratic recurrence found")

        val appropriateStart = floorMod(numSteps, period)

        val quadratic = Quadratic.fit(
            datapoint(appropriateStart),
            datapoint(appropriateStart + period),
            datapoint(appropriateStart + 2 * period)
        )
            ?: throw IllegalStateException("Can't fit polynomial starting at index $appropriateStart with period $period")

        if (!quadratic.matches(datapoint(appropriateStart + 3 * period))) {
            throw IllegalStateException("Quadratic from $appropriateStart with period $period doesn't extrapolate!")
        }
        return quadratic.invoke(numSteps.toLong())
    }

    @Suppress("unused")
    fun saveDatapoints() {
        val file = File("src/main/kotlin/advent/year2023/day21/datapoints")
        file.printWriter().use { out ->
            reachable().map { it.size() }.take(width * 4).withIndex().forEach { (index, size) ->
                println("$index: $size")
                out.println(size)
            }
        }
    }
}

/**
 * We calculated sizes and saved them to a file for faster reuse.
 */
fun loadDatapoints(): List<Int> = File("src/main/kotlin/advent/year2023/day21/datapoints")
    .readLines()
    .map { it.toInt() }


data class Quadratic(val a: Rational, val b: Rational, val c: Rational) {
    companion object {
        fun fit(p1: Point, p2: Point, p3: Point): Quadratic? {
            // From https://stackoverflow.com/a/717791.
            val x1 = p1.x.toLong()
            val y1 = p1.y.toLong()
            val x2 = p2.x.toLong()
            val y2 = p2.y.toLong()
            val x3 = p3.x.toLong()
            val y3 = p3.y.toLong()

            val denom = (x1 - x2) * (x1 - x3) * (x2 - x3)
            val a = Rational.of(x3 * (y2 - y1) + x2 * (y1 - y3) + x1 * (y3 - y2), denom)
            val b = Rational.of(x3 * x3 * (y1 - y2) + x2 * x2 * (y3 - y1) + x1 * x1 * (y2 - y3), denom)
            val c = Rational.of(x2 * x3 * (x2 - x3) * y1 + x3 * x1 * (x3 - x1) * y2 + x1 * x2 * (x1 - x2) * y3, denom)

            val quadratic = Quadratic(a, b, c)

            return if (quadratic.matches(p1) && quadratic.matches(p2) && quadratic.matches(p3)) quadratic else null
        }
    }

    constructor(a: Long, b: Long, c: Long) : this(
        Rational.of(a, 1),
        Rational.of(b, 1),
        Rational.of(c, 1)
    )

    operator fun invoke(x: Long): Long = ((x * x) * a + x * b + c).toLong()
    fun matches(point: Point) = this(point.x.toLong()) == point.y.toLong()
}

fun main() {
    val input = File("src/main/kotlin/advent/year2023/day21/input.txt").readText().trim()
    val map = GardenMap.of(input)
    // map.saveDatapoints()

    println(map.countAfter(64))
    println(map.countByRecurrence(26501365, loadDataFromFile = true))
}