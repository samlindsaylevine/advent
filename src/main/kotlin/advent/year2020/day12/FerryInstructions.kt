package advent.year2020.day12

import advent.utils.Direction
import advent.utils.Point
import advent.utils.times
import advent.year2018.day18.advance
import java.io.File

data class FerryInstructions(val instructions: List<FerryInstruction>) {
  constructor(input: String) : this(input.trim()
          .lines()
          .map { FerryInstruction(it[0], it.drop(1).toInt()) })

  fun execute() = instructions.fold(FerryState(), FerryState::execute)
  fun executeWithWaypoint() = instructions.fold(FerryState(), FerryState::executeWithWaypoint)
}


data class FerryState(val position: Point = Point(0, 0),
                      val direction: Direction = Direction.E,
                      val waypointOffset: Point = Point(10, 1)) {
  fun execute(instruction: FerryInstruction): FerryState = when {
    instruction.action == 'F' -> FerryState(position + instruction.amount * direction.toPoint(), direction)
    instruction.action == 'L' -> FerryState(position, direction.left(instruction.amount))
    instruction.action == 'R' -> FerryState(position, direction.right(instruction.amount))
    instruction.asDirection != null ->
      FerryState(position + instruction.amount * instruction.asDirection.toPoint(), direction)
    else -> throw IllegalArgumentException("Bad instruction $instruction")
  }

  fun executeWithWaypoint(instruction: FerryInstruction): FerryState = when {
    instruction.action == 'F' -> FerryState(position + instruction.amount * waypointOffset, direction, waypointOffset)
    instruction.action == 'L' -> FerryState(position, direction, waypointOffset.rotated(instruction.amount))
    instruction.action == 'R' -> FerryState(position, direction, waypointOffset.rotated(-instruction.amount))
    instruction.asDirection != null ->
      FerryState(position, direction, waypointOffset + instruction.amount * instruction.asDirection.toPoint())
    else -> throw IllegalArgumentException("Bad instruction $instruction")
  }

  fun distanceFromOrigin() = this.position.distanceFrom(Point(0, 0))

  private fun Direction.right(degrees: Int) = this.turn(degrees, Direction::right)
  private fun Direction.left(degrees: Int) = this.turn(degrees, Direction::left)
  private fun Direction.turn(degrees: Int, way: (Direction) -> Direction) = advance((degrees % 360) / 90, this, way)

  private fun Point.rotated(degrees: Int): Point {
    return when (Math.floorMod(degrees, 360)) {
      0 -> this
      90 -> Point(-this.y, this.x)
      180 -> Point(-this.x, -this.y)
      270 -> Point(this.y, -this.x)
      else -> throw IllegalArgumentException("Can't rotate by $degrees, only right angles supported")
    }
  }
}

data class FerryInstruction(val action: Char, val amount: Int) {
  val asDirection = try {
    Direction.valueOf(action.toString())
  } catch (e: IllegalArgumentException) {
    null
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day12/input.txt")
          .readText()

  val instructions = FerryInstructions(input)

  println(instructions.execute().distanceFromOrigin())
  println(instructions.executeWithWaypoint().distanceFromOrigin())
}