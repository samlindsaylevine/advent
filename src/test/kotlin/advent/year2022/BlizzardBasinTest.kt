package advent.year2022

import advent.year2022.day24.BlizzardBasin
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
}