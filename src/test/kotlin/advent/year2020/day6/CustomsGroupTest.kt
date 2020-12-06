package advent.year2020.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CustomsGroupTest {

  @Test
  fun `distinct yes counts -- reference input -- reference value`() {
    val input = """
      abc

      a
      b
      c

      ab
      ac

      a
      a
      a
      a

      b
    """.trimIndent()

    val groups = CustomsGroups(input)

    val count = groups.groups.sumOf { it.distinctYesAnswers.count() }

    assertThat(count).isEqualTo(11)
  }

  @Test
  fun `overlapping yes counts -- reference input -- reference value`() {
    val input = """
      abc

      a
      b
      c

      ab
      ac

      a
      a
      a
      a

      b
    """.trimIndent()

    val groups = CustomsGroups(input)

    val count = groups.groups.sumOf { it.overlappingYesAnswers.count() }

    assertThat(count).isEqualTo(6)
  }
}