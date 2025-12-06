package advent.year2025.day5

import advent.meta.readInput

class KitchenDatabase(
  val ranges: List<LongRange>,
  val ingredients: List<Long>
) {
  companion object {
    fun of(input: String): KitchenDatabase {
      val (rangeSection, ingredientSection) = input.trim().split("\n\n")
      val ranges = rangeSection.lines().map { line ->
        val (start, end) = line.split("-")
        start.toLong()..end.toLong()
      }
      val ingredients = ingredientSection.lines().map(String::toLong)
      return KitchenDatabase(ranges, ingredients)
    }
  }

  fun countFresh() = ingredients.count { ingredient -> ranges.any { ingredient in it } }
}

fun main() {
  val database = KitchenDatabase.of(readInput())

  println(database.countFresh())
}