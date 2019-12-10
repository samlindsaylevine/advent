package advent.year2018.day22

import advent.year2018.day20.Point
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
        TODO()
    }
}

enum class RegionType(val riskLevel: Int, val char: Char) {
    ROCKY(riskLevel = 0, char = '.'),
    WET(riskLevel = 1, char = '='),
    NARROW(riskLevel = 2, char = '|')
}

fun main() {
    val cave = CaveSystem(depth = 5355, target = Point(14, 796))

    println(cave.totalRisk())
}