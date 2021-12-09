package advent.year2021

import advent.year2021.day9.SmokeCaves
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SmokeCavesTest {

  private val input = """
    2199943210
    3987894921
    9856789892
    8767896789
    9899965678
  """.trimIndent()

  @Test
  fun `riskLevelSum -- reference input -- 15`() {
    val caves = SmokeCaves(input)

    assertThat(caves.riskLevelSum()).isEqualTo(15)
  }

  @Test
  fun `basinProduct -- reference input -- 1134`() {
    val caves = SmokeCaves(input)

    assertThat(caves.basinProduct()).isEqualTo(1134)
  }
}