package advent.year2024.day14

import advent.meta.readInput
import advent.utils.Point
import advent.utils.times
import java.lang.Math.floorMod

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