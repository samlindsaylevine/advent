package advent.year2018.day20

import advent.utils.Direction
import advent.utils.Point
import advent.utils.UnorderedPair
import advent.year2015.day24.Ticker
import java.io.File

/**
 * --- Day 20: A Regular Map ---
 * While you were learning about instruction pointers, the Elves made considerable progress. When you look up, you
 * discover that the North Pole base construction project has completely surrounded you.
 * The area you are in is made up entirely of rooms and doors. The rooms are arranged in a grid, and rooms only connect
 * to adjacent rooms when a door is present between them.
 * For example, drawing rooms as ., walls as #, doors as | or -, your current position as X, and where north is up, the
 * area you're in might look like this:
 * #####
 * #.|.#
 * #-###
 * #.|X#
 * #####
 * 
 * You get the attention of a passing construction Elf and ask for a map. "I don't have time to draw out a map of this
 * place - it's huge. Instead, I can give you directions to every room in the facility!" He writes down some directions
 * on a piece of parchment and runs off. In the example above, the instructions might have been ^WNE$, a regular
 * expression or "regex" (your puzzle input).
 * The regex matches routes (like WNE for "west, north, east") that will take you from your current room through
 * various doors in the facility. In aggregate, the routes will take you through every door in the facility at least
 * once; mapping out all of these routes will let you build a proper map and find your way around.
 * ^ and $ are at the beginning and end of your regex; these just mean that the regex doesn't match anything outside
 * the routes it describes. (Specifically, ^ matches the start of the route, and $ matches the end of it.) These
 * characters will not appear elsewhere in the regex.
 * The rest of the regex matches various sequences of the characters N (north), S (south), E (east), and W (west). In
 * the example above, ^WNE$ matches only one route, WNE, which means you can move west, then north, then east from your
 * current position. Sequences of letters like this always match that exact route in the same order.
 * Sometimes, the route can branch. A branch is given by a list of options separated by pipes (|) and wrapped in
 * parentheses. So, ^N(E|W)N$ contains a branch: after going north, you must choose to go either east or west before
 * finishing your route by going north again. By tracing out the possible routes after branching, you can determine
 * where the doors are and, therefore, where the rooms are in the facility.
 * For example, consider this regex: ^ENWWW(NEEE|SSE(EE|N))$
 * This regex begins with ENWWW, which means that from your current position, all routes must begin by moving east,
 * north, and then west three times, in that order. After this, there is a branch.  Before you consider the branch,
 * this is what you know about the map so far, with doors you aren't sure about marked with a ?:
 * #?#?#?#?#
 * ?.|.|.|.?
 * #?#?#?#-#
 *     ?X|.?
 *     #?#?#
 * 
 * After this point, there is (NEEE|SSE(EE|N)). This gives you exactly two options: NEEE and SSE(EE|N). By following
 * NEEE, the map now looks like this:
 * #?#?#?#?#
 * ?.|.|.|.?
 * #-#?#?#?#
 * ?.|.|.|.?
 * #?#?#?#-#
 *     ?X|.?
 *     #?#?#
 * 
 * Now, only SSE(EE|N) remains. Because it is in the same parenthesized group as NEEE, it starts from the same room
 * NEEE started in. It states that starting from that point, there exist doors which will allow you to move south
 * twice, then east; this ends up at another branch. After that, you can either move east twice or north once. This
 * information fills in the rest of the doors:
 * #?#?#?#?#
 * ?.|.|.|.?
 * #-#?#?#?#
 * ?.|.|.|.?
 * #-#?#?#-#
 * ?.?.?X|.?
 * #-#-#?#?#
 * ?.|.|.|.?
 * #?#?#?#?#
 * 
 * Once you've followed all possible routes, you know the remaining unknown parts are all walls, producing a finished
 * map of the facility:
 * #########
 * #.|.|.|.#
 * #-#######
 * #.|.|.|.#
 * #-#####-#
 * #.#.#X|.#
 * #-#-#####
 * #.|.|.|.#
 * #########
 * 
 * Sometimes, a list of options can have an empty option, like (NEWS|WNSE|). This means that routes at this point could
 * effectively skip the options in parentheses and move on immediately.  For example, consider this regex and the
 * corresponding map:
 * ^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$
 * 
 * ###########
 * #.|.#.|.#.#
 * #-###-#-#-#
 * #.|.|.#.#.#
 * #-#####-#-#
 * #.#.#X|.#.#
 * #-#-#####-#
 * #.#.|.|.|.#
 * #-###-###-#
 * #.|.|.#.|.#
 * ###########
 * 
 * This regex has one main route which, at three locations, can optionally include additional detours and be valid:
 * (NEWS|), (WNSE|), and (SWEN|). Regardless of which option is taken, the route continues from the position it is left
 * at after taking those steps. So, for example, this regex matches all of the following routes (and more that aren't
 * listed here):
 * 
 * ENNWSWWSSSEENEENNN
 * ENNWSWWNEWSSSSEENEENNN
 * ENNWSWWNEWSSSSEENEESWENNNN
 * ENNWSWWSSSEENWNSEEENNN
 * 
 * By following the various routes the regex matches, a full map of all of the doors and rooms in the facility can be
 * assembled.
 * To get a sense for the size of this facility, you'd like to determine which room is furthest from you: specifically,
 * you would like to find the room for which the shortest path to that room would require passing through the most
 * doors.
 * 
 * In the first example (^WNE$), this would be the north-east corner 3 doors away.
 * In the second example (^ENWWW(NEEE|SSE(EE|N))$), this would be the south-east corner 10 doors away.
 * In the third example (^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$), this would be the north-east corner 18 doors away.
 * 
 * Here are a few more examples:
 * Regex: ^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$
 * Furthest room requires passing 23 doors
 * 
 * #############
 * #.|.|.|.|.|.#
 * #-#####-###-#
 * #.#.|.#.#.#.#
 * #-#-###-#-#-#
 * #.#.#.|.#.|.#
 * #-#-#-#####-#
 * #.#.#.#X|.#.#
 * #-#-#-###-#-#
 * #.|.#.|.#.#.#
 * ###-#-###-#-#
 * #.|.#.|.|.#.#
 * #############
 * 
 * Regex: ^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$
 * Furthest room requires passing 31 doors
 * 
 * ###############
 * #.|.|.|.#.|.|.#
 * #-###-###-#-#-#
 * #.|.#.|.|.#.#.#
 * #-#########-#-#
 * #.#.|.|.|.|.#.#
 * #-#-#########-#
 * #.#.#.|X#.|.#.#
 * ###-#-###-#-#-#
 * #.|.#.#.|.#.|.#
 * #-###-#####-###
 * #.|.#.|.|.#.#.#
 * #-#-#####-#-#-#
 * #.#.|.|.|.#.|.#
 * ###############
 * 
 * What is the largest number of doors you would be required to pass through to reach a room? That is, find the room
 * for which the shortest path from your starting location to that room would require passing through the most doors;
 * what is the fewest doors you can pass through to reach it?
 * 
 * --- Part Two ---
 * Okay, so the facility is big.
 * How many rooms have a shortest path from your current location that pass through at least 1000 doors?
 * 
 */
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