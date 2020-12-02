package advent.year2020.day1

import java.io.File

class ExpenseReport(entriesList: List<Int>) {

  private val entries = entriesList.asSequence()

  fun product() = entries.flatMap { first -> entries.map { first to it } }
          .first { it.first + it.second == 2020 }
          .let { it.first * it.second }

  fun tripleProduct() = entries.flatMap { first ->
    entries.flatMap { second -> entries.map { third -> Triple(first, second, third) } }
  }.first { it.first + it.second + it.third == 2020 }
          .let { it.first * it.second * it.third }
}

fun main() {
  val input = File("src/main/kotlin/advent/advent.year2020/day1/input.txt")
          .readText()
          .trim()
          .lines()
          .map { it.toInt() }

  val report = ExpenseReport(input)

  println(report.product())
  println(report.tripleProduct())
}