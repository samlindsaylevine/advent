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
    val path = RopePath(2).move(input.lines())

    val visited = path.tailVisited

    assertThat(visited).hasSize(13)
  }

  @Test
  fun `tail visited -- large rope, small reference input -- 1 position`() {
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
    val path = RopePath(10).move(input.lines())

    val visited = path.tailVisited

    assertThat(visited).hasSize(1)
  }

  @Test
  fun `tail visited -- large rope, large reference input -- 36 positions`() {
    val input = """
      R 5
      U 8
      L 8
      D 3
      R 17
      D 10
      L 25
      U 20
    """.trimIndent()
    val path = RopePath(10).move(input.lines())

    val visited = path.tailVisited

    assertThat(visited).hasSize(36)
  }
}