package advent.year2020.day21

import java.io.File

class FoodList(input: String) {
  val foods = input.trim()
          .lines()
          .map(::Food)

  private val allIngredients = foods.flatMap { it.ingredients }.toSet()
  private val allAllergens = foods.flatMap { it.allergens }.toSet()

  fun guaranteedNonAllergenicIngredients(): Set<String> {
    return allIngredients.filter { ingredient ->
      allAllergens.none { allergen -> isPossible(ingredient, allergen) }
    }.toSet()
  }

  private fun isPossible(ingredient: String, allergen: String) = foods.all { food ->
    if (food.allergens.contains(allergen)) food.ingredients.contains(ingredient) else true
  }

  fun appearanceCount(ingredients: Collection<String>) = ingredients.sumOf { ingredient ->
    foods.count { it.ingredients.contains(ingredient) }
  }
}

class Food(input: String) {
  companion object {
    val regex = "(.*) \\(contains (.*)\\)".toRegex()
  }

  private val match = regex.matchEntire(input) ?: throw IllegalArgumentException("Can't parse food $input")

  val ingredients = match.groupValues[1].split(" ").toSet()
  val allergens = match.groupValues[2].split(", ").toSet()
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day21/input.txt")
          .readText()
          .trim()

  val foods = FoodList(input)
  val nonAllergenic = foods.guaranteedNonAllergenicIngredients()

  println(foods.appearanceCount(nonAllergenic))
}