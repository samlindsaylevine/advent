package advent.year2019.day15

import advent.utils.*
import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

/**
 * --- Day 15: Oxygen System ---
 * Out here in deep space, many things can go wrong. Fortunately, many of those things have indicator lights.
 * Unfortunately, one of those lights is lit: the oxygen system for part of the ship has failed!
 * According to the readouts, the oxygen system must have failed days ago after a rupture in oxygen tank two; that
 * section of the ship was automatically sealed once oxygen levels went dangerously low. A single remotely-operated
 * repair droid is your only option for fixing the oxygen system.
 * The Elves' care package included an Intcode program (your puzzle input) that you can use to remotely control the
 * repair droid. By running that program, you can direct the repair droid to the oxygen system and fix the problem.
 * The remote control program executes the following steps in a loop forever:
 * 
 * Accept a movement command via an input instruction.
 * Send the movement command to the repair droid.
 * Wait for the repair droid to finish the movement operation.
 * Report on the status of the repair droid via an output instruction.
 * 
 * Only four movement commands are understood: north (1), south (2), west (3), and east (4). Any other command is
 * invalid. The movements differ in direction, but not in distance: in a long enough east-west hallway, a series of
 * commands like 4,4,4,4,3,3,3,3 would leave the repair droid back where it started.
 * The repair droid can reply with any of the following status codes:
 * 
 * 0: The repair droid hit a wall. Its position has not changed.
 * 1: The repair droid has moved one step in the requested direction.
 * 2: The repair droid has moved one step in the requested direction; its new position is the location of the oxygen
 * system.
 * 
 * You don't know anything about the area around the repair droid, but you can figure it out by watching the status
 * codes.
 * For example, we can draw the area using D for the droid, # for walls, . for locations the droid can traverse, and
 * empty space for unexplored locations.  Then, the initial state looks like this:
 *       
 *       
 *    D  
 *       
 *       
 * 
 * To make the droid go north, send it 1. If it replies with 0, you know that location is a wall and that the droid
 * didn't move:
 *       
 *    #  
 *    D  
 *       
 *       
 * 
 * To move east, send 4; a reply of 1 means the movement was successful:
 *       
 *    #  
 *    .D 
 *       
 *       
 * 
 * Then, perhaps attempts to move north (1), south (2), and east (4) are all met with replies of 0:
 *       
 *    ## 
 *    .D#
 *     # 
 *       
 * 
 * Now, you know the repair droid is in a dead end. Backtrack with 3 (which you already know will get a reply of 1
 * because you already know that location is open):
 *       
 *    ## 
 *    D.#
 *     # 
 *       
 * 
 * Then, perhaps west (3) gets a reply of 0, south (2) gets a reply of 1, south again (2) gets a reply of 0, and then
 * west (3) gets a reply of 2:
 *       
 *    ## 
 *   #..#
 *   D.# 
 *    #  
 * 
 * Now, because of the reply of 2, you know you've found the oxygen system! In this example, it was only 2 moves away
 * from the repair droid's starting position.
 * What is the fewest number of movement commands required to move the repair droid from its starting position to the
 * location of the oxygen system?
 * 
 * --- Part Two ---
 * You quickly repair the oxygen system; oxygen gradually fills the area.
 * Oxygen starts in the location containing the repaired oxygen system. It takes one minute for oxygen to spread to all
 * open locations that are adjacent to a location that already contains oxygen. Diagonal locations are not adjacent.
 * In the example above, suppose you've used the droid to explore the area fully and have the following map (where
 * locations that currently contain oxygen are marked O):
 *  ##   
 * #..## 
 * #.#..#
 * #.O.# 
 *  ###  
 * 
 * Initially, the only location which contains oxygen is the location of the repaired oxygen system.  However, after
 * one minute, the oxygen spreads to all open (.) locations that are adjacent to a location containing oxygen:
 *  ##   
 * #..## 
 * #.#..#
 * #OOO# 
 *  ###  
 * 
 * After a total of two minutes, the map looks like this:
 *  ##   
 * #..## 
 * #O#O.#
 * #OOO# 
 *  ###  
 * 
 * After a total of three minutes:
 *  ##   
 * #O.## 
 * #O#OO#
 * #OOO# 
 *  ###  
 * 
 * And finally, the whole region is full of oxygen after a total of four minutes:
 *  ##   
 * #OO## 
 * #O#OO#
 * #OOO# 
 *  ###  
 * 
 * So, in this example, all locations contain oxygen after 4 minutes.
 * Use the repair droid to get a complete map of the area. How many minutes will it take to fill with oxygen?
 * 
 */
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
        val nextTarget = map.unexplored.minByOrNull { it.distanceFrom(droid.position) }
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
                EndState(to),
                Steps { point -> point.adjacentNeighbors.filter { open.contains(it) || it == to }.toSet() })

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