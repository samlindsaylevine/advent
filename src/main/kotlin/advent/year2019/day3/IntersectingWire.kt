package advent.year2019.day3

import java.io.File

class IntersectingWire(input: String) {

    private val points: List<Point> = input.split(",")
            .map { Segment.parse(it) }
            .fold(WireInProgress(Point(0, 0)), WireInProgress::plus)
            .all

    fun closestIntersectionDistance(other: IntersectingWire) = closestIntersectionBy(other) {
        it.distanceFrom(Point(0, 0))
    }

    fun closestIntersectionSteps(other: IntersectingWire) = closestIntersectionBy(other) {
        this.points.indexOf(it) + other.points.indexOf(it)
    }

    private fun closestIntersectionBy(other: IntersectingWire, transform: (Point) -> Int) =
            points.intersect(other.points)
                    .filter { it != Point(0, 0) }
                    .map(transform)
                    .min()

    private class WireInProgress(val current: Point, val all: List<Point> = listOf(current)) {
        operator fun plus(segment: Segment): WireInProgress {
            val newPoints = (1..segment.length)
                    .map { distance: Int -> current + segment.direction * distance }
            return WireInProgress(newPoints.last(), all + newPoints)
        }
    }

    private class Segment(val direction: Point, val length: Int) {
        companion object {
            fun parse(input: String): Segment {
                val direction = when (input.first()) {
                    'R' -> Point(1, 0)
                    'L' -> Point(-1, 0)
                    'U' -> Point(0, 1)
                    'D' -> Point(0, -1)
                    else -> throw IllegalArgumentException("Unparseable segment $input")
                }
                val length = input.substring(1).toInt()
                return Segment(direction, length)
            }
        }
    }
}

private data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(this.x + other.x, this.y + other.y)
    operator fun times(distance: Int) = Point(distance * this.x, distance * this.y)
    fun distanceFrom(other: Point) = Math.abs(this.x - other.x) + Math.abs(this.y - other.y)
}

fun main() {
    val lines = File("src/main/kotlin/advent/year2019/day3/input.txt")
            .readLines()

    val firstWire = IntersectingWire(lines[0])
    val secondWire = IntersectingWire(lines[1])

    println(firstWire.closestIntersectionDistance(secondWire))
    println(firstWire.closestIntersectionSteps(secondWire))
}