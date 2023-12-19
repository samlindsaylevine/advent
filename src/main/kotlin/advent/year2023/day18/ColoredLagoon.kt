package advent.year2023.day18

import advent.utils.Direction
import advent.utils.Point
import advent.utils.times
import advent.year2020.day14.substringBetween
import java.io.File
import kotlin.math.abs


/**
 * --- Day 18: Lavaduct Lagoon ---
 * Thanks to your efforts, the machine parts factory is one of the first factories up and running since the lavafall
 * came back. However, to catch up with the large backlog of parts requests, the factory will also need a large supply
 * of lava for a while; the Elves have already started creating a large lagoon nearby for this purpose.
 * However, they aren't sure the lagoon will be big enough; they've asked you to take a look at the dig plan (your
 * puzzle input). For example:
 * R 6 (#70c710)
 * D 5 (#0dc571)
 * L 2 (#5713f0)
 * D 2 (#d2c081)
 * R 2 (#59c680)
 * D 2 (#411b91)
 * L 5 (#8ceee2)
 * U 2 (#caa173)
 * L 1 (#1b58a2)
 * U 2 (#caa171)
 * R 2 (#7807d2)
 * U 3 (#a77fa3)
 * L 2 (#015232)
 * U 2 (#7a21e3)
 *
 * The digger starts in a 1 meter cube hole in the ground. They then dig the specified number of meters up (U), down
 * (D), left (L), or right (R), clearing full 1 meter cubes as they go. The directions are given as seen from above, so
 * if "up" were north, then "right" would be east, and so on. Each trench is also listed with the color that the edge
 * of the trench should be painted as an RGB hexadecimal color code.
 * When viewed from above, the above example dig plan would result in the following loop of trench (#) having been dug
 * out from otherwise ground-level terrain (.):
 * #######
 * #.....#
 * ###...#
 * ..#...#
 * ..#...#
 * ###.###
 * #...#..
 * ##..###
 * .#....#
 * .######
 *
 * At this point, the trench could contain 38 cubic meters of lava. However, this is just the edge of the lagoon; the
 * next step is to dig out the interior so that it is one meter deep as well:
 * #######
 * #######
 * #######
 * ..#####
 * ..#####
 * #######
 * #####..
 * #######
 * .######
 * .######
 *
 * Now, the lagoon can contain a much more respectable 62 cubic meters of lava. While the interior is dug out, the
 * edges are also painted according to the color codes in the dig plan.
 * The Elves are concerned the lagoon won't be large enough; if they follow their dig plan, how many cubic meters of
 * lava could it hold?
 *
 * --- Part Two ---
 * The Elves were right to be concerned; the planned lagoon would be much too small.
 * After a few minutes, someone realizes what happened; someone swapped the color and instruction parameters when
 * producing the dig plan. They don't have time to fix the bug; one of them asks if you can extract the correct
 * instructions from the hexadecimal codes.
 * Each hexadecimal code is six hexadecimal digits long. The first five hexadecimal digits encode the distance in
 * meters as a five-digit hexadecimal number. The last hexadecimal digit encodes the direction to dig: 0 means R, 1
 * means D, 2 means L, and 3 means U.
 * So, in the above example, the hexadecimal codes can be converted into the true instructions:
 *
 * #70c710 = R 461937
 * #0dc571 = D 56407
 * #5713f0 = R 356671
 * #d2c081 = D 863240
 * #59c680 = R 367720
 * #411b91 = D 266681
 * #8ceee2 = L 577262
 * #caa173 = U 829975
 * #1b58a2 = L 112010
 * #caa171 = D 829975
 * #7807d2 = L 491645
 * #a77fa3 = U 686074
 * #015232 = L 5411
 * #7a21e3 = U 500254
 *
 * Digging out this loop and its interior produces a lagoon that can hold an impressive 952408144115 cubic meters of
 * lava.
 * Convert the hexadecimal color codes into the correct instructions; if the Elves follow this new dig plan, how many
 * cubic meters of lava could the lagoon hold?
 *
 */
