package advent.year2024.day12

import advent.meta.readInput
import advent.utils.Point

class GardenRegions(private val plots: Map<Point, Char>) {
    constructor(input: String) : this(input.trim()
        .lines()
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Point(x, y) to c }
        }
        .toMap())

    fun price() = regions().sumOf { it.price() }

    private fun regions(): List<GardenRegion> {
        val output = mutableListOf<GardenRegion>()
        val remainingPoints = plots.keys.toMutableSet()

        while (remainingPoints.isNotEmpty()) {
            val point = remainingPoints.first()
            val nextRegion = this.findRegion(point)
            remainingPoints.removeAll(nextRegion)
            output.add(GardenRegion(nextRegion))
        }

        return output
    }

    private fun findRegion(point: Point): Set<Point> {
        val plantType = plots[point] ?: throw IllegalArgumentException("Can't find region for $point, no plant there")
        return findRegion(plantType, emptySet(), listOf(point))
    }

    private tailrec fun findRegion(
        plantType: Char,
        alreadyInRegion: Set<Point>,
        pendingEvaluation: List<Point>
    ): Set<Point> {
        if (pendingEvaluation.isEmpty()) return alreadyInRegion
        val evaluating = pendingEvaluation.first()
        return when {
            evaluating in alreadyInRegion -> findRegion(plantType, alreadyInRegion, pendingEvaluation.drop(1))
            plots[evaluating] == plantType -> findRegion(
                plantType,
                alreadyInRegion + evaluating,
                pendingEvaluation.drop(1) + evaluating.adjacentNeighbors
            )

            else -> findRegion(plantType, alreadyInRegion, pendingEvaluation.drop(1))
        }
    }
}

class GardenRegion(val points: Set<Point>) {
    private fun area() = points.size
    private fun perimeter() = points.sumOf { point -> point.adjacentNeighbors.count { it !in points } }

    fun price() = area() * perimeter()
}

fun main() {
    val gardenRegions = GardenRegions(readInput())

    println(gardenRegions.price())
}