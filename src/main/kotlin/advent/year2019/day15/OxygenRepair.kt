package advent.year2019.day15

import advent.utils.Direction
import advent.utils.Point
import advent.utils.ShortestPathFinder
import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import java.lang.Thread.sleep
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class OxygenRepair(private val droidProgram: List<Long>) {

    fun explore(): OxygenMap {
        val start = Point(0, 0)
        val map = OxygenMap(start.adjacentNeighbors,
                emptySet(),
                setOf(start),
                null)
        val droid = RepairDroid(droidProgram, start)
        return explore(map, droid)
    }

    private tailrec fun explore(map: OxygenMap,
                                droid: RepairDroid): OxygenMap {

        // We're assuming, based on the problem statement, that this is a finite enclosed area with walls, and we can
        // explore all of it.
        val nextTarget = map.unexplored.minBy { it.distanceFrom(droid.position) }
                ?: return map
        val path = map.path(droid.position, nextTarget)

        val newMap = when (droid.walk(path)) {
            LocationState.WALL -> map.withWall(nextTarget)
            LocationState.OPEN -> map.withOpen(nextTarget)
            LocationState.OXYGEN -> map.withOxygen(nextTarget)
        }

        // If you want a fun visualization on a terminal that supports ANSI escape codes.
        //        System.out.print("\u001B[H");
        //        println(newMap)
        //        sleep(20)

        return explore(newMap, droid)
    }
}

class OxygenMap(val unexplored: Set<Point>,
                val walls: Set<Point>,
                val open: Set<Point>,
                val oxygenSystem: Point?) {

    fun path(from: Point, to: Point): List<Direction> {
        val finder = ShortestPathFinder()

        val paths = finder.find(from,
                to,
                { point -> point.adjacentNeighbors.filter { open.contains(it) || it == to }.toSet() })

        val allPoints = listOf(from) + paths.first().steps

        return allPoints.toDirections()
    }

    private fun Point.unexploredNeighbors() = this.adjacentNeighbors
            .filter { !open.contains(it) && !walls.contains(it) }

    fun withWall(wall: Point) = OxygenMap(unexplored - wall,
            walls + wall,
            open,
            oxygenSystem)

    fun withOpen(newOpen: Point) = OxygenMap(unexplored - newOpen + newOpen.unexploredNeighbors(),
            walls,
            open + newOpen,
            oxygenSystem)

    fun withOxygen(oxygen: Point) = OxygenMap(unexplored - oxygen + oxygen.unexploredNeighbors(),
            walls,
            open + oxygen,
            oxygen)

    /**
     * This is just a for-fun visualization where I already know what size the map will end up being.
     */
    override fun toString() = (-19..21).joinToString("\n") { y ->
        (-21..19).joinToString("") { x ->
            val point = Point(x, y)
            when {
                walls.contains(point) -> "#"
                oxygenSystem == point -> "O"
                open.contains(point) -> "."
                else -> " "
            }
        }
    }

    fun timeForOxygenToDiffuse() = timeForOxygenToDiffuse(open,
            setOf(oxygenSystem!!),
            0)

    private tailrec fun timeForOxygenToDiffuse(vacuums: Set<Point>,
                                               justOxygenated: Set<Point>,
                                               timeElapsed: Int): Int =
            if (vacuums.isEmpty()) {
                timeElapsed
            } else {
                val oxygenated = justOxygenated.flatMap { it.adjacentNeighbors }
                        .filter { vacuums.contains(it) }
                        .toSet()
                timeForOxygenToDiffuse(vacuums - oxygenated,
                        oxygenated,
                        timeElapsed + 1)
            }
}

fun List<Point>.toDirections(): List<Direction> = this.zipWithNext().map {
    val delta = it.second - it.first
    Direction.values().first { dir -> dir.toPoint() == delta }
}

private class RepairDroid(val droidProgram: List<Long>,
                          var position: Point) {
    private val inputs = LinkedBlockingQueue<Long>()
    private val outputs = LinkedBlockingQueue<Long>()

    init {
        thread(start = true, isDaemon = true) {
            val computer = IntcodeComputer()
            computer.execute(droidProgram,
                    input = inputs::take,
                    output = outputs::put)
        }
    }

    fun move(direction: Direction): LocationState {
        inputs.put(when (direction) {
            Direction.N -> 1
            Direction.S -> 2
            Direction.W -> 3
            Direction.E -> 4
        })

        val output = outputs.take()

        val state = LocationState.values().first { it.code == output }

        if (state != LocationState.WALL) position += direction.toPoint()

        return state
    }

    /**
     * Walks an entire list of directions, assuming that all of them but the last will be open; then walks the final
     * direction and returns the state there.
     */
    fun walk(directions: List<Direction>): LocationState {
        directions.dropLast(1).forEach { this.move(it) }
        return move(directions.last())
    }
}

private enum class LocationState(val code: Long) {
    WALL(0),
    OPEN(1),
    OXYGEN(2)
}

fun main() {
    val program = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day15/input.txt")

    val oxygenRepair = OxygenRepair(program)

    val map = oxygenRepair.explore()

    val path = map.path(Point(0, 0), map.oxygenSystem ?: throw IllegalStateException("Should be oxygen"))

    println(path.size)
    println(map.timeForOxygenToDiffuse())
}