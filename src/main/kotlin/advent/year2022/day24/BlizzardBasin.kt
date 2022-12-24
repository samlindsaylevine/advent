package advent.year2022.day24

import advent.utils.*
import java.io.File

/**
 * --- Day 24: Blizzard Basin ---
 * With everything replanted for next year (and with elephants and monkeys to tend the grove), you and the Elves leave
 * for the extraction point.
 * Partway up the mountain that shields the grove is a flat, open area that serves as the extraction point. It's a bit
 * of a climb, but nothing the expedition can't handle.
 * At least, that would normally be true; now that the mountain is covered in snow, things have become more difficult
 * than the Elves are used to.
 * As the expedition reaches a valley that must be traversed to reach the extraction site, you find that strong,
 * turbulent winds are pushing small blizzards of snow and sharp ice around the valley. It's a good thing everyone
 * packed warm clothes! To make it across safely, you'll need to find a way to avoid them.
 * Fortunately, it's easy to see all of this from the entrance to the valley, so you make a map of the valley and the
 * blizzards (your puzzle input). For example:
 * #.#####
 * #.....#
 * #>....#
 * #.....#
 * #...v.#
 * #.....#
 * #####.#
 *
 * The walls of the valley are drawn as #; everything else is ground. Clear ground - where there is currently no
 * blizzard - is drawn as .. Otherwise, blizzards are drawn with an arrow indicating their direction of motion: up (^),
 * down (v), left (<), or right (>).
 * The above map includes two blizzards, one moving right (>) and one moving down (v). In one minute, each blizzard
 * moves one position in the direction it is pointing:
 * #.#####
 * #.....#
 * #.>...#
 * #.....#
 * #.....#
 * #...v.#
 * #####.#
 *
 * Due to conservation of blizzard energy, as a blizzard reaches the wall of the valley, a new blizzard forms on the
 * opposite side of the valley moving in the same direction. After another minute, the bottom downward-moving blizzard
 * has been replaced with a new downward-moving blizzard at the top of the valley instead:
 * #.#####
 * #...v.#
 * #..>..#
 * #.....#
 * #.....#
 * #.....#
 * #####.#
 *
 * Because blizzards are made of tiny snowflakes, they pass right through each other. After another minute, both
 * blizzards temporarily occupy the same position, marked 2:
 * #.#####
 * #.....#
 * #...2.#
 * #.....#
 * #.....#
 * #.....#
 * #####.#
 *
 * After another minute, the situation resolves itself, giving each blizzard back its personal space:
 * #.#####
 * #.....#
 * #....>#
 * #...v.#
 * #.....#
 * #.....#
 * #####.#
 *
 * Finally, after yet another minute, the rightward-facing blizzard on the right is replaced with a new one on the left
 * facing the same direction:
 * #.#####
 * #.....#
 * #>....#
 * #.....#
 * #...v.#
 * #.....#
 * #####.#
 *
 * This process repeats at least as long as you are observing it, but probably forever.
 * Here is a more complex example:
 * #.######
 * #>>.<^<#
 * #.<..<<#
 * #>v.><>#
 * #<^v^^>#
 * ######.#
 *
 * Your expedition begins in the only non-wall position in the top row and needs to reach the only non-wall position in
 * the bottom row. On each minute, you can move up, down, left, or right, or you can wait in place. You and the
 * blizzards act simultaneously, and you cannot share a position with a blizzard.
 * In the above example, the fastest way to reach your goal requires 18 steps. Drawing the position of the expedition
 * as E, one way to achieve this is:
 * Initial state:
 * #E######
 * #>>.<^<#
 * #.<..<<#
 * #>v.><>#
 * #<^v^^>#
 * ######.#
 *
 * Minute 1, move down:
 * #.######
 * #E>3.<.#
 * #<..<<.#
 * #>2.22.#
 * #>v..^<#
 * ######.#
 *
 * Minute 2, move down:
 * #.######
 * #.2>2..#
 * #E^22^<#
 * #.>2.^>#
 * #.>..<.#
 * ######.#
 *
 * Minute 3, wait:
 * #.######
 * #<^<22.#
 * #E2<.2.#
 * #><2>..#
 * #..><..#
 * ######.#
 *
 * Minute 4, move up:
 * #.######
 * #E<..22#
 * #<<.<..#
 * #<2.>>.#
 * #.^22^.#
 * ######.#
 *
 * Minute 5, move right:
 * #.######
 * #2Ev.<>#
 * #<.<..<#
 * #.^>^22#
 * #.2..2.#
 * ######.#
 *
 * Minute 6, move right:
 * #.######
 * #>2E<.<#
 * #.2v^2<#
 * #>..>2>#
 * #<....>#
 * ######.#
 *
 * Minute 7, move down:
 * #.######
 * #.22^2.#
 * #<vE<2.#
 * #>>v<>.#
 * #>....<#
 * ######.#
 *
 * Minute 8, move left:
 * #.######
 * #.<>2^.#
 * #.E<<.<#
 * #.22..>#
 * #.2v^2.#
 * ######.#
 *
 * Minute 9, move up:
 * #.######
 * #<E2>>.#
 * #.<<.<.#
 * #>2>2^.#
 * #.v><^.#
 * ######.#
 *
 * Minute 10, move right:
 * #.######
 * #.2E.>2#
 * #<2v2^.#
 * #<>.>2.#
 * #..<>..#
 * ######.#
 *
 * Minute 11, wait:
 * #.######
 * #2^E^2>#
 * #<v<.^<#
 * #..2.>2#
 * #.<..>.#
 * ######.#
 *
 * Minute 12, move down:
 * #.######
 * #>>.<^<#
 * #.<E.<<#
 * #>v.><>#
 * #<^v^^>#
 * ######.#
 *
 * Minute 13, move down:
 * #.######
 * #.>3.<.#
 * #<..<<.#
 * #>2E22.#
 * #>v..^<#
 * ######.#
 *
 * Minute 14, move right:
 * #.######
 * #.2>2..#
 * #.^22^<#
 * #.>2E^>#
 * #.>..<.#
 * ######.#
 *
 * Minute 15, move right:
 * #.######
 * #<^<22.#
 * #.2<.2.#
 * #><2>E.#
 * #..><..#
 * ######.#
 *
 * Minute 16, move right:
 * #.######
 * #.<..22#
 * #<<.<..#
 * #<2.>>E#
 * #.^22^.#
 * ######.#
 *
 * Minute 17, move down:
 * #.######
 * #2.v.<>#
 * #<.<..<#
 * #.^>^22#
 * #.2..2E#
 * ######.#
 *
 * Minute 18, move down:
 * #.######
 * #>2.<.<#
 * #.2v^2<#
 * #>..>2>#
 * #<....>#
 * ######E#
 *
 * What is the fewest number of minutes required to avoid the blizzards and reach the goal?
 *
 * --- Part Two ---
 * As the expedition reaches the far side of the valley, one of the Elves looks especially dismayed:
 * He forgot his snacks at the entrance to the valley!
 * Since you're so good at dodging blizzards, the Elves humbly request that you go back for his snacks. From the same
 * initial conditions, how quickly can you make it from the start to the goal, then back to the start, then back to the
 * goal?
 * In the above example, the first trip to the goal takes 18 minutes, the trip back to the start takes 23 minutes, and
 * the trip back to the goal again takes 13 minutes, for a total time of 54 minutes.
 * What is the fewest number of minutes required to reach the goal, go back to the start, then reach the goal again?
 *
 */

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

  fun timeToEscape() = timeToMove(start, end, 0)
  fun timeToRoundTripForSnacks(): Int {
    val outbound = timeToEscape()
    val goBackForSnacks = timeToMove(end, start, outbound)
    val reachGoalAgain = timeToMove(start, end, outbound + goBackForSnacks)
    return outbound + goBackForSnacks + reachGoalAgain
  }

  private fun timeToMove(beginning: Point, target: Point, startingTime: Int): Int {
    val shortestPaths = ShortestPathFinder().find(
      start = BlizzardTraversal(beginning, startingTime),
      end = EndCondition { it.position == target },
      nextSteps = Steps { this.legalMoves(it) },
      collapse = CollapseOnCurrentState(),
      // reportEvery = 1
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

    // Sanity check, had a bug here earlier.
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

  // This is pretty slow, about four minutes. Could probably go faster by either a) not actually capturing all the
  // paths, like ShortestPathFinder does; or b) updating ShortestPathFinder to use A* instead of its existing
  // breadth-first.
  println(basin.timeToEscape())
  println(basin.timeToRoundTripForSnacks())
}