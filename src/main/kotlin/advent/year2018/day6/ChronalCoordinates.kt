package advent.year2018.day6

import java.io.File

class ChronalCoordinates(private val coordinates: Set<Point>) {
    constructor(input: String) : this(input.trim().split("\n")
            .map { Point.parse(it) }
            .toSet())

    private val minX = coordinates.map { it.x }.min() ?: 0
    private val maxX = coordinates.map { it.x }.max() ?: 0
    private val minY = coordinates.map { it.y }.min() ?: 0
    private val maxY = coordinates.map { it.y }.max() ?: 0

    fun largestFiniteAreaSize(): Int? = pointsInBoundingBox.groupingBy { closestCoordinateTo(it) }
            .eachCount()
            .filterKeys { it in coordinatesWithFiniteArea }
            .maxBy { it.value }
            ?.value

    private fun closestCoordinateTo(point: Point): Point? {
        val closest = coordinates.allMinBy { point.distanceFrom(it) }
        return if (closest.size == 1) closest.first() else null
    }

    private val pointsInBoundingBox: Set<Point> = pointsInRectangle(minX, maxX, minY, maxY)

    private fun pointsInRectangle(minX: Int, maxX: Int, minY: Int, maxY: Int) = (minX until maxX).flatMap { x ->
        (minY until maxY).map { y -> Point(x, y) }
    }.toSet()

    private val pointsOnBoundary = pointsInBoundingBox.filter {
        it.x == minX ||
                it.x == maxX ||
                it.y == minY ||
                it.y == maxY
    }

    /**
     * If a coordinate is the closest to any point on the boundary, then it will have an infinite area of influence.
     */
    private val coordinatesWithFiniteArea = coordinates.filter { coordinate ->
        pointsOnBoundary.none { closestCoordinateTo(it) == coordinate }
    }

    fun areaWithinTotalDistance(distance: Int): Int {
        if (coordinates.isEmpty()) throw IllegalStateException("Undefined for 0 points - the entire plane?")

        // Nothing more than this far away can possibly match our total distance.
        val maxDistance = distance / coordinates.size

        val possiblePoints = pointsInRectangle(minX - maxDistance, maxX + maxDistance,
                minY - maxDistance, maxY + maxDistance)

        return possiblePoints.count { totalDistance(it) < distance }
    }

    fun totalDistance(point: Point) = coordinates.sumBy { it.distanceFrom(point) }
}

fun <T, R : Comparable<R>> Iterable<T>.allMinBy(function: (T) -> R): Set<T> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return emptySet()

    val first = iterator.next()
    val output = mutableSetOf(first)
    var minValue = function(first)

    while (iterator.hasNext()) {
        val next = iterator.next()
        val value = function(next)
        if (value < minValue) {
            output.clear()
            output.add(next)
            minValue = value
        } else if (value == minValue) {
            output.add(next)
        }
    }

    return output
}

data class Point(val x: Int, val y: Int) {
    fun distanceFrom(other: Point) = Math.abs(x - other.x) + Math.abs(y - other.y)

    companion object {
        private val REGEX = "(\\d+), (\\d+)".toRegex()
        fun parse(input: String): Point {
            val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable input $input")
            return Point(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        }
    }
}


fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day6/input.txt")
            .readText()
            .trim()

    val coordinates = ChronalCoordinates(input)

    println(coordinates.largestFiniteAreaSize())
    println(coordinates.areaWithinTotalDistance(10000))
}