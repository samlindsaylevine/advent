package advent.year2023.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColoredLagoonTest {

  @Test
  fun `volume -- reference input -- 62`() {
    val input = """
      R 6 (#70c710)
      D 5 (#0dc571)
      L 2 (#5713f0)
      D 2 (#d2c081)
      R 2 (#59c680)
      D 2 (#411b91)
      L 5 (#8ceee2)
      U 2 (#caa173)
      L 1 (#1b58a2)
      U 2 (#caa171)
      R 2 (#7807d2)
      U 3 (#a77fa3)
      L 2 (#015232)
      U 2 (#7a21e3)
    """.trimIndent()
    val lagoon = ColoredLagoon.of(input, ColoredLagoon::partOne)

    val volume = lagoon.volume()

    assertThat(volume).isEqualTo(62)
  }

  @Test
  fun `volume -- reference input, part two -- 952408144115`() {
    val input = """
      R 6 (#70c710)
      D 5 (#0dc571)
      L 2 (#5713f0)
      D 2 (#d2c081)
      R 2 (#59c680)
      D 2 (#411b91)
      L 5 (#8ceee2)
      U 2 (#caa173)
      L 1 (#1b58a2)
      U 2 (#caa171)
      R 2 (#7807d2)
      U 3 (#a77fa3)
      L 2 (#015232)
      U 2 (#7a21e3)
    """.trimIndent()
    val lagoon = ColoredLagoon.of(input, ColoredLagoon::partTwo)

    val volume = lagoon.volume()

    assertThat(volume).isEqualTo(952408144115)
  }
}