class ColoredLagoon(
        /**
         * Note that the first vertex is both the first and last item in this list.
         */
        private val vertices: List<Point>) {
  companion object {
    fun of(input: String, parseLine: (String) -> Pair<Direction, Int>): ColoredLagoon {
      val vertices = mutableListOf(Point(0, 0))
      input.trim().lines().forEach { line ->
        val (direction, amount) = parseLine(line)
        vertices.add(vertices.last() + amount * direction.toPoint())
      }
      return ColoredLagoon(vertices)
    }

    fun partOne(line: String): Pair<Direction, Int> {
      val (directionChar, amountStr) = line.split(" ")
      val direction = when (directionChar) {
        "U" -> Direction.N
        "L" -> Direction.W
        "R" -> Direction.E
        "D" -> Direction.S
        else -> throw IllegalArgumentException("Unrecognized direction $directionChar")
      }
      val amount = amountStr.toInt()
      return direction to amount
    }

    fun partTwo(line: String): Pair<Direction, Int> {
      val hex = line.substringBetween("#", ")")
      val direction = when (val lastChar = hex.last()) {
        '0' -> Direction.E
        '1' -> Direction.S
        '2' -> Direction.W
        '3' -> Direction.N
        else -> throw IllegalArgumentException("Unrecognized direction $lastChar")
      }
      val amount = hex.take(5).toInt(radix = 16)
      return direction to amount
    }
  }


  /**
   * I started solving this by using the same flood fill we used in Day 10. That worked great for part 1, but it
   * was obviously going to be a disaster for part 2!
   *
   * Instead I googled "area of polygon from coordinates of vertices". That turned up the "shoelace formula" which
   * we implement here (without proof or explanation of why it works!).
   */
  fun volume(): Long {
    val sum = vertices
            .zipWithNext { first, second -> first.x.toLong() * second.y.toLong() - second.x.toLong() * first.y.toLong() }
            .sum()
    val shoelaceResult: Long = abs(sum) / 2

    // There is a complication - our coordinates were all to the middle of each square. That means that at each point
    // in the trench not in a vertex, we are missing a volume of 1/2; at each point that is a vertex, we are missing
    // a volume of either 1/4 or 3/4.
    // Let's add those back in. (I tried for a bit directly adjusting the vertex coordinates before applying the
    // shoelace theorem, but that was a pain compared to doing this later calculation.)
    val numEdgePoints = vertices.zipWithNext { first, second -> first.distanceFrom(second).toLong() - 1 }
            .sum()
    // How do we know, at a particular vertex, whether we are missing 1/4 or 3/4?
    // In the reference example, we can see that each vertex, if it is a right turn, we are missing 3/4, and if it is
    // a left turn, we are missing 1/4.
    // Also, we see that we have 9 right turns and 5 left turns. We know that one of the types of turn will always have
    // 4 more than the other. The type that exists more is each missing 3/4.
    val turns = turns()
    val lefts = turns.count { it == Turning.LEFT }
    val rights = turns.size - lefts
    val turnAdjustment = if (lefts > rights) (3 * lefts + rights) else (3 * rights + lefts)
    val adjustment: Long = (2 * numEdgePoints + turnAdjustment) / 4

    return shoelaceResult + adjustment
  }


  private data class DoublePoint(val x: Double, val y: Double)
  private enum class Turning { LEFT, RIGHT }

  private fun turns(): List<Turning> {
    val triples = vertices.windowed(3) + listOf(listOf(vertices[vertices.size - 2], vertices[0], vertices[1]))
    return triples.map { (previous, current, next) ->
      // Cross product determines which direction the turn is.
      val first = (current - previous)
      val second = (next - current)
      // e.g. first is positive y and 0 x, second is positive x and 0 y -> right turn
      // This is suggestive of the shoelace formula again, isn't it?
      if ((second.x.toLong() * first.y.toLong() - second.y.toLong() * first.x.toLong()) > 0) Turning.RIGHT else Turning.LEFT
    }
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day18/input.txt").readText().trim()

  val lagoonOne = ColoredLagoon.of(input, ColoredLagoon::partOne)
  println(lagoonOne.volume())

  val lagoonTwo = ColoredLagoon.of(input, ColoredLagoon::partTwo)
  println(lagoonTwo.volume())
}