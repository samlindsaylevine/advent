package advent.year2022.day9

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RopePathTest {

  @Test
  fun `tail visited -- reference input -- 13 positions`() {
    val input = """
      R 4
      U 4
      L 3
      D 1
      R 4
      D 1
      L 5
      R 2
    """.trimIndent()
    val path = RopePath().move(input.lines())

    val visited = path.tailVisited

    assertThat(visited).hasSize(13)
  }
}