package advent.year2021.day13

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TransparentOrigamiTest {

  private val input = """
    6,10
    0,14
    9,10
    0,3
    10,4
    4,11
    6,0
    6,12
    4,1
    0,13
    10,12
    3,4
    3,0
    8,4
    1,10
    2,14
    8,10
    9,0

    fold along y=7
    fold along x=5
  """.trimIndent().let(::TransparentOrigami)

  @Test
  fun `number of dots -- reference example folded once -- 17`() {
    val folded = input.foldedOnce()

    assertThat(folded.points).hasSize(17)
  }

  @Test
  fun `visualized -- reference example folded all the way -- a square`() {
    val folded = input.folded()

    assertThat(folded.toString()).isEqualTo(
      """
      #####
      #   #
      #   #
      #   #
      #####
    """.trimIndent()
    )
  }

}