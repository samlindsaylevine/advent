package advent.year2023.day18

import advent.utils.Direction
import advent.utils.Point
import advent.utils.PointRange
import advent.utils.times
import advent.year2020.day14.substringBetween
import advent.year2023.day10.floodFill
import java.io.File

class ColoredLagoon(val trenches: List<ColoredTrench>) {
  constructor(input: String) : this(input.let {
    var currentPosition = Point(0, 0)
    it.trim().lines().map { line ->
      val (directionChar, amountStr, colorCode) = line.split(" ")
      val direction = when (directionChar) {
        "U" -> Direction.N
        "L" -> Direction.W
        "R" -> Direction.E
        "D" -> Direction.S
        else -> throw IllegalArgumentException("Unrecognized direction $directionChar")
      }
      val amount = amountStr.toInt()
      val position = currentPosition..currentPosition + amount * direction.toPoint()
      val color = colorCode.substringBetween("#", ")").toInt(radix = 16)
      currentPosition = position.last()
      ColoredTrench(position, color)
    }
  })

  private val trenchPoints: Set<Point> by lazy { trenches.flatMap { it.position }.toSet() }
  private val minX by lazy { trenchPoints.minOf { it.x } }
  private val maxX by lazy { trenchPoints.maxOf { it.x } }
  private val minY by lazy { trenchPoints.minOf { it.y } }
  private val maxY by lazy { trenchPoints.maxOf { it.y } }

  /**
   * This is similar to day 10!  Let's reuse our flood fill from that day.
   */
  fun volume(): Int {
    val outside = floodFill(Point(minX - 1, minY - 1)) { test ->
      (test.x in (minX - 1)..(maxX + 1)) &&
              (test.y in (minY - 1)..(maxY + 1)) &&
              test !in trenchPoints
    }
    return (maxX - minX + 3) * (maxY - minY + 3) - outside.size
  }

  override fun toString(): String = (maxY downTo minY).joinToString(separator = "\n") { y ->
    (minX..maxX).joinToString(separator = "") { x -> if (Point(x, y) in trenchPoints) "#" else "." }
  }

}

class ColoredTrench(val position: PointRange, val color: Int)

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day18/input.txt").readText().trim()
  val lagoon = ColoredLagoon(input)

  println(lagoon.volume())
}