package advent.year2019.day11

import advent.utils.Direction
import advent.utils.Point
import advent.year2019.day5.IntcodeComputer
import java.io.File

class HullPaintingRobot(val program: List<Long>) {

    /**
     * Returns the set of points that were painted white.
     */
    fun execute(startOnWhite: Boolean = false): PaintResult {
        var direction = Direction.N
        var position = Point(0, 0)
        val whitePoints = mutableSetOf<Point>()
        val pointsEverPainted = mutableSetOf<Point>()

        if (startOnWhite) whitePoints.add(position)

        // If false, output is for movement.
        var outputPaints = true

        fun cameraInput() = if (whitePoints.contains(position)) 1L else 0L
        // Outputs switch back and forth between controlling paint and controlling turn / move.
        fun consumeOutput(output: Long) {
            if (outputPaints) {
                when (output) {
                    0L -> whitePoints.remove(position)
                    1L -> whitePoints.add(position)
                    else -> throw IllegalArgumentException("Bad output $output")
                }

                pointsEverPainted.add(position)

                outputPaints = false
            } else {
                direction = when (output) {
                    0L -> direction.left()
                    1L -> direction.right()
                    else -> throw IllegalArgumentException("Bad output $output")
                }
                position += direction.toPoint()

                outputPaints = true
            }
        }

        val computer = IntcodeComputer()

        computer.execute(program = program,
                input = ::cameraInput,
                output = ::consumeOutput)

        return PaintResult(whitePoints, pointsEverPainted)
    }

    data class PaintResult(val whitePoints: Set<Point>,
                           val pointsEverPainted: Set<Point>) {
        override fun toString(): String {
            val maxY = whitePoints.map { it.y }.maxOrNull() ?: 0
            val minY = whitePoints.map { it.y }.minOrNull() ?: 0
            val minX = whitePoints.map { it.x }.minOrNull() ?: 0
            val maxX = whitePoints.map { it.x }.maxOrNull() ?: 0

            return (maxY downTo minY).joinToString("\n") { y ->
                (minX..maxX).joinToString("") { x ->
                    if (whitePoints.contains(Point(x, y))) "#" else " "
                }
            }
        }
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day11/input.txt")
            .readText()
            .trim()
            .split(",")
            .map { it.toLong() }

    val robot = HullPaintingRobot(input)

    val result = robot.execute()

    println(result.pointsEverPainted.size)

    val secondPartResult = robot.execute(startOnWhite = true)
    println(secondPartResult)
}