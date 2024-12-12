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
    fun bulkDiscountPrice() = regions().sumOf { it.bulkDiscountPrice() }

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
    private fun numberOfSides(): Int {
        val perimeterCrossings: MutableSet<Pair<Point, Point>> = points.flatMap { point ->
            point.adjacentNeighbors
                .filter { it !in points }
                .map { neighbor -> point to neighbor }
        }.toMutableSet()

        // Finding the sides is quite similar to finding the regions in the first place! Starting with a perimeter
        // crossing, see if the adjacent neighbors also have a crossing in the same direction, and if so, group them
        // together and keep going.
        var output = 0
        while (perimeterCrossings.isNotEmpty()) {
            output++
            val underConsideration = mutableListOf(perimeterCrossings.first())
            while (underConsideration.isNotEmpty()) {
                val crossing = underConsideration.removeFirst()
                val wasACrossing = perimeterCrossings.remove(crossing)
                if (wasACrossing) {
                    val delta = crossing.second - crossing.first
                    underConsideration.addAll(
                        crossing.first.adjacentNeighbors.map { neighbor -> neighbor to neighbor + delta }
                    )
                }
            }
        }

        return output
    }

    fun price() = area() * perimeter()
    fun bulkDiscountPrice() = area() * numberOfSides()
}

fun main() {
    val gardenRegions = GardenRegions(readInput())

    println(gardenRegions.price())
    println(gardenRegions.bulkDiscountPrice())
}