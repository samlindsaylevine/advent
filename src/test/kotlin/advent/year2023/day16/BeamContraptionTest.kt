package advent.year2023.day16

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BeamContraptionTest {

  @Test
  fun `energizedTileCount -- reference input -- 46`() {
    val input = """
      .|...\....
      |.-.\.....
      .....|-...
      ........|.
      ..........
      .........\
      ..../.\\..
      .-.-/..|..
      .|....-|.\
      ..//.|....
    """.trimIndent()

    val contraption = BeamContraption(input)
    val result = contraption.energizedTileCount()

    assertThat(result).isEqualTo(46)
  }

  @Test
  fun `optimumEnergizedTileCount -- reference input -- 51`() {
    val input = """
      .|...\....
      |.-.\.....
      .....|-...
      ........|.
      ..........
      .........\
      ..../.\\..
      .-.-/..|..
      .|....-|.\
      ..//.|....
    """.trimIndent()

    val contraption = BeamContraption(input)
    val result = contraption.optimumEnergizedTileCount()

    assertThat(result).isEqualTo(51)
  }
}