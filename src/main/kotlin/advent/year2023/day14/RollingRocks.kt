package advent.year2023.day14

import advent.utils.Direction
import advent.utils.Point
import advent.utils.PointRange
import advent.year2018.day18.advance
import java.io.File

/**
 * --- Day 14: Parabolic Reflector Dish ---
 * You reach the place where all of the mirrors were pointing: a massive parabolic reflector dish attached to the side
 * of another large mountain.
 * The dish is made up of many small mirrors, but while the mirrors themselves are roughly in the shape of a parabolic
 * reflector dish, each individual mirror seems to be pointing in slightly the wrong direction. If the dish is meant to
 * focus light, all it's doing right now is sending it in a vague direction.
 * This system must be what provides the energy for the lava! If you focus the reflector dish, maybe you can go where
 * it's pointing and use the light to fix the lava production.
 * Upon closer inspection, the individual mirrors each appear to be connected via an elaborate system of ropes and
 * pulleys to a large metal platform below the dish. The platform is covered in large rocks of various shapes.
 * Depending on their position, the weight of the rocks deforms the platform, and the shape of the platform controls
 * which ropes move and ultimately the focus of the dish.
 * In short: if you move the rocks, you can focus the dish. The platform even has a control panel on the side that lets
 * you tilt it in one of four directions! The rounded rocks (O) will roll when the platform is tilted, while the
 * cube-shaped rocks (#) will stay in place. You note the positions of all of the empty spaces (.) and rocks (your
 * puzzle input). For example:
 * O....#....
 * O.OO#....#
 * .....##...
 * OO.#O....O
 * .O.....O#.
 * O.#..O.#.#
 * ..O..#O..O
 * .......O..
 * #....###..
 * #OO..#....
 *
 * Start by tilting the lever so all of the rocks will slide north as far as they will go:
 * OOOO.#.O..
 * OO..#....#
 * OO..O##..O
 * O..#.OO...
 * ........#.
 * ..#....#.#
 * ..O..#.O.O
 * ..O.......
 * #....###..
 * #....#....
 *
 * You notice that the support beams along the north side of the platform are damaged; to ensure the platform doesn't
 * collapse, you should calculate the total load on the north support beams.
 * The amount of load caused by a single rounded rock (O) is equal to the number of rows from the rock to the south
 * edge of the platform, including the row the rock is on. (Cube-shaped rocks (#) don't contribute to load.) So, the
 * amount of load caused by each rock in each row is as follows:
 * OOOO.#.O.. 10
 * OO..#....#  9
 * OO..O##..O  8
 * O..#.OO...  7
 * ........#.  6
 * ..#....#.#  5
 * ..O..#.O.O  4
 * ..O.......  3
 * #....###..  2
 * #....#....  1
 *
 * The total load is the sum of the load caused by all of the rounded rocks. In this example, the total load is 136.
 * Tilt the platform so that the rounded rocks all roll north. Afterward, what is the total load on the north support
 * beams?
 *
 * --- Part Two ---
 * The parabolic reflector dish deforms, but not in a way that focuses the beam. To do that, you'll need to move the
 * rocks to the edges of the platform. Fortunately, a button on the side of the control panel labeled "spin cycle"
 * attempts to do just that!
 * Each cycle tilts the platform four times so that the rounded rocks roll north, then west, then south, then east.
 * After each tilt, the rounded rocks roll as far as they can before the platform tilts in the next direction. After
 * one cycle, the platform will have finished rolling the rounded rocks in those four directions in that order.
 * Here's what happens in the example above after each of the first few cycles:
 * After 1 cycle:
 * .....#....
 * ....#...O#
 * ...OO##...
 * .OO#......
 * .....OOO#.
 * .O#...O#.#
 * ....O#....
 * ......OOOO
 * #...O###..
 * #..OO#....
 *
 * After 2 cycles:
 * .....#....
 * ....#...O#
 * .....##...
 * ..O#......
 * .....OOO#.
 * .O#...O#.#
 * ....O#...O
 * .......OOO
 * #..OO###..
 * #.OOO#...O
 *
 * After 3 cycles:
 * .....#....
 * ....#...O#
 * .....##...
 * ..O#......
 * .....OOO#.
 * .O#...O#.#
 * ....O#...O
 * .......OOO
 * #...O###.O
 * #.OOO#...O
 *
 * This process should work if you leave it running long enough, but you're still worried about the north support
 * beams. To make sure they'll survive for a while, you need to calculate the total load on the north support beams
 * after 1000000000 cycles.
 * In the above example, after 1000000000 cycles, the total load on the north support beams is 64.
 * Run the spin cycle for 1000000000 cycles. Afterward, what is the total load on the north support beams?
 *
 */
