package advent.year2022.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FoodCarryingElvesTest {

  @Test
  fun `sample input -- most calories -- 24000`() {
    val input = """
      1000
      2000
      3000

      4000

      5000
      6000

      7000
      8000
      9000

      10000
    """.trimIndent()

    val elves = parseElves(input)

    assertThat(elves.mostCalories()).isEqualTo(24000)
  }

  @Test
  fun `sample input -- calories from top 3 elves -- 45000`() {
    val input = """
      1000
      2000
      3000

      4000

      5000
      6000

      7000
      8000
      9000

      10000
    """.trimIndent()

    val elves = parseElves(input)

    assertThat(elves.caloriesFromTopElves(numElves = 3)).isEqualTo(45000)
  }

}