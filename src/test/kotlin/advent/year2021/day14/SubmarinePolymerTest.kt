package advent.year2021.day14

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SubmarinePolymerTest {

  private val example = """
    NNCB

    CH -> B
    HH -> N
    CB -> H
    NH -> C
    HB -> C
    HC -> B
    HN -> C
    NN -> C
    BH -> H
    NC -> B
    NB -> B
    BN -> B
    BB -> N
    BC -> B
    CC -> N
    CN -> C
  """.trimIndent().let(::SubmarinePolymer)

  @ParameterizedTest
  @CsvSource(
    "1, NCNBCHB",
    "2, NBCCNBBBCBHCB",
    "3, NBBBCNCCNBBNBNBBCHBHHBCHB",
    "4, NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB"
  )
  fun `next -- formulas -- as per reference`(steps: Int, expected: String) {
    val polymer = example.next(steps)

    assertThat(polymer.currentFormula).isEqualTo(expected)
  }

  @Test
  fun `quantity difference -- after 10 steps -- 1588`() {
    val difference = example.next(10).quantityDifference()

    assertThat(difference).isEqualTo(1588)
  }

  @Test
  fun `quantity difference -- after 40 steps -- 2188189693529`() {
    val difference = example.next(40).quantityDifference()

    assertThat(difference).isEqualTo(2188189693529L)
  }
}