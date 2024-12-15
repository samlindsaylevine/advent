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

    private fun attempt(warehouseObject: WarehouseObject, move: Move): RobotOperatedWarehouse {
        val targetSpaces = warehouseObject.spaces().map { it + move.direction }.toSet()
        val moved = warehouseObject.movedTo(warehouseObject.position + move.direction)
        val objectsInTheWay =
            objects.filter { (it.spaces() intersect targetSpaces).isNotEmpty() && it != warehouseObject }
        if (objectsInTheWay.any { it is Wall }) return this
        if (objectsInTheWay.isEmpty()) return this.copy(objects = objects - warehouseObject + moved)
        val withNextObjectsPushed =
            objectsInTheWay.fold(this) { warehouse, nextObject -> warehouse.attempt(nextObject, move) }
        if (targetSpaces.any { space -> withNextObjectsPushed.objects.any { it != warehouseObject && space in it.spaces() } }) return this
        return withNextObjectsPushed.copy(objects = withNextObjectsPushed.objects - warehouseObject + moved)
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