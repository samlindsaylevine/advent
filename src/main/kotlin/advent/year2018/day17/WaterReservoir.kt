package advent.year2018.day17

import java.io.File

/**
 * --- Day 17: Reservoir Research ---
 * You arrive in the year 18. If it weren't for the coat you got in 1018, you would be very cold: the North Pole base
 * hasn't even been constructed.
 * Rather, it hasn't been constructed yet.  The Elves are making a little progress, but there's not a lot of liquid
 * water in this climate, so they're getting very dehydrated.  Maybe there's more underground?
 * You scan a two-dimensional vertical slice of the ground nearby and discover that it is mostly sand with veins of
 * clay.  The scan only provides data with a granularity of square meters, but it should be good enough to determine
 * how much water is trapped there. In the scan, x represents the distance to the right, and y represents the distance
 * down. There is also a spring of water near the surface at x=500, y=0. The scan identifies which square meters are
 * clay (your puzzle input).
 * For example, suppose your scan shows the following veins of clay:
 * x=495, y=2..7
 * y=7, x=495..501
 * x=501, y=3..7
 * x=498, y=2..4
 * x=506, y=1..2
 * x=498, y=10..13
 * x=504, y=10..13
 * y=13, x=498..504
 * 
 * Rendering clay as #, sand as ., and the water spring as +, and with x increasing to the right and y increasing
 * downward, this becomes:
 *    44444455555555
 *    99999900000000
 *    45678901234567
 *  0 ......+.......
 *  1 ............#.
 *  2 .#..#.......#.
 *  3 .#..#..#......
 *  4 .#..#..#......
 *  5 .#.....#......
 *  6 .#.....#......
 *  7 .#######......
 *  8 ..............
 *  9 ..............
 * 10 ....#.....#...
 * 11 ....#.....#...
 * 12 ....#.....#...
 * 13 ....#######...
 * 
 * The spring of water will produce water forever. Water can move through sand, but is blocked by clay. Water always
 * moves down when possible, and spreads to the left and right otherwise, filling space that has clay on both sides and
 * falling out otherwise.
 * For example, if five squares of water are created, they will flow downward until they reach the clay and settle
 * there. Water that has come to rest is shown here as ~, while sand through which water has passed (but which is now
 * dry again) is shown as |:
 * ......+.......
 * ......|.....#.
 * .#..#.|.....#.
 * .#..#.|#......
 * .#..#.|#......
 * .#....|#......
 * .#~~~~~#......
 * .#######......
 * ..............
 * ..............
 * ....#.....#...
 * ....#.....#...
 * ....#.....#...
 * ....#######...
 * 
 * Two squares of water can't occupy the same location.  If another five squares of water are created, they will settle
 * on the first five, filling the clay reservoir a little more:
 * ......+.......
 * ......|.....#.
 * .#..#.|.....#.
 * .#..#.|#......
 * .#..#.|#......
 * .#~~~~~#......
 * .#~~~~~#......
 * .#######......
 * ..............
 * ..............
 * ....#.....#...
 * ....#.....#...
 * ....#.....#...
 * ....#######...
 * 
 * Water pressure does not apply in this scenario. If another four squares of water are created, they will stay on the
 * right side of the barrier, and no water will reach the left side:
 * ......+.......
 * ......|.....#.
 * .#..#.|.....#.
 * .#..#~~#......
 * .#..#~~#......
 * .#~~~~~#......
 * .#~~~~~#......
 * .#######......
 * ..............
 * ..............
 * ....#.....#...
 * ....#.....#...
 * ....#.....#...
 * ....#######...
 * 
 * At this point, the top reservoir overflows. While water can reach the tiles above the surface of the water, it
 * cannot settle there, and so the next five squares of water settle like this:
 * ......+.......
 * ......|.....#.
 * .#..#||||...#.
 * .#..#~~#|.....
 * .#..#~~#|.....
 * .#~~~~~#|.....
 * .#~~~~~#|.....
 * .#######|.....
 * ........|.....
 * ........|.....
 * ....#...|.#...
 * ....#...|.#...
 * ....#~~~~~#...
 * ....#######...
 * 
 * Note especially the leftmost |: the new squares of water can reach this tile, but cannot stop there.  Instead,
 * eventually, they all fall to the right and settle in the reservoir below.
 * After 10 more squares of water, the bottom reservoir is also full:
 * ......+.......
 * ......|.....#.
 * .#..#||||...#.
 * .#..#~~#|.....
 * .#..#~~#|.....
 * .#~~~~~#|.....
 * .#~~~~~#|.....
 * .#######|.....
 * ........|.....
 * ........|.....
 * ....#~~~~~#...
 * ....#~~~~~#...
 * ....#~~~~~#...
 * ....#######...
 * 
 * Finally, while there is nowhere left for the water to settle, it can reach a few more tiles before overflowing
 * beyond the bottom of the scanned data:
 * ......+.......    (line not counted: above minimum y value)
 * ......|.....#.
 * .#..#||||...#.
 * .#..#~~#|.....
 * .#..#~~#|.....
 * .#~~~~~#|.....
 * .#~~~~~#|.....
 * .#######|.....
 * ........|.....
 * ...|||||||||..
 * ...|#~~~~~#|..
 * ...|#~~~~~#|..
 * ...|#~~~~~#|..
 * ...|#######|..
 * ...|.......|..    (line not counted: below maximum y value)
 * ...|.......|..    (line not counted: below maximum y value)
 * ...|.......|..    (line not counted: below maximum y value)
 * 
 * How many tiles can be reached by the water?  To prevent counting forever, ignore tiles with a y coordinate smaller
 * than the smallest y coordinate in your scan data or larger than the largest one. Any x coordinate is valid. In this
 * example, the lowest y coordinate given is 1, and the highest is 13, causing the water spring (in row 0) and the
 * water falling off the bottom of the render (in rows 14 through infinity) to be ignored.
 * So, in the example above, counting both water at rest (~) and other sand tiles the water can hypothetically reach
 * (|), the total number of tiles the water can reach is 57.
 * How many tiles can the water reach within the range of y values in your scan?
 * 
 * --- Part Two ---
 * After a very long time, the water spring will run dry. How much water will be retained?
 * In the example above, water that won't eventually drain out is shown as ~, a total of 29 tiles.
 * How many water tiles are left after the water spring stops producing water and all remaining water not at rest has
 * drained?
 * 
 */
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

    private val minY = clay.map { it.y }.minOrNull() ?: 0
    private val maxY = clay.map { it.y }.maxOrNull() ?: 0
    private val minX = clay.map { it.x }.minOrNull() ?: 0
    private val maxX = clay.map { it.x }.maxOrNull() ?: 0

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