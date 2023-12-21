package advent.year2023.day21

import advent.utils.Point
import advent.utils.findLinearRecurrence
import java.io.File
import java.lang.Math.floorMod

class GardenMap(val start: Point,
                val width: Int,
                val height: Int,
                val rocks: Set<Point>,
                val infinite: Boolean) {
  companion object {
    fun of(input: String, infinite: Boolean = false): GardenMap {
      val lines = input.trim().lines()
      val pointToChar: Map<Point, Char> = lines.flatMapIndexed { y, line ->
        line.mapIndexed { x, c -> Point(x, y) to c }
      }.toMap()
      val start = pointToChar.entries.first { it.value == 'S' }.key
      val width = lines.maxOf { it.length }
      val height = lines.size
      val rocks = pointToChar.entries.filter { it.value == '#' }.map { it.key }.toSet()
      return GardenMap(start, width, height, rocks, infinite)
    }
  }

  private fun reachable(): Sequence<Set<Point>> = generateSequence(setOf(start)) { points -> points.flatMap { next(it) }.toSet() }

  private fun next(point: Point) = point.adjacentNeighbors
          .filter { Point(floorMod(it.x, width), floorMod(it.y, height)) !in rocks }
          .filter { if (!infinite) (it.x in (0..width) && it.y in (0..height)) else true }

  fun countAfter(numSteps: Int) = reachable().drop(numSteps).first().size.toLong()

  fun countByRecurrence(numSteps: Int): Long {
    val recurrence = reachable().map { it.size }.diffs().diffs().findLinearRecurrence(printEvery = 100)
    println(recurrence)
    return 0L
  }

  private fun Sequence<Int>.diffs() = this.zipWithNext { second, first -> second - first }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day21/input.txt").readText().trim()
  val map = GardenMap.of(input)

  println(map.countAfter(64))
}