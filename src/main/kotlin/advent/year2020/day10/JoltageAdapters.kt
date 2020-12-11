package advent.year2020.day10

import advent.utils.loadingCache
import advent.utils.maxOrThrow
import java.io.File

class JoltageAdapters(ratings: List<Int>) {
  constructor(input: String) : this(input.trim()
          .lines()
          .map(String::toInt)
          .plus(0)
          .let { it + (it.maxOrThrow() + 3) })

  private val sortedRatings = ratings.sorted()
  private val setOfRatings = ratings.toSet()
  private val max = sortedRatings.maxOrThrow()

  fun differencesOfSize(jolts: Int) = sortedRatings.zipWithNext()
          .count { it.second - it.first == jolts }

  // Cache the number of arrangements starting at a particular joltage for re-use.
  private val arrangementsCache = loadingCache(::arrangementsFrom)

  fun arrangements(): Long = arrangementsCache[0]

  // Calculate the number of arrangements from the start joltage until the end.
  private fun arrangementsFrom(joltage: Int): Long = when {
    joltage > max -> 0
    joltage == max -> 1
    else -> arrangementCount(joltage + 1) + arrangementCount(joltage + 2) + arrangementCount(joltage + 3)
  }

  // If n is a joltage that we have an adapter for, the number of arrangements from there to the end; if it isn't, 0.
  private fun arrangementCount(n: Int) = if (setOfRatings.contains(n)) {
    arrangementsCache[n]
  } else {
    0
  }

}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day10/input.txt")
          .readText()

  val adapters = JoltageAdapters(input)

  println(adapters.differencesOfSize(1) * adapters.differencesOfSize(3))
  println(adapters.arrangements())
}