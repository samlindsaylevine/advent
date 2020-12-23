package advent.year2020.day21

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FoodListTest {

  private val input = """
    mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
    trh fvjkl sbzzf mxmxvkd (contains dairy)
    sqjhc fvjkl (contains soy)
    sqjhc mxmxvkd sbzzf (contains fish)
  """.trimIndent()

  @Test
  fun `appearance count -- reference input, non-allergenic ingredients -- appear 5 times`() {
    val foods = FoodList(input)

    val nonAllergenic = listOf("kfcds", "nhms", "sbzzf", "trh")
    val count = foods.appearanceCount(nonAllergenic)

    assertThat(count).isEqualTo(5)
  }

  @Test
  fun `non allergenic -- reference input -- kfcds, nhms, sbzzf, trh`() {
    val foods = FoodList(input)

    val nonAllergenic = foods.guaranteedNonAllergenicIngredients()

    assertThat(nonAllergenic).containsExactlyInAnyOrder("kfcds", "nhms", "sbzzf", "trh")
  }

  @Test
  fun `dangerous ingredients -- reference input -- mxmxvkd = dairy, sqjhc = fish, fvjkl = soy`() {
    val foods = FoodList(input)

    val dangerous = foods.dangerousIngredients()

    assertThat(dangerous).isEqualTo(mapOf(
            "mxmxvkd" to "dairy",
            "sqjhc" to "fish",
            "fvjkl" to "soy"
    ))
  }

  @Test
  fun `canonical dangerous ingredient list -- reference input -- mxmxvkd,sqjhc,fvjkl`() {
    val foods = FoodList(input)

    val canonicalList = foods.canonicalDangerousIngredientList()

    assertThat(canonicalList).isEqualTo("mxmxvkd,sqjhc,fvjkl")
  }
}