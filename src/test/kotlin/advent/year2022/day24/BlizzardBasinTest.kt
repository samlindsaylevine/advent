package advent.year2022.day24

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BlizzardBasinTest {

  private val input = """
    #.######
    #>>.<^<#
    #.<..<<#
    #>v.><>#
    #<^v^^>#
    ######.#
  """.trimIndent()

  @Test
  fun `time to escape -- example -- 18`() {
    val basin = BlizzardBasin(input)

    assertThat(basin.timeToEscape()).isEqualTo(18)
  }

  @Test
  fun `time to round trip for snacks -- example -- 54`() {
    val basin = BlizzardBasin(input)

    assertThat(basin.timeToRoundTripForSnacks()).isEqualTo(54)
  }
}