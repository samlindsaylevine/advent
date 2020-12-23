package advent.year2020.day21

import java.io.File

class FoodList(val foods: List<Food>) {
  constructor(input: String) : this(input.trim()
          .lines()
          .map(Food::parse))

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

  /**
   * Map from ingredient to allergen.
   */
  fun dangerousIngredients(): Map<String, String> = solve(PartialSolution(this, emptyMap()))
          ?: throw IllegalArgumentException("Unsolveable food list")

  /**
   * Returns the solved map of ingredient to allergen, or null if this is an unsolveable situation.
   */
  private fun solve(partialSolution: PartialSolution): Map<String, String>? {
    val simplified = simplify(partialSolution)

    if (simplified.foodList.allAllergens.isEmpty()) return simplified.solvedIngredients
    if (simplified.foodList.foods.any { it.allergens.size > it.ingredients.size }) return null

    // The simplified solution is neither solved, nor yet unsolvable. We have to take a guess.
    val allergen = simplified.foodList.allAllergens.first()

    val ingredientPossbilities = simplified.foodList.allIngredients
            .asSequence()
            .filter { simplified.foodList.isPossible(it, allergen) }

    return ingredientPossbilities
            .mapNotNull { solve(simplified.with(it, allergen)) }
            .firstOrNull()
  }

  /**
   * Makes deterministic simplifications - eliminates all ingredients that are guaranteed safe; selects all determined
   * ingredients (i.e., foods with only 1 ingredient & 1 allergen).
   */
  private tailrec fun simplify(partialSolution: PartialSolution): PartialSolution {
    if (partialSolution.foodList.allAllergens.isEmpty()) return partialSolution

    val guaranteedSafe = partialSolution.foodList.guaranteedNonAllergenicIngredients()

    return if (guaranteedSafe.isEmpty()) {
      val solvedFood = partialSolution.foodList.foods.firstOrNull { it.ingredients.size == 1 && it.allergens.size == 1 }
              ?: return partialSolution

      val ingredient = solvedFood.ingredients.first()
      val allergen = solvedFood.allergens.first()

      // Not much of a guess since we know this one is determined.
      val next = partialSolution.with(ingredient, allergen)

      simplify(next)
    } else {
      val next = PartialSolution(partialSolution.foodList.withoutIngredients(guaranteedSafe),
              partialSolution.solvedIngredients)
      simplify(next)
    }
  }

  private data class PartialSolution(val foodList: FoodList, val solvedIngredients: Map<String, String>) {
    fun with(ingredient: String, allergen: String) =
            PartialSolution(this.foodList.withoutIngredients(setOf(ingredient)).withoutAllergen(allergen),
                    this.solvedIngredients + (ingredient to allergen))
  }

  fun canonicalDangerousIngredientList() = dangerousIngredients().entries
          .sortedBy { it.value }
          .joinToString(separator = ",") { it.key }

  private fun withoutIngredients(ingredients: Set<String>) = FoodList(foods.map { it.withoutIngredients(ingredients) })

  private fun withoutAllergen(allergen: String) = FoodList(foods.map { it.withoutAllergen(allergen) }
          .filter { it.allergens.isNotEmpty() })
}

class Food(val ingredients: Set<String>,
           val allergens: Set<String>) {
  companion object {
    private val regex = "(.*) \\(contains (.*)\\)".toRegex()

    fun parse(input: String): Food {
      val match = regex.matchEntire(input) ?: throw IllegalArgumentException("Can't parse food $input")
      return Food(match.groupValues[1].split(" ").toSet(),
              match.groupValues[2].split(", ").toSet())
    }
  }

  fun withoutIngredients(omitIngredients: Set<String>) = Food(ingredients - omitIngredients, allergens)
  fun withoutAllergen(omitAllergen: String) = Food(ingredients, allergens - omitAllergen)

  override fun toString() = "${ingredients.joinToString(" ")} (contains ${allergens.joinToString(" ,")})"
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day21/input.txt")
          .readText()
          .trim()

  val foods = FoodList(input)
  val nonAllergenic = foods.guaranteedNonAllergenicIngredients()

  println(foods.appearanceCount(nonAllergenic))
  println(foods.canonicalDangerousIngredientList())
}