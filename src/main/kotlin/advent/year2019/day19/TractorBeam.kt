package advent.year2019.day19

import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import advent.year2019.day5.asInput

class DroneSystem(val program: List<Long>) {
  fun isBeingPulled(x: Long, y: Long): Boolean {
    require(x >= 0)
    require(y >= 0)
    val computer = IntcodeComputer()
    val result = computer.execute(
            program = program,
            input = listOf(x, y).asInput()
    )

    return result.output.first() == 1L
  }
}

fun main() {
  val program = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day19/input.txt")

  val droneSystem = DroneSystem(program)

  val pointsAffected = (0L until 50L).sumOf { x ->
    (0L until 50L).count { y -> droneSystem.isBeingPulled(x, y) }
  }

  println(pointsAffected)
}