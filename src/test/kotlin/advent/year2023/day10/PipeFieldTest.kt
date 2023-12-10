package advent.year2023.day10

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PipeFieldTest {

  @Test
  fun `stepsToFarthest -- small example -- 4 steps`() {
    val input = """
      .....
      .S-7.
      .|.|.
      .L-J.
      .....
    """.trimIndent()

    val field = PipeField(input)
    val steps = field.stepsToFarthest()

    assertThat(steps).isEqualTo(4)
  }

  @Test
  fun `stepsToFarthest --- more complex example -- 8 steps`() {
    val input = """
      7-F7-
      .FJ|7
      SJLL7
      |F--J
      LJ.LJ
    """.trimIndent()

    val field = PipeField(input)
    val steps = field.stepsToFarthest()

    assertThat(steps).isEqualTo(8)
  }
}