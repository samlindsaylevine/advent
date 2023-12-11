package advent.year2023.day11

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ObservedCosmosTest {

  @Test
  fun `sumOfLengths -- reference input, expanded -- 374`() {
    val input = """
      ...#......
      .......#..
      #.........
      ..........
      ......#...
      .#........
      .........#
      ..........
      .......#..
      #...#.....
    """.trimIndent()

    val cosmos = ObservedCosmos(input)
    val result = cosmos.expanded().sumOfLengths()

    assertThat(result).isEqualTo(374)
  }

  @ParameterizedTest(name = "sumOfLengths -- reference input, expanded by {0} - {1}")
  @CsvSource("10, 1030",
          "100, 8410")
  fun `sumOfLengths -- reference input, expanded by amount -- has expected total sum`(amount: Int, expectedResult: Long) {
    val input = """
      ...#......
      .......#..
      #.........
      ..........
      ......#...
      .#........
      .........#
      ..........
      .......#..
      #...#.....
    """.trimIndent()

    val cosmos = ObservedCosmos(input)
    val result = cosmos.expanded(amount).sumOfLengths()

    assertThat(result).isEqualTo(expectedResult)
  }
}