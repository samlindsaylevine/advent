package advent.year2024.day15

import advent.meta.readInput
import advent.utils.Direction
import advent.utils.Point

data class RobotOperatedWarehouse(
    val objects: Set<WarehouseObject>,
    val instructions: List<Move>
) {
    companion object {
        fun of(input: String): RobotOperatedWarehouse {
            val (mapSection, instructionSection) = input.split("\n\n")
            val lines = mapSection.lines()
            val charsByPoint: List<Pair<Point, Char>> = lines.flatMapIndexed { y, line ->
                line.mapIndexed { x, c -> Point(x, y) to c }
            }
            val objects = charsByPoint.mapNotNull { (point, c) ->
                when (c) {
                    '@' -> Robot(point)
                    'O' -> Box(point)
                    '#' -> Wall(point)
                    '[' -> WideBox(point)
                    else -> null
                }
            }.toSet()

            val instructionString = instructionSection.replace("\n", "").trim()
            val instructions = instructionString.map(Move::of)
            return RobotOperatedWarehouse(objects, instructions)
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

    private operator fun Point.plus(direction: Direction) = this + direction.toPoint()

    fun attempt(move: Move): RobotOperatedWarehouse = attempt(objects.first { it is Robot }, move)

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
    fun gpsSum() = objects.filter { it is Box || it is WideBox }.sumOf { it.position.gpsCoordinate() }

    override fun toString(): String = (0..objects.maxOf { it.position.y }).joinToString(separator = "\n") { y ->
        (0..objects.maxOf { it.position.x }).joinToString(separator = "") { x ->
            val point = Point(x, y)
            when {
                objects.any { point == it.position && it is Robot } -> "@"
                objects.any { point == it.position && it is Box } -> "O"
                objects.any { point == it.position && it is Wall } -> "#"
                objects.any { point == it.position && it is WideBox } -> "["
                objects.any { point == it.position + Direction.E && it is WideBox } -> "]"
                else -> "."
            }
        }
    }

    sealed class WarehouseObject(val position: Point) {
        open fun spaces(): Set<Point> = setOf(position)
        abstract fun movedTo(newPosition: Point): WarehouseObject
    }

    class Robot(position: Point) : WarehouseObject(position) {
        override fun movedTo(newPosition: Point) = Robot(newPosition)
    }

    class Wall(position: Point) : WarehouseObject(position) {
        override fun movedTo(newPosition: Point) = throw IllegalStateException("Walls don't move!")
    }

    class Box(position: Point) : WarehouseObject(position) {
        override fun movedTo(newPosition: Point) = Box(newPosition)
    }

    class WideBox(position: Point) : WarehouseObject(position) {
        override fun spaces(): Set<Point> = setOf(position, position + Direction.E.toPoint())
        override fun movedTo(newPosition: Point) = WideBox(newPosition)
    }
}

fun String.widerWarehouse() = this.replace("#", "##")
    .replace("O", "[]")
    .replace(".", "..")
    .replace("@", "@.")

fun main() {
    val warehouse = RobotOperatedWarehouse.of(readInput())
    println(warehouse.afterMoves().gpsSum())

    val widened = RobotOperatedWarehouse.of(readInput().widerWarehouse())
    println(widened.afterMoves().gpsSum())
}