package advent.year2018.day25

import java.io.File

class ReindeerConstellations private constructor(private val points: Set<Point4D>) {
    companion object {
        fun parse(input: String) = ReindeerConstellations(input.lines()
                .map { Point4D.parse(it) }
                .toSet())
    }

    fun numConstellations() = numConstellations(points,
            emptySet(),
            0)

    private tailrec fun numConstellations(unassignedPoints: Set<Point4D>,
                                          justAssignedPoints: Set<Point4D>,
                                          constellationsCounted: Int): Int = when {
        unassignedPoints.isEmpty() -> constellationsCounted
        justAssignedPoints.isEmpty() -> {
            val first = unassignedPoints.first()
            numConstellations(unassignedPoints - first, setOf(first), constellationsCounted + 1)
        }
        else -> {
            val adjacent = unassignedPoints.filter { unassigned ->
                justAssignedPoints.any { it.distanceTo(unassigned) <= 3 }
            }.toSet()
            numConstellations(unassignedPoints - adjacent,
                    adjacent,
                    constellationsCounted)
        }
    }
}

private data class Point4D(val x: Int, val y: Int, val z: Int, val t: Int) {
    companion object {
        fun parse(input: String): Point4D {
            val nums = input.trim().split(",").map { it.toInt() }
            return Point4D(nums[0], nums[1], nums[2], nums[3])
        }
    }

    fun distanceTo(other: Point4D) = Math.abs(this.x - other.x) +
            Math.abs(this.y - other.y) +
            Math.abs(this.z - other.z) +
            Math.abs(this.t - other.t)
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day25/input.txt")
            .readText()
            .trim()

    val constellations = ReindeerConstellations.parse(input)

    println(constellations.numConstellations())
}