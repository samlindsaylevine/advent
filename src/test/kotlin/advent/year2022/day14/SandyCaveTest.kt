package advent.year2022.day14

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SandyCaveTest {

  private val input = """
    498,4 -> 498,6 -> 496,6
    503,4 -> 502,4 -> 502,9 -> 494,9
  """.trimIndent()

  @Test
  fun `amount of sand -- reference input -- 24`() {
    val cave = SandyCave(input)

    val amount = cave.filled().sand.size

    assertThat(amount).isEqualTo(24)
  }

  @Test
  fun `amount of sand -- reference input, with floor -- 93`() {
    val cave = SandyCave(input, hasFloor = true)

    val amount = cave.filled().sand.size

    assertThat(amount).isEqualTo(93)
  }
}