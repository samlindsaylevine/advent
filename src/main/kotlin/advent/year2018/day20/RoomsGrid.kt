package advent.year2018.day20

import advent.utils.Direction
import advent.utils.Point
import advent.utils.UnorderedPair
import advent.year2015.day24.Ticker
import java.io.File

class RoomsGrid(private val rooms: Set<Point>,
                private val doors: Set<Door>) {
    companion object {
        fun fromPaths(paths: RoomPaths): RoomsGrid {
            val rooms = mutableSetOf<Point>()
            val doors = mutableSetOf<Door>()

            val ticker = Ticker(1000)

            paths.allPaths().forEach {
                it.walkPath(rooms, doors)
                ticker.tick()
            }

            return RoomsGrid(rooms, doors)
        }

        private fun List<Direction>.walkPath(rooms: MutableSet<Point>,
                                             doors: MutableSet<Door>) {
            var current = Point(0, 0)
            rooms.add(current)

            for (step in this) {
                val next = current + step.toPoint()
                rooms.add(next)
                doors.add(Door(current, next))
                current = next
            }
        }
    }

    override fun toString(): String {
        val minX = rooms.map { it.x }.minOrNull() ?: 0
        val maxX = rooms.map { it.x }.maxOrNull() ?: 0
        val minY = rooms.map { it.y }.minOrNull() ?: 0
        val maxY = rooms.map { it.y }.maxOrNull() ?: 0

        // Our string is going to be a total of 2 * (maxX - minX) + 3 wide and likewise for height.
        val yRange = ((2 * minY - 1)..(2 * maxY + 1)).toList().reversed()
        val xRange = (2 * minX - 1)..(2 * maxX + 1)

        return yRange.joinToString("\n") { y ->
            xRange.joinToString("") { x ->
                when {
                    x == 0 && y == 0 -> "X"
                    x mod 2 == 0 && y mod 2 == 0 -> "."
                    x mod 2 == 1 && y mod 2 == 1 -> "#" // Corner
                    x mod 2 == 1 && y mod 2 == 0 -> {
                        val roomY = (y / 2)
                        val roomOneX = (x - 1) / 2
                        val roomTwoX = (x + 1) / 2
                        val roomOne = Point(roomOneX, roomY)
                        val roomTwo = Point(roomTwoX, roomY)
                        if (doors.contains(Door(roomOne, roomTwo))) "|" else "#"
                    }
                    x mod 2 == 0 && y mod 2 == 1 -> {
                        val roomX = (x / 2)
                        val roomOneY = (y - 1) / 2
                        val roomTwoY = (y + 1) / 2
                        val roomOne = Point(roomX, roomOneY)
                        val roomTwo = Point(roomX, roomTwoY)
                        if (doors.contains(Door(roomOne, roomTwo))) "-" else "#"
                    }
                    else -> throw IllegalStateException("Cases should have been exhaustive; x=$x y=$y")
                }
            }
        }
    }

    private infix fun Int.mod(other: Int): Int {
        val crappyJavaMod = this % other
        return if (crappyJavaMod < 0) crappyJavaMod + other else crappyJavaMod
    }

    fun distanceToFurthestRoom() = roomCrawl.maxDistance
    fun farAwayRoomCount() = roomCrawl.farAwayRoomCount

    private val roomCrawl by lazy {
        // The naivest approach, calculating the distance to any particular room individually, and then taking the max,
        // is too slow for the 10,000 rooms in our problem input. Instead, we'll walk all the rooms at once and mark
        // how many steps that takes.
        var stepsTaken = 0
        val visited = mutableSetOf(Point(0, 0))
        var lastVisited = setOf(Point(0, 0))
        var farAwayRoomCount = 0

        while (!visited.containsAll(rooms)) {
            val nextRooms = lastVisited.flatMap { reachableFromRoom(it) }
                    .filter { !visited.contains(it) }
                    .toSet()
            visited.addAll(nextRooms)
            stepsTaken++
            if (stepsTaken >= 1000) {
                farAwayRoomCount += nextRooms.size
            }
            lastVisited = nextRooms
        }

        CrawlResult(stepsTaken, farAwayRoomCount)
    }

    private data class CrawlResult(val maxDistance: Int, val farAwayRoomCount: Int)

    private fun reachableFromRoom(room: Point) = doors.filter { it.contains(room) }
            .flatMap { it.elements }
            .filter { it != room }
            .toSet()
}

