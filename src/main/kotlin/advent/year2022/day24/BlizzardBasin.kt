package advent.year2022.day24

import advent.utils.*
import java.io.File

/**
 * We're going to make the simplifying assumption, based on the actual input and the example, that the basin is
 * rectangular in shape. We can also note that there are no up or down blizzards in the columns with the entry or
 * exit, so that all the blizzards are confined to the internal rectangle.
 *
 * We'll capture the width and height of that internal rectangle, to make wrap-around easier with modular arithmetic.
 * We'll also make the upper left of the internal rectangle our (0, 0) point, with Y increasing upwards.
 */
class BlizzardBasin(
  val width: Int,
  val height: Int,
  private val blizzards: Set<Blizzard>
) {
  constructor(input: String) : this(
    input.lines().first().length - 2,
    input.lines().size - 2,
    input.lines().flatMapIndexed { y, line ->
      line.mapIndexedNotNull { x, c ->
        c.asDirectionArrow()?.let { direction -> Blizzard(Point(x - 1, 1 - y), direction) }
      }
    }.toSet()
  )

  val start = Point(0, 1)
  val end = Point(width - 1, -height)

  fun timeToEscape(): Int {
    val shortestPaths = ShortestPathFinder().find<BlizzardTraversal>(
      start = BlizzardTraversal(start, 0),
      end = EndCondition { it.position == end },
      nextSteps = Steps { this.legalMoves(it) },
      collapse = CollapseOnCurrentState(),
      reportEvery = 1
    )
    return shortestPaths.first().totalCost
  }

  private fun legalMoves(traversal: BlizzardTraversal): Set<BlizzardTraversal> {
    val turn = traversal.turnsElapsed + 1
    val blizzardPositions = blizzards.map { it.positionAt(turn) }.toSet()

    val possibleDeltas = Direction.values().map { it.toPoint() } + Point(0, 0)
    val possibleNextPositions = possibleDeltas.map { it + traversal.position }
    val legalNextPositions = possibleNextPositions.filter {
      (it == end || it == start || (it.x in 0 until width && it.y in -height + 1..0))
          && it !in blizzardPositions
    }
    return legalNextPositions.map { BlizzardTraversal(it, turn) }.toSet()
  }

  private fun Blizzard.positionAt(time: Int): Point {
    val moved = this.initialPosition + time * this.direction.toPoint()
    val modHeight = moved.y % height
    val newHeight = if (modHeight > 0) modHeight - height else modHeight
    val output = Point(Math.floorMod(moved.x, width), newHeight)

    // Sanity check.
    if (output.x !in 0 until width || output.y !in -height + 1..0) {
      throw IllegalStateException("Blizzard in a bad spot! $this, time $time, result $output")
    }

    return output
  }
}

private fun Char.asDirectionArrow(): Direction? = when (this) {
  '<' -> Direction.W
  '^' -> Direction.N
  '>' -> Direction.E
  'v' -> Direction.S
  else -> null
}

data class Blizzard(val initialPosition: Point, val direction: Direction)

data class BlizzardTraversal(val position: Point, val turnsElapsed: Int)

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day24/input.txt").readText().trimEnd()

  val basin = BlizzardBasin(input)

  println(basin.timeToEscape())
}