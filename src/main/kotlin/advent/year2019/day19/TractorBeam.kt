package advent.year2019.day19

import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import advent.year2019.day5.asInput

class DroneSystem(val program: List<Long>) {

  // Cache our computations so that we never have to recalculate a particular space more than once.
  private val cache = mutableMapOf<Pair<Long, Long>, Boolean>()

  fun isBeingPulled(x: Long, y: Long) = cache.getOrPut(x to y) { calculateBeingPulled(x, y) }

  fun countClosest(squareLength: Long) = (0L until squareLength).sumOf { x ->
    (0L until squareLength).count { y -> this.isBeingPulled(x, y) }
  }

  fun closestContainedSquare(squareLength: Long): Pair<Long, Long> =
          inOrderOfCloseness().first { squareIsContained(it.first, it.second, squareLength) }

  private fun squareIsContained(upperLeftX: Long, upperleftY: Long, squareLength: Long) =
  // Speed up optimization based on a guess about the nature of the tractor beam: check the 4 corners of the
          // square first!
          isBeingPulled(upperLeftX, upperleftY) &&
                  isBeingPulled(upperLeftX + squareLength - 1, upperleftY) &&
                  isBeingPulled(upperLeftX, upperleftY + squareLength - 1) &&
                  isBeingPulled(upperLeftX + squareLength - 1, upperleftY + squareLength - 1) &&
                  // OK, also check the entire interior of the square.
                  (upperLeftX until upperLeftX + squareLength).all { x ->
                    (upperleftY until upperleftY + squareLength).all { y -> isBeingPulled(x, y) }
                  }

  private fun inOrderOfCloseness(): Sequence<Pair<Long, Long>> = sequence {
    var current = 0L to 0L

    while (true) {
      yield(current)
      current = if (current.second == 0L) {
        println("Distance ${current.first + 1}")
        0L to current.first + 1
      } else {
        current.first + 1 to current.second - 1
      }
    }
  }

  private fun calculateBeingPulled(x: Long, y: Long): Boolean {
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

  println(droneSystem.countClosest(50))

  // This takes about 8 minutes, so there's probably some way to speed it up... but CPU time is cheaper than developer
  // time. Ship it!
  val closestSquare = droneSystem.closestContainedSquare(100)

  println(closestSquare.first * 10000 + closestSquare.second)
}