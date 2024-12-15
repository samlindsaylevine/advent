package advent.year2024.day15

import advent.meta.readInput
import advent.utils.Direction
import advent.utils.Point

data class RobotOperatedWarehouse(
    val robot: Point,
    val boxes: Set<Point>,
    val walls: Set<Point>,
    val instructions: List<Move>
) {
    companion object {
        fun of(input: String): RobotOperatedWarehouse {
            val (mapSection, instructionSection) = input.split("\n\n")
            val lines = mapSection.lines()
            val charsByPoint: List<Pair<Point, Char>> = lines.flatMapIndexed { y, line ->
                line.mapIndexed { x, c -> Point(x, y) to c }
            }
            val robot = charsByPoint.first { it.second == '@' }.first
            val boxes = charsByPoint.filter { it.second == 'O' }.map { it.first }.toSet()
            val walls = charsByPoint.filter { it.second == '#' }.map { it.first }.toSet()

            val instructionString = instructionSection.replace("\n", "").trim()
            val instructions = instructionString.map(Move::of)
            return RobotOperatedWarehouse(robot, boxes, walls, instructions)
        }
    }

    enum class Move(val char: Char, val direction: Direction) {
        // Note that the up & down directions are inverted because positive Y is towards the bottom of the warehouse.
        UP('^', Direction.S),
        DOWN('v', Direction.N),
        LEFT('<', Direction.W),
        RIGHT('>', Direction.E);

        companion object {
            fun of(char: Char) = Move.values().first { it.char == char }
        }
    }

    fun attempt(move: Move): RobotOperatedWarehouse =
        when (val targetSpace = robot + move.direction.toPoint()) {
            in walls -> this
            in boxes -> {
                val withBoxPushed = attemptBoxMove(targetSpace, move)
                if (targetSpace in withBoxPushed.boxes) this else withBoxPushed.copy(robot = targetSpace)
            }

            else -> this.copy(robot = targetSpace)
        }

    // Try to move the box at the specified point with the specified move.
    private fun attemptBoxMove(box: Point, move: Move): RobotOperatedWarehouse =
        when (val targetSpace = box + move.direction.toPoint()) {
            in walls -> this
            in boxes -> {
                val withNextBoxPushed = attemptBoxMove(targetSpace, move)
                if (targetSpace in withNextBoxPushed.boxes) this else withNextBoxPushed.copy(boxes = (withNextBoxPushed.boxes - box) + targetSpace)
            }

            else -> this.copy(boxes = (boxes - box) + targetSpace)
        }

    private fun Point.gpsCoordinate() = 100 * y + x
    fun afterMoves() = instructions.fold(this) { warehouse, move -> warehouse.attempt(move) }
    fun gpsSum() = boxes.sumOf { it.gpsCoordinate() }

    override fun toString(): String = (0..walls.maxOf { it.y }).joinToString(separator = "\n") { y ->
        (0..walls.maxOf { it.x }).joinToString(separator = "") { x ->
            when (Point(x, y)) {
                robot -> "@"
                in boxes -> "O"
                in walls -> "#"
                else -> "."
            }
        }
    }
}

fun main() {
    val warehouse = RobotOperatedWarehouse.of(readInput())

    println(warehouse.afterMoves().gpsSum())
}