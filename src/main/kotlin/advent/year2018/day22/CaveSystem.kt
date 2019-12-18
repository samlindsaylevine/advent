package advent.year2018.day22

import advent.utils.*
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader

class CaveSystem(val depth: Int,
                 val target: Point) {

    private val geologicalIndices = CacheBuilder.newBuilder()
            .build(CacheLoader.from { point: Point? -> calculateGeologicalIndex(point!!) })

    fun geologicalIndex(point: Point): Int = geologicalIndices.get(point)

    private fun calculateGeologicalIndex(point: Point): Int = when {
        point == Point(0, 0) -> 0
        point == target -> 0
        point.y == 0 -> point.x * 16807
        point.x == 0 -> point.y * 48271
        else -> erosionLevel(point + Point(-1, 0)) *
                erosionLevel(point + Point(0, -1))
    }

    fun erosionLevel(point: Point) = (geologicalIndex(point) + depth) % 20183

    fun regionType(point: Point) = when (val mod = erosionLevel(point) % 3) {
        0 -> RegionType.ROCKY
        1 -> RegionType.WET
        2 -> RegionType.NARROW
        else -> throw IllegalStateException("This should not be possible, region type $mod")
    }

    private fun rectangle(upperLeft: Point, lowerRight: Point): List<List<Point>> =
            (upperLeft.y..lowerRight.y).map { y ->
                (upperLeft.x..lowerRight.x).map { x -> Point(x, y) }
            }

    private fun charAt(point: Point) = when (point) {
        Point(0, 0) -> 'M'
        target -> 'T'
        else -> regionType(point).char
    }

    fun map(lowerRight: Point): String = rectangle(Point(0, 0), lowerRight).joinToString("\n") { row ->
        row.joinToString("") { point -> Character.toString(charAt(point)) }
    }

    fun totalRisk() = rectangle(Point(0, 0), target).sumBy { row -> row.sumBy { regionType(it).riskLevel } }

    fun rescueTime(): Int {
        val finder = ShortestPathFinder()

        val shortestPaths = finder.find(start = RescuerState(Point(0, 0), CaveTool.TORCH),
                end = EndState(RescuerState(target, CaveTool.TORCH)),
                filter = Filter {
                    val last = it.last()
                    // This is kind of a hack - we don't expect to have to go too far beyond the bounds of where
                    // our target's X and Y are so we will prune those branches aggressively. If this is wrong, we
                    // might end up with an answer that is too high. Without this aggressive pruning, the path finding
                    // takes uncomfortably long.
                    last.position.x > 2 * target.x || last.position.y > 2 * target.y
                },
                // We don't care about any 2 paths that share the same current state - we can just pick one of those
                // arbitrarily.
                collapse = Collapse { steps: List<RescuerState> -> steps.last() },
                nextSteps = StepsWithCost { it.nextSteps(this) })

        return shortestPaths.first().totalCost
    }
}

enum class RegionType(val riskLevel: Int,
                      val char: Char,
                      val legalTools: Set<CaveTool>) {
    ROCKY(riskLevel = 0, char = '.', legalTools = setOf(CaveTool.CLIMBING_GEAR, CaveTool.TORCH)),
    WET(riskLevel = 1, char = '=', legalTools = setOf(CaveTool.CLIMBING_GEAR, CaveTool.NEITHER)),
    NARROW(riskLevel = 2, char = '|', legalTools = setOf(CaveTool.TORCH, CaveTool.NEITHER))
}

enum class CaveTool { CLIMBING_GEAR, TORCH, NEITHER }

private data class RescuerState(val position: Point, val tool: CaveTool) {
    fun nextSteps(cave: CaveSystem): Set<Step<RescuerState>> {
        val toolChanges = CaveTool.values()
                .filter { it != tool && cave.regionType(position).legalTools.contains(it) }
                .map { Step(next = RescuerState(position, it), cost = 7) }
                .toSet()

        val moves = position.adjacentNeighbors
                .filter { it.x >= 0 && it.y >= 0 }
                .filter { cave.regionType(it).legalTools.contains(tool) }
                .map { Step(next = RescuerState(it, tool), cost = 1) }

        return toolChanges + moves
    }
}

fun main() {
    val cave = CaveSystem(depth = 5355, target = Point(14, 796))

    println(cave.totalRisk())
    println(cave.rescueTime())
}