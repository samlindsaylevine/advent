package advent.year2021.day5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HydrothermalVentTest {

  private val input = """
    0,9 -> 5,9
    8,0 -> 0,8
    9,4 -> 3,4
    2,2 -> 2,1
    7,0 -> 7,4
    6,4 -> 2,0
    0,9 -> 2,9
    3,4 -> 1,4
    0,0 -> 8,8
    5,5 -> 8,2
  """.trimIndent()

  @Test
  fun `overlapping points -- reference input, orthogonal only -- 5`() {
    val vents = HydrothermalVents(input).orthogonalOnly()

    assertThat(vents.overlappingPoints()).isEqualTo(5)
  }

  @Test
  fun `overlapping points -- reference input, diagonals OK-- 12`() {
    val vents = HydrothermalVents(input)

    assertThat(vents.overlappingPoints()).isEqualTo(12)
  }
}