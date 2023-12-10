package advent.year2023.day10

import advent.utils.Direction
import advent.utils.Point
import advent.utils.size
import advent.utils.takeWhileInclusive
import java.io.File
import kotlin.math.ceil

class PipeField(val tiles: Map<Point, Char>) {
  constructor(input: String) : this(
          input.trim().lines()
                  .flatMapIndexed { y, line ->
                    line.mapIndexed { x, c -> Point(x, y) to c }
                  }
                  .toMap()

  )

  private data class AnimalTraversal(val previous: Point?, val current: Point)

  private fun start() = tiles.entries.find { it.value == 'S' }
          ?.key
          ?: throw IllegalStateException("No start found!")

  private fun Point.connections(): Set<Point> {
    val tile = tiles[this] ?: return emptySet()
    val adjacencies = when (tile) {
      '|' -> setOf(Direction.N, Direction.S)
      '-' -> setOf(Direction.W, Direction.E)
      'L' -> setOf(Direction.N, Direction.E)
      'J' -> setOf(Direction.N, Direction.W)
      '7' -> setOf(Direction.S, Direction.W)
      'F' -> setOf(Direction.S, Direction.E)
      'S' -> Direction.values().toSet()
      else -> emptySet()
    }
    return adjacencies.map { it.toPoint() }
            // Invert Y direction -- since we took y-index of the lines, "N" is down for us.
            .map { Point(it.x, -it.y) }
            .map { this + it }
            .toSet()
  }

  private fun animalPath(): Sequence<AnimalTraversal> = generateSequence(AnimalTraversal(null, start())) { (previous, current) ->
    val neighbors = if (previous == null) current.adjacentNeighbors else (current.adjacentNeighbors - previous)
    val next = neighbors.firstOrNull { current in it.connections() && it in current.connections() }
            ?: throw IllegalStateException("Can't find next step; currently at $current (a ${tiles[current]})")
    AnimalTraversal(current, next)
  }

  fun stepsToFarthest(): Int {
    val path = animalPath()
    val pathLength = path.drop(1)
            .takeWhileInclusive { tiles[it.current] != 'S' }
            .size()
    return ceil((pathLength.toFloat()) / 2).toInt()
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day10/input.txt").readText().trim()
  val field = PipeField(input)

  println(field.stepsToFarthest())
}