data class RollingRocks(val roundedRocks: Set<Point>,
                        val cubeShapedRocks: Set<Point>,
                        val width: Int,
                        val height: Int) {
  companion object {
    fun of(input: String): RollingRocks {
      val lines = input.trim().lines()
      val width = lines.maxOf { it.length }
      val height = lines.size
      val pointToRockType: List<Pair<Point, Char>> = lines.flatMapIndexed { y, line ->
        line.flatMapIndexed { x, c -> if (c == '.') emptyList() else listOf(Point(x, y) to c) }
      }
      val (roundedRocks, cubeShapedRocks) = pointToRockType.partition { (_, type) -> type == 'O' }
      return RollingRocks(roundedRocks.map { it.first }.toSet(),
              cubeShapedRocks.map { it.first }.toSet(),
              width,
              height)
    }
  }

  fun load() = roundedRocks.sumOf { height - it.y }

  // We'll define the definition of "alleys", both horizontal and vertical. An alley is a maximally long stretch of
  // points that is bounded on each side either by the edge of the map, or by a cube-shaped rock. When we tilt the
  // surface, all the round rocks in each alley will be moved to one side of the alley.
  private fun PointRange.splitToAlleys(): List<PointRange> {
    val output = mutableListOf<PointRange>()
    var currentAlleyStart: Point? = null
    var previousPoint: Point = this.first()
    for (point in this) {
      when {
        currentAlleyStart == null && point !in cubeShapedRocks -> currentAlleyStart = point
        currentAlleyStart != null && point in cubeShapedRocks -> {
          output.add(currentAlleyStart..previousPoint)
          currentAlleyStart = null
        }
      }
      previousPoint = point
    }
    // Finish up on the right hand side.
    if (currentAlleyStart != null) output.add(currentAlleyStart..previousPoint)
    return output
  }

  private val horizontalAlleys by lazy {
    (0 until height).flatMap { y -> (Point(0, y)..Point(width - 1, y)).splitToAlleys() }
  }

  private val verticalAlleys by lazy {
    (0 until width).flatMap { x -> (Point(x, 0)..Point(x, height - 1)).splitToAlleys() }
  }

  /**
   * Returns the result if this point range, which must be an alley, is tilted in one direction or the other - i.e.,
   * the new positions of the rounded rocks in this alley.
   *
   * @param positiveDirection If true, the alley is being tilted towards the + direction in the coordinates on this
   * axis.
   */
  private fun PointRange.tilted(positiveDirection: Boolean): List<Point> {
    val rockCount = this.count { it in roundedRocks }
    val range = if (positiveDirection) this.reversed() else this
    return range.take(rockCount)
  }

  fun tilted(direction: Direction): RollingRocks {
    val alleys = when (direction) {
      Direction.N, Direction.S -> verticalAlleys
      Direction.W, Direction.E -> horizontalAlleys
    }
    val positiveDirection = when (direction) {
      Direction.S, Direction.E -> true
      Direction.N, Direction.W -> false
    }
    val newRocks = alleys.flatMap { it.tilted(positiveDirection) }.toSet()
    return RollingRocks(newRocks, cubeShapedRocks, width, height)
  }

  private fun cycled() = this.tilted(Direction.N)
          .tilted(Direction.W)
          .tilted(Direction.S)
          .tilted(Direction.E)

  // Uses existing loop-detection functionality to see when we hit one that we have hit before.
  fun cycled(count: Int): RollingRocks = advance(count, this, RollingRocks::cycled)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day14/input.txt").readText().trim()
  val rocks = RollingRocks.of(input)

  println(rocks.tilted(Direction.N).load())
  println(rocks.cycled(1000000000).load())
}