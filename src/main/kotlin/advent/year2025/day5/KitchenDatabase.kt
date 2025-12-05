package advent.year2025.day5

import advent.meta.readInput

class KitchenDatabase(val ranges: List<LongRange>, val ingredients: List<Long>) {
  private constructor(input: Pair<List<LongRange>, List<Long>>) : this(input.first, input.second)
  constructor(input: String) : this(input.trim().let {
    val (rangeSection, ingredientSection) = it.split("\n\n")
    val ranges = rangeSection.lines().map { line ->
      val (start, end) = line.split("-")
      start.toLong()..end.toLong()
    }
    val ingredients = ingredientSection.lines().map(String::toLong)
    ranges to ingredients
  })

  fun countFresh() = ingredients.count { ingredient -> ranges.any { ingredient in it } }
}

fun main() {
  val database = KitchenDatabase(readInput())

  println(database.countFresh())
}