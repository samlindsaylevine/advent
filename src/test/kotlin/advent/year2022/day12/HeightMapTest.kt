package advent.year2022.day12

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HeightMapTest {

  @Test
  fun `fewest steps -- reference input -- 31`() {
    val input = """
      Sabqponm
      abcryxxl
      accszExk
      acctuvwj
      abdefghi
    """.trimIndent()
    val heightMap = HeightMap(input)

    assertThat(heightMap.fewestSteps()).isEqualTo(31)
  }

  @Test
  fun `fewest steps from any a -- reference input -- 29`() {
    val input = """
      Sabqponm
      abcryxxl
      accszExk
      acctuvwj
      abdefghi
    """.trimIndent()
    val heightMap = HeightMap(input)

    assertThat(heightMap.fewestStepsFromAnyA()).isEqualTo(29)
  }
}