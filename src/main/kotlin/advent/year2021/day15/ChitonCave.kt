package advent.year2021.day15

import advent.utils.*
import java.io.File

class ChitonCave(val riskLevels: Map<Point, Int>) {
  constructor(input: String) : this(
    input.lines().flatMapIndexed { y, line ->
      line.mapIndexed { x, c -> Point(x, y) to c.digitToInt() }
    }.toMap()
  )

  private fun nextSteps(point: Point): Set<Step<Point>> = point.adjacentNeighbors
    .mapNotNull { neighbor -> riskLevels[neighbor]?.let { risk -> Step(neighbor, risk) } }
    .toSet()

  private val endPoint = Point(riskLevels.keys.maxOf { it.x }, riskLevels.keys.maxOf { it.y })

  fun lowestRiskPath() = ShortestPathFinder().find(
    start = Point(0, 0),
    end = EndState(endPoint),
    nextSteps = StepsWithCost { this.nextSteps(it) },
    collapse = Collapse { it.last() }
  ).first()
}

fun main() {
  val cave = File("src/main/kotlin/advent/year2021/day15/input.txt")
    .readText()
    .let(::ChitonCave)

  println(cave.lowestRiskPath().totalCost)
}