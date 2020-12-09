package advent.year2020.day9

import advent.utils.allUnorderedPairs
import java.io.File
import java.util.*

class ExchangeMaskingAdditionSystem(private val preambleLength: Int = 25) {

  fun firstInvalid(input: List<Long>): Long {
    val previousNumbers = LinkedList(input.take(preambleLength).toList())

    return input.drop(preambleLength).first {
      val matches = !previousNumbers.pairwiseSums().contains(it)

      previousNumbers.removeFirst()
      previousNumbers.addLast(it)

      matches
    }
  }

  fun encryptionWeakness(input: List<Long>): Long {
    val target = this.firstInvalid(input)

    val range = input.contiguousRangeSummingTo(target)

    return (range.minOrNull() ?: 0) + (range.maxOrNull() ?: 0)
  }

  private fun List<Long>.pairwiseSums() = this.allUnorderedPairs().map { (a, b) -> a + b }.toSet()

  private fun List<Long>.contiguousRangeSummingTo(sum: Long) = indices.mapNotNull { rangeSummingTo(sum, it) }
          .first()

  /**
   * Finds the list, starting at the provided index, that sums to the target value, or null if there is
   * none.
   */
  private fun List<Long>.rangeSummingTo(sum: Long, index: Int): List<Long>? {
    val output = mutableListOf<Long>()
    var currentSum = 0L

    for (i in index until this.size) {
      output.add(this[i])
      currentSum += this[i]

      if (currentSum == sum) return output
      if (currentSum > sum) return null
    }

    return null
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day9/input.txt")
          .readText()
          .trim()
          .lines()
          .map(String::toLong)

  println(ExchangeMaskingAdditionSystem().firstInvalid(input))
  println(ExchangeMaskingAdditionSystem().encryptionWeakness(input))
}