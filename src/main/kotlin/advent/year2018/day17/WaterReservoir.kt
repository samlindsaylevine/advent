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

    fun waterTileCount() = flowResult().waterCount()
    fun settledWaterCount() = flowResult().settledWaterCount()

    fun display() = flowResult().toString()

    private fun flowResult() = FlowResult(clay, Point(500, 0))

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

private data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(this.x + other.x, this.y + other.y)
}

private class FlowResult constructor(clay: Set<Point>, spring: Point) {
    private enum class PointState(val display: String, val causesPressure: Boolean = false) {
        FLOWING_WATER("|"),
        SETTLED_WATER("~", causesPressure = true),
        CLAY("#", causesPressure = true),
        EMPTY(".")
    }

    private val points: MutableMap<Point, PointState> = clay.associate { it to PointState.CLAY }.toMutableMap()

    private val minY = clay.map { it.y }.min() ?: 0
    private val maxY = clay.map { it.y }.max() ?: 0
    private val minX = clay.map { it.x }.min() ?: 0
    private val maxX = clay.map { it.x }.max() ?: 0

    init {
        this[spring] = PointState.FLOWING_WATER
        flow(spring)
    }

    private operator fun get(point: Point): PointState = points[point] ?: PointState.EMPTY
    private operator fun set(point: Point, state: PointState) {
        points[point] = state
    }

    // Going to be imperative and mutate all our state on construction time for speed.
    private fun flow(flowPoint: Point) {
        if (flowPoint.y > maxY) return

        val underneath = flowPoint + Point(0, 1)

        // We're going to recursively flow out the "downstream" spaces from this space first, before handling this
        // space's ultimate fate. So, after this call, we know that the space underneath us will be properly decided.
        if (this[underneath] == PointState.EMPTY) {
            this[underneath] = PointState.FLOWING_WATER
            flow(underneath)
        }

        val left = flowPoint + Point(-1, 0)

        // Now that the space underneath us is decided, we can see if it causes backpressure and the water has to flow
        // to the sides. If so, we'll do that. (After these calls to the side, we might still have things on this
        // y level marked as FLOWING that will ultimately be overwritten as SETTLED.)
        if (this[underneath].causesPressure && this[left] == PointState.EMPTY) {
            this[left] = PointState.FLOWING_WATER
            flow(left)
        }

        val right = flowPoint + Point(1, 0)

        if (this[underneath].causesPressure && this[right] == PointState.EMPTY) {
            this[right] = PointState.FLOWING_WATER
            flow(right)
        }

        // We flowed to the sides - now see if this level is trapped - i.e., has walls. If it does, overwrite all the
        // FLOWING_WATER that is trapped, as SETTLED_WATER. (If underneath us is flowing, this is definitely not
        // settled! This criterion had to be added because otherwise flowing water that was contained by two walls,
        // but dropped out between them, was incorrectly marked as SETTLED.)
        if (hasWalls(flowPoint) && this[underneath] != PointState.FLOWING_WATER) {
            fillLevel(flowPoint)
        }
    }

    private fun hasWalls(point: Point) = hasWall(point, left = true) && hasWall(point, left = false)

    private fun hasWall(point: Point, left: Boolean) = horizontalFrom(point, left = left)
            .map {
                when (this[it]) {
                    PointState.EMPTY -> false
                    PointState.CLAY -> true
                    else -> null
                }
            }
            .filterNotNull()
            .first()

    private fun fillLevel(point: Point) {
        fillSide(point, left = true)
        fillSide(point, left = false)
    }

    private fun fillSide(point: Point, left: Boolean) = horizontalFrom(point, left)
            .takeWhile { this[it] != PointState.CLAY }
            .forEach { this[it] = PointState.SETTLED_WATER }

    private fun horizontalFrom(point: Point, left: Boolean) =
            generateSequence(point) { it + Point(if (left) -1 else 1, 0) }

    override fun toString(): String {
        return (0..maxY).joinToString("\n") { y ->
            ((minX - 1)..(maxX + 1)).joinToString("") { x -> this[Point(x, y)].display }
        }
    }

    fun waterCount() = this.points
            .filter { it.key.y in minY..maxY }
            .count { it.value == PointState.FLOWING_WATER || it.value == PointState.SETTLED_WATER }

    fun settledWaterCount() = this.points.count { it.value == PointState.SETTLED_WATER }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day17/input.txt")
            .readText()

    val reservoir = WaterReservoir.parse(input)

    println(reservoir.display())

    println(reservoir.waterTileCount())
    println(reservoir.settledWaterCount())
}