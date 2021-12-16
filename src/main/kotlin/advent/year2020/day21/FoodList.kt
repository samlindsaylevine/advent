package advent.year2020.day21

import java.io.File

/**
 * --- Day 21: Allergen Assessment ---
 * You reach the train's last stop and the closest you can get to your vacation island without getting wet. There
 * aren't even any boats here, but nothing can stop you now: you build a raft. You just need a few days' worth of food
 * for your journey.
 * You don't speak the local language, so you can't read any ingredients lists. However, sometimes, allergens are
 * listed in a language you do understand. You should be able to use this information to determine which ingredient
 * contains which allergen and work out which foods are safe to take with you on your trip.
 * You start by compiling a list of foods (your puzzle input), one food per line. Each line includes that food's
 * ingredients list followed by some or all of the allergens the food contains.
 * Each allergen is found in exactly one ingredient. Each ingredient contains zero or one allergen. Allergens aren't
 * always marked; when they're listed (as in (contains nuts, shellfish) after an ingredients list), the ingredient that
 * contains each listed allergen will be somewhere in the corresponding ingredients list. However, even if an allergen
 * isn't listed, the ingredient that contains that allergen could still be present: maybe they forgot to label it, or
 * maybe it was labeled in a language you don't know.
 * For example, consider the following list of foods:
 * mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
 * trh fvjkl sbzzf mxmxvkd (contains dairy)
 * sqjhc fvjkl (contains soy)
 * sqjhc mxmxvkd sbzzf (contains fish)
 * 
 * The first food in the list has four ingredients (written in a language you don't understand): mxmxvkd, kfcds, sqjhc,
 * and nhms. While the food might contain other allergens, a few allergens the food definitely contains are listed
 * afterward: dairy and fish.
 * The first step is to determine which ingredients can't possibly contain any of the allergens in any food in your
 * list. In the above example, none of the ingredients kfcds, nhms, sbzzf, or trh can contain an allergen. Counting the
 * number of times any of these ingredients appear in any ingredients list produces 5: they all appear once each except
 * sbzzf, which appears twice.
 * Determine which ingredients cannot possibly contain any of the allergens in your list. How many times do any of
 * those ingredients appear?
 * 
 * --- Part Two ---
 * Now that you've isolated the inert ingredients, you should have enough information to figure out which ingredient
 * contains which allergen.
 * In the above example:
 * 
 * mxmxvkd contains dairy.
 * sqjhc contains fish.
 * fvjkl contains soy.
 * 
 * Arrange the ingredients alphabetically by their allergen and separate them by commas to produce your canonical
 * dangerous ingredient list. (There should not be any spaces in your canonical dangerous ingredient list.) In the
 * above example, this would be mxmxvkd,sqjhc,fvjkl.
 * Time to stock your raft with supplies. What is your canonical dangerous ingredient list?
 * 
 */
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