package advent.year2023.day24

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

class HailstonesTest {

  @Test
  fun `xyIntersectionsWithinTestArea -- reference input and range -- 2`() {
    val input = """
      19, 13, 30 @ -2,  1, -2
      18, 19, 22 @ -1, -1, -2
      20, 25, 34 @ -2, -2, -4
      12, 31, 28 @ -1, -2, -1
      20, 19, 15 @  1, -5, -3
    """.trimIndent()
    val hailstones = Hailstones(input)

    val count = hailstones.xyIntersectionsWithinTestArea(7L..27L)

    assertThat(count).isEqualTo(2)
  }

  @Test
  fun `magic rock result -- reference input -- 47`() {
    val input = """
      19, 13, 30 @ -2,  1, -2
      18, 19, 22 @ -1, -1, -2
      20, 25, 34 @ -2, -2, -4
      12, 31, 28 @ -1, -2, -1
      20, 19, 15 @  1, -5, -3
    """.trimIndent()
    val hailstones = Hailstones(input)

    val magicRock = hailstones.magicRockResult()

    assertThat(magicRock).isEqualTo(BigInteger.valueOf(47))
  }
}