typealias Door = UnorderedPair<Point>

class RoomPaths private constructor(private val steps: SerialOptions) {
    companion object {
        fun parse(input: String): RoomPaths {
            val steps = SerialOptions(mutableListOf())

            val insertionPointStack = mutableListOf<InsertionPoint>(steps)

            for (char in input) {
                when (char) {
                    '^' -> Unit
                    '$' -> Unit
                    '(' -> {
                        val nextPoint = insertionPointStack.last().startMultipleChoice()
                        insertionPointStack.add(nextPoint)
                    }
                    '|' -> {
                        insertionPointStack.removeLastWhile { it !is MultipleChoice }
                    }
                    ')' -> {
                        insertionPointStack.removeLastWhile { it !is MultipleChoice }
                        insertionPointStack.removeLast()
                    }
                    else -> {
                        val insertPoint = insertionPointStack.last()
                        val nextPoint = insertionPointStack.last()
                                .addDirection(Direction.valueOf(Character.toString(char)))
                        if (nextPoint != insertPoint) insertionPointStack.add(nextPoint)
                    }
                }
            }

            return RoomPaths(steps)
        }
    }

    fun allPaths() = steps.segments.allPaths()
}

private fun <T> MutableList<T>.removeLast() = this.removeAt(this.size - 1)
private fun <T> MutableList<T>.removeLastWhile(test: (T) -> Boolean) {
    while (this.isNotEmpty() && test(this.last())) {
        this.removeLast()
    }
}

private sealed class PathOption

private interface InsertionPoint {
    fun addDirection(direction: Direction): InsertionPoint
    fun startMultipleChoice(): MultipleChoice
}

/**
 * A single step.
 */
private data class Step(val direction: Direction) : PathOption()

/**
 * A series of segments to be executed in a row.
 */
private data class SerialOptions(val segments: MutableList<PathOption>) : PathOption(), InsertionPoint {
    override fun addDirection(direction: Direction): InsertionPoint {
        segments.add(Step(direction))
        return this
    }

    override fun startMultipleChoice(): MultipleChoice {
        val choice = MultipleChoice(mutableListOf())
        segments.add(choice)
        return choice
    }
}

/**
 * Multiple options to fill in for a particular segment.
 */
private data class MultipleChoice(val choices: MutableList<PathOption>) : PathOption(), InsertionPoint {
    override fun addDirection(direction: Direction): InsertionPoint {
        val list = SerialOptions(mutableListOf(Step(direction)))
        choices.add(list)
        return list
    }

    override fun startMultipleChoice(): MultipleChoice {
        val subChoice = MultipleChoice(mutableListOf())
        choices.add(subChoice)
        return subChoice
    }
}

/**
 * Given a set of path options that are all in series, find all possible paths that match those options.
 */
private fun List<PathOption>.allPaths(): Sequence<List<Direction>> {
    if (this.isEmpty()) return sequenceOf(emptyList())

    val first = this.first()
    val rest = this.drop(1)
    val restPaths = rest.allPaths()

    fun List<Direction>.combinedWithRest() = restPaths.map { this + it }

    return when (first) {
        is Step -> listOf(first.direction).combinedWithRest()
        is SerialOptions -> first.segments.allPaths().flatMap { it.combinedWithRest() }
        is MultipleChoice -> first.choices.asSequence().flatMap { listOf(it).allPaths() }
                .flatMap { it.combinedWithRest() }
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day20/input.txt")
            .readText()
            .trim()

    val roomsGrid = RoomsGrid.fromPaths(RoomPaths.parse(input))

    println(roomsGrid.distanceToFurthestRoom())
    println(roomsGrid.farAwayRoomCount())
}