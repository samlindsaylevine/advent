package advent.year2022.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LavaDropletsTest {

  @Test
  fun `surface area -- two adjoining cubes -- 10`() {
    val input = """
      1,1,1
      2,1,1
    """.trimIndent()

    val droplets = LavaDroplets(input)

    assertThat(droplets.surfaceArea()).isEqualTo(10)
  }

  @Test
  fun `surface area -- larger example -- 64`() {
    val input = """
      2,2,2
      1,2,2
      3,2,2
      2,1,2
      2,3,2
      2,2,1
      2,2,3
      2,2,4
      2,2,6
      1,2,5
      3,2,5
      2,1,5
      2,3,5
    """.trimIndent()

    val droplets = LavaDroplets(input)

    assertThat(droplets.surfaceArea()).isEqualTo(64)
  }

  @Test
  fun `external surface area -- larger example -- 58`() {
    val input = """
      2,2,2
      1,2,2
      3,2,2
      2,1,2
      2,3,2
      2,2,1
      2,2,3
      2,2,4
      2,2,6
      1,2,5
      3,2,5
      2,1,5
      2,3,5
    """.trimIndent()

    val droplets = LavaDroplets(input)

    assertThat(droplets.externalSurfaceArea()).isEqualTo(58)
  }
}