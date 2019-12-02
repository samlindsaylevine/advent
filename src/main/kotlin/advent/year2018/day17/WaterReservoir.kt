package advent.year2018.day17

import java.io.File

class WaterReservoir private constructor(rows: List<Row>,
                                         columns: List<Column>) {

    companion object {
        fun parse(input: String): WaterReservoir {
            val lines = input.split("\n")
            val rows = lines.filter { it.startsWith("y") }
                    .map { Row.parse(it) }
            val columns = lines.filter { it.startsWith("x") }
                    .map { Column.parse(it) }
            return WaterReservoir(rows, columns)
        }
    }

    private val clay: Set<Point> = (rows.map { it.points } + columns.map { it.points })
            .fold(emptySet(), { acc, points -> acc.union(points) })


    private val minY by lazy { clay.map { it.y }.min() ?: 0 }
    private val maxY by lazy { clay.map { it.y }.max() ?: 0 }
    private val minX by lazy { clay.map { it.x }.min() ?: 0 }
    private val maxX by lazy { clay.map { it.x }.max() ?: 0 }

    private fun waterTiles() = waterTiles(setOf(Point(500, 0)))

    fun waterTileCount() = waterTiles().all.count { it.y in minY..maxY }

    private data class Point(val x: Int, val y: Int) {
        operator fun plus(other: Point) = Point(this.x + other.x, this.y + other.y)
    }

    private data class WaterTiles(val settledWater: Set<Point>,
                                  val flowingWater: Set<Point>) {
        val all = settledWater + flowingWater
    }

    private tailrec fun waterTiles(springs: Set<Point>,
                                   settledWater: Set<Point> = emptySet(),
                                   flowingWater: Set<Point> = emptySet()): WaterTiles {
        if (springs.isEmpty()) return WaterTiles(settledWater, flowingWater)

        val spring = springs.first()
        val otherSprings = springs - spring

        val underneath = spring + Point(0, 1)

        return if (spring.y >= maxY) {
            // This spring made it to the bottom of the map and we can stop now.
            waterTiles(otherSprings, settledWater, flowingWater + spring)
        } else if (flowingWater.contains(underneath)) {
            // If the spot underneath us has flowing water, we can be done with this spring -- it will join with that
            // flow and its point is flowing water.
            waterTiles(otherSprings, settledWater, flowingWater + spring)
        } else if (clay.contains(underneath) || settledWater.contains(underneath)) {
            // The water spreads out to both sides. It might be contained or not. If it is contained, then this
            // contained row is settled water and we need to move the spring up one.
            val row = checkRow(spring, settledWater, flowingWater)
            val newSprings = otherSprings + row.newSprings +
                    (if (row.contained) setOf(spring + Point(0, -1)) else emptySet())
            waterTiles(newSprings,
                    settledWater + row.newSettledWater,
                    flowingWater - row.newSettledWater + row.newFlowingWater)
        } else {
            // Air is underneath. Move the spring point down one and mark this point as flowing water.
            waterTiles(otherSprings + underneath, settledWater, flowingWater + spring)
        }
    }

    private fun checkRow(spring: Point, settledWater: Set<Point>, flowingWater: Set<Point>): SpreadResult {
        val left = checkDirection(spring, -1, settledWater, flowingWater)
        val right = checkDirection(spring, 1, settledWater, flowingWater)

        val newWater = (left.newWater + right.newWater + spring).toSet()
        val contained = left.terminus.blocksFlow && right.terminus.blocksFlow
        val newSprings = listOfNotNull(left.newSpring, right.newSpring).toSet()

        return SpreadResult(contained,
                if (contained) newWater else emptySet(),
                if (contained) emptySet() else newWater,
                newSprings)
    }

    private data class SpreadResult(val contained: Boolean,
                                    val newSettledWater: Set<Point>,
                                    val newFlowingWater: Set<Point>,
                                    val newSprings: Set<Point>)

    private fun checkDirection(spring: Point,
                               delta: Int,
                               settledWater: Set<Point>,
                               flowingWater: Set<Point>): DirectionResult {
        return checkDirection(listOf(spring), delta, settledWater, flowingWater)
    }

    private tailrec fun checkDirection(newWater: List<Point>,
                                       delta: Int,
                                       settledWater: Set<Point>,
                                       flowingWater: Set<Point>): DirectionResult {
        val last = newWater.last()
        val next = last + Point(delta, 0)
        val underneath = last + Point(0, 1)

        fun isEmpty(point: Point) = !clay.contains(point) &&
                !settledWater.contains(point) &&
                !flowingWater.contains(point)

        return when {
            isEmpty(underneath) -> DirectionResult(newWater, DirectionTerminus.NEW_SPRING)
            clay.contains(next) -> DirectionResult(newWater, DirectionTerminus.CLAY)
            flowingWater.contains(next) -> DirectionResult(newWater, DirectionTerminus.FLOWING_WATER)
            settledWater.contains(next) -> DirectionResult(newWater, DirectionTerminus.SETTLED_WATER)
            else -> checkDirection(newWater + next, delta, settledWater, flowingWater)
        }
    }

    private enum class DirectionTerminus(val blocksFlow: Boolean) {
        CLAY(blocksFlow = true),
        NEW_SPRING(blocksFlow = false),
        SETTLED_WATER(blocksFlow = true),
        FLOWING_WATER(blocksFlow = false)
    }

    private data class DirectionResult(val newWater: List<Point>,
                                       val terminus: DirectionTerminus) {
        val newSpring: Point? = if (terminus == DirectionTerminus.NEW_SPRING) newWater.last() else null
    }

    fun display(): String {
        val waterTiles = waterTiles()
        return (0..maxY).joinToString("\n") { y ->
            ((minX - 1)..(maxX + 1)).joinToString("") { x ->
                when {
                    clay.contains(Point(x, y)) -> "#"
                    waterTiles.settledWater.contains(Point(x, y)) -> "~"
                    waterTiles.flowingWater.contains(Point(x, y)) -> "|"
                    else -> "."
                }
            }
        }
    }

    private class Row(y: Int, minX: Int, maxX: Int) {
        companion object {
            private val REGEX = "y=(\\d+), x=(\\d+)..(\\d+)".toRegex()

            fun parse(input: String): Row {
                val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("not a row: $input")
                return Row(match.groupValues[1].toInt(),
                        match.groupValues[2].toInt(),
                        match.groupValues[3].toInt())
            }
        }

        val points: Set<Point> = (minX..maxX).map { Point(it, y) }.toSet()

    }

    private class Column(x: Int, minY: Int, maxY: Int) {
        companion object {
            private val REGEX = "x=(\\d+), y=(\\d+)..(\\d+)".toRegex()

            fun parse(input: String): Column {
                val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("not a row: $input")
                return Column(match.groupValues[1].toInt(),
                        match.groupValues[2].toInt(),
                        match.groupValues[3].toInt())
            }
        }

        val points: Set<Point> = (minY..maxY).map { Point(x, it) }.toSet()
    }
}

private fun <T> Sequence<T>.takeWhileInclusive(pred: (T) -> Boolean): Sequence<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = pred(it)
        result
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day17/input.txt")
            .readText()

    val reservoir = WaterReservoir.parse(input)

    println(reservoir.toString())

    //920 is too low
    //29138 is too high
    println(reservoir.waterTileCount())
}