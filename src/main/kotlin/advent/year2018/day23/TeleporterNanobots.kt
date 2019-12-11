package advent.year2018.day23

import advent.year2015.day24.Ticker
import advent.year2018.day6.allMinBy
import java.io.File
import java.lang.IllegalArgumentException

data class TeleporterNanobot(val location: Point3D, val range: Int) {
    companion object {
        private val REGEX = "pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)".toRegex()

        fun parse(input: String): TeleporterNanobot {
            val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable input $input")
            return TeleporterNanobot(Point3D(match.groupValues[1].toInt(),
                    match.groupValues[2].toInt(),
                    match.groupValues[3].toInt()),
                    match.groupValues[4].toInt())
        }
    }

    infix fun isInRangeOf(other: TeleporterNanobot) = (this.location.distanceTo(other.location)) <= other.range

    fun originDistanceRange(): IntRange {
        val distanceToOrigin = this.location.distanceTo(Point3D(0, 0, 0))
        val lower = Math.max(0, distanceToOrigin - range)
        val upper = distanceToOrigin + range
        return lower..upper
    }
}

data class Point3D(val x: Int, val y: Int, val z: Int) {
    fun distanceTo(other: Point3D) = Math.abs(this.x - other.x) +
            Math.abs(this.y - other.y) +
            Math.abs(this.z - other.z)
}

class TeleporterNanobots(val bots: List<TeleporterNanobot>) {
    companion object {
        fun parse(input: String) = TeleporterNanobots(input.lines().map { TeleporterNanobot.parse(it) })
    }

    fun botCountInRangeOfStrongest(): Int {
        val strongest = bots.maxBy { it.range } ?: return 0
        return bots.count { it isInRangeOf strongest }
    }
}

fun List<IntRange>.maxOverlapPoint(): Int {
    val lowerBounds = this.map { it.first }.toSet()
    return lowerBounds.maxBy { i ->
        this.count { it.contains(i) }
    } ?: 0
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day23/input.txt")
            .readText()
            .trim()

    val nanobots = TeleporterNanobots.parse(input)
    println(nanobots.botCountInRangeOfStrongest())

    // 94481123 is too low
    println(nanobots.bots.map { it.originDistanceRange() }.sortedBy { it.first })
}