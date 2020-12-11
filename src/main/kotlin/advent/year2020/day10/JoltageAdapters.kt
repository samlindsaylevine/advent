package advent.year2020.day10

import advent.utils.maxOrThrow
import java.io.File

class JoltageAdapters(ratings: List<Int>) {
  constructor(input: String) : this(input.trim()
          .lines()
          .map(String::toInt)
          .plus(0)
          .let { it + (it.maxOrThrow() + 3) })

  val sortedRatings = ratings.sorted()

  fun differencesOfSize(jolts: Int) = sortedRatings.zipWithNext()
          .count { it.second - it.first == jolts }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day10/input.txt")
          .readText()

  val adapters = JoltageAdapters(input)

  println(adapters.differencesOfSize(1) * adapters.differencesOfSize(3))
}