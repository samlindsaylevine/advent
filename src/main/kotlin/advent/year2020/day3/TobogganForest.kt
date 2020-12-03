package advent.year2020.day3

import advent.utils.Point
import java.io.File

data class TobogganForest(private val width: Int,
                          private val height: Int,
                          private val trees: Set<Point>) {

  companion object {
    fun parse(input: String): TobogganForest {
      val lines = input.trim().lines()

      val width = lines.maxOf { it.length }
      val height = lines.size
      val trees = lines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char == '#') Point(x, y) else null }
      }

      return TobogganForest(width, height, trees.toSet())
    }
  }

  fun treesEncountered(right: Int, down: Int) = generateSequence(0) { it + 1 }
          .map { Point((right * it) % width, down * it) }
          .takeWhile { it.y < height }
          .count { trees.contains(it) }
          .toLong()

  fun slopesProduct() = treesEncountered(1, 1) *
          treesEncountered(3, 1) *
          treesEncountered(5, 1) *
          treesEncountered(7, 1) *
          treesEncountered(1, 2)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day3/input.txt")
          .readText()

  val forest = TobogganForest.parse(input)

  println(forest.treesEncountered(3, 1))
  println(forest.slopesProduct())
}