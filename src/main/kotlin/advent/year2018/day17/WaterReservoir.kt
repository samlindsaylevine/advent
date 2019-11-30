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

    private val waterTiles by lazy {
        waterTiles(emptySet(), setOf(Point(500, 0)))
    }

    val waterTileCount by lazy { waterTiles.count { it.y in minY..maxY } }

    private data class Point(val x: Int, val y: Int)

    private tailrec fun waterTiles(water: Set<Point>,
                                   remainingSprings: Set<Point>,
                                   allSprings: Set<Point> = remainingSprings): Set<Point> {
        if (remainingSprings.isEmpty()) return water

        val spring = remainingSprings.first()
        val otherSprings = remainingSprings - spring

        // Trace the location of the spring downwards.
        val waterfall = waterfall(spring)

        // Either it hits:
        // 1) The bottom of the map (maxY), in which case it is finished there
        // 2) A water or clay tile, in which case it expands to the sides
        val lastWater = waterfall.last()
        val belowLast = Point(lastWater.x, lastWater.y + 1)
        val expand = clay.contains(belowLast) || water.contains(belowLast)

        return if (expand) {
            //   a) It expands from that impact point all the way to the left and right
            //   b) If it expands to extend over nothing on either or both sides, stop, and those over-nothing points
            //      are now spring tiles
            //   c) If it is contained on both sides, check back up the downward cascade one level as the impact point
            //      and go to step 3a
            val expansion = expansion(lastWater, water)

            // Just a double check to make sure we never infinite loop.
            val newSprings = expansion.newSprings.filter { !allSprings.contains(it) }

            waterTiles(water + waterfall + expansion.moreWater,
                    otherSprings + newSprings,
                    allSprings + newSprings)
        } else {
            waterTiles(water + waterfall, otherSprings, allSprings)
        }
    }

    /**
     * Trace the fall of a spring downwards until it ends (at the bottom of the
     * map, or on top of wtater or a clay tile).
     *
     * @return A list of points that will be full of water.
     */
    private fun waterfall(spring: Point) = generateSequence(spring) { Point(it.x, it.y + 1) }
            .takeWhile { !clay.contains(it) && it.y <= maxY }
            .toList()

    private tailrec fun expansion(impactPoint: Point, water: Set<Point>): ExpansionResult {
        println("Expanding, water = $water")

        val left = sideExpand(impactPoint, water, true)
        val right = sideExpand(impactPoint, water, false)

        val newSprings = listOfNotNull(left.newSpring, right.newSpring)

        val newWater = water + left.moreWater + right.moreWater

        return if (newSprings.isEmpty()) {
            expansion(Point(impactPoint.x, impactPoint.y - 1), newWater)
        } else {
            ExpansionResult(newWater, newSprings.toSet())
        }
    }

    private data class ExpansionResult(val moreWater: Set<Point>,
                                       val newSprings: Set<Point>)


    private fun sideExpand(impactPoint: Point, water: Set<Point>, left: Boolean): SideExpandResult {
        val delta = if (left) -1 else 1

        val newWater = generateSequence(impactPoint) { Point(it.x + delta, it.y) }
                .takeWhileInclusive {
                    val underneath = Point(it.x, it.y + 1)
                    val nextSpace = Point(it.x + delta, it.y)

                    (clay.contains(underneath) || water.contains(underneath)) &&
                            !clay.contains(nextSpace)
                }
                .toList()

        val underLast = Point(newWater.last().x, newWater.last().y + 1)
        val newSpring = if (clay.contains(underLast) || water.contains(underLast)) null else newWater.last()


        return SideExpandResult(newWater.toSet(), newSpring)
    }

    private data class SideExpandResult(val moreWater: Set<Point>,
                                        val newSpring: Point?)

    override fun toString(): String = (0..maxY).joinToString("\n") { y ->
        ((minX - 1)..(maxX + 1)).joinToString("") { x ->
            when {
                clay.contains(Point(x, y)) -> "#"
                waterTiles.contains(Point(x, y)) -> "~"
                else -> "."
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
    println(reservoir.waterTileCount)
}