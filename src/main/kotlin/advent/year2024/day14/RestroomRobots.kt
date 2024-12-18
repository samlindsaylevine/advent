package advent.year2024.day14

import advent.meta.readInput
import advent.utils.Point
import advent.utils.times
import java.lang.Math.floorMod

/**
 * --- Day 14: Restroom Redoubt ---
 * One of The Historians needs to use the bathroom; fortunately, you know there's a bathroom near an unvisited location
 * on their list, and so you're all quickly teleported directly to the lobby of Easter Bunny Headquarters.
 * Unfortunately, EBHQ seems to have "improved" bathroom security again after your last visit. The area outside the
 * bathroom is swarming with robots!
 * To get The Historian safely to the bathroom, you'll need a way to predict where the robots will be in the future.
 * Fortunately, they all seem to be moving on the tile floor in predictable straight lines.
 * You make a list (your puzzle input) of all of the robots' current positions (p) and velocities (v), one robot per
 * line. For example:
 * p=0,4 v=3,-3
 * p=6,3 v=-1,-3
 * p=10,3 v=-1,2
 * p=2,0 v=2,-1
 * p=0,0 v=1,3
 * p=3,0 v=-2,-2
 * p=7,6 v=-1,-3
 * p=3,0 v=-1,-2
 * p=9,3 v=2,3
 * p=7,3 v=-1,2
 * p=2,4 v=2,-3
 * p=9,5 v=-3,-3
 *
 * Each robot's position is given as p=x,y where x represents the number of tiles the robot is from the left wall and y
 * represents the number of tiles from the top wall (when viewed from above). So, a position of p=0,0 means the robot
 * is all the way in the top-left corner.
 * Each robot's velocity is given as v=x,y where x and y are given in tiles per second. Positive x means the robot is
 * moving to the right, and positive y means the robot is moving down. So, a velocity of v=1,-2 means that each second,
 * the robot moves 1 tile to the right and 2 tiles up.
 * The robots outside the actual bathroom are in a space which is 101 tiles wide and 103 tiles tall (when viewed from
 * above). However, in this example, the robots are in a space which is only 11 tiles wide and 7 tiles tall.
 * The robots are good at navigating over/under each other (due to a combination of springs, extendable legs, and
 * quadcopters), so they can share the same tile and don't interact with each other. Visually, the number of robots on
 * each tile in this example looks like this:
 * 1.12.......
 * ...........
 * ...........
 * ......11.11
 * 1.1........
 * .........1.
 * .......1...
 *
 * These robots have a unique feature for maximum bathroom security: they can teleport. When a robot would run into an
 * edge of the space they're in, they instead teleport to the other side, effectively wrapping around the edges. Here
 * is what robot p=2,4 v=2,-3 does for the first few seconds:
 * Initial state:
 * ...........
 * ...........
 * ...........
 * ...........
 * ..1........
 * ...........
 * ...........
 *
 * After 1 second:
 * ...........
 * ....1......
 * ...........
 * ...........
 * ...........
 * ...........
 * ...........
 *
 * After 2 seconds:
 * ...........
 * ...........
 * ...........
 * ...........
 * ...........
 * ......1....
 * ...........
 *
 * After 3 seconds:
 * ...........
 * ...........
 * ........1..
 * ...........
 * ...........
 * ...........
 * ...........
 *
 * After 4 seconds:
 * ...........
 * ...........
 * ...........
 * ...........
 * ...........
 * ...........
 * ..........1
 *
 * After 5 seconds:
 * ...........
 * ...........
 * ...........
 * .1.........
 * ...........
 * ...........
 * ...........
 *
 * The Historian can't wait much longer, so you don't have to simulate the robots for very long. Where will the robots
 * be after 100 seconds?
 * In the above example, the number of robots on each tile after 100 seconds has elapsed looks like this:
 * ......2..1.
 * ...........
 * 1..........
 * .11........
 * .....1.....
 * ...12......
 * .1....1....
 *
 * To determine the safest area, count the number of robots in each quadrant after 100 seconds. Robots that are exactly
 * in the middle (horizontally or vertically) don't count as being in any quadrant, so the only relevant robots are:
 * ..... 2..1.
 * ..... .....
 * 1.... .....
 *
 * ..... .....
 * ...12 .....
 * .1... 1....
 *
 * In this example, the quadrants contain 1, 3, 4, and 1 robot. Multiplying these together gives a total safety factor
 * of 12.
 * Predict the motion of the robots in your list within a space which is 101 tiles wide and 103 tiles tall. What will
 * the safety factor be after exactly 100 seconds have elapsed?
 *
 * --- Part Two ---
 * During the bathroom break, someone notices that these robots seem awfully similar to ones built and used at the
 * North Pole. If they're the same type of robots, they should have a hard-coded Easter egg: very rarely, most of the
 * robots should arrange themselves into a picture of a Christmas tree.
 * What is the fewest number of seconds that must elapse for the robots to display the Easter egg?
 *
 */
data class RestroomRobots(val room: RestroomRobotRoom, val robots: List<RestroomRobot>) {
    companion object {
        fun of(input: String, room: RestroomRobotRoom = RestroomRobotRoom()) =
            RestroomRobots(room, input.trim().lines().map { RestroomRobot.of(it, room) })
    }

    fun next(amount: Int) = RestroomRobots(room, robots.map { it.next(amount) })

    fun safetyFactor(): Int {
        val quadrantWidth = room.width / 2
        val quadrantHeight = room.height / 2
        return robotsInRange(0 until quadrantWidth, 0 until quadrantHeight) *
                robotsInRange(room.width - quadrantWidth until room.width, 0 until quadrantHeight) *
                robotsInRange(0 until quadrantWidth, room.height - quadrantHeight until room.height) *
                robotsInRange(
                    room.width - quadrantWidth until room.width,
                    room.height - quadrantHeight until room.height
                )
    }

    private fun robotsInRange(xRange: IntRange, yRange: IntRange) =
        robots.count { it.position.x in xRange && it.position.y in yRange }

    fun inDistinctPositions() = robots.map { it.position }.toSet().size == robots.size
}

class RestroomRobotRoom(val width: Int = 101, val height: Int = 103)

data class RestroomRobot(val position: Point, val velocity: Point, val room: RestroomRobotRoom) {
    companion object {
        fun of(input: String, room: RestroomRobotRoom): RestroomRobot {
            val regex = """p=(\d+),(\d+) v=(-?\d+),(-?\d+)""".toRegex()
            val match = regex.matchEntire(input) ?: throw IllegalArgumentException("Unparseable robot $input")
            val (px, py, vx, vy) = match.destructured
            return RestroomRobot(Point(px.toInt(), py.toInt()), Point(vx.toInt(), vy.toInt()), room)
        }
    }

    fun next(amount: Int): RestroomRobot {
        val next = position + amount * velocity
        val nextWrapped = Point(floorMod(next.x, room.width), floorMod(next.y, room.height))
        return RestroomRobot(nextWrapped, velocity, room)
    }
}

fun main() {
    val robots = RestroomRobots.of(readInput())

    println(robots.next(100).safetyFactor())

    // I've got a weird feeling that the easter egg will be visible when all the robots are in
    // distinct positions... this is certainly not guaranteed, but from the way the problem is written,
    // it feels like it might be the case.
    val distinctPositionTime = generateSequence(0) { i -> i + 1 }.first {
        robots.next(it).inDistinctPositions()
    }
    println(distinctPositionTime)

    // And indeed, when we display the robots and advance to that time, we see the tree!
    RestroomRobotsDisplay(robots, pausePlayingAt = distinctPositionTime)

}