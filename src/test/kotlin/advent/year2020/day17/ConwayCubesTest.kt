package advent.year2020.day17

import advent.year2018.day18.advance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConwayCubesTest {

  @Test
  fun `advanced six times -- count -- 112`() {
    val cubes = ConwayCubes("""
      .#.
      ..#
      ###
    """.trimIndent())

    val finalState = advance(6, cubes, ConwayCubes::next)
    val count = finalState.active.size

    assertThat(count).isEqualTo(112)
  }

  @Test
  fun `hypercubes advanced six times -- count -- 848`() {
    val cubes = ConwayHypercubes("""
      .#.
      ..#
      ###
    """.trimIndent())

    val finalState = advance(6, cubes, ConwayHypercubes::next)
    val count = finalState.active.size

    assertThat(count).isEqualTo(848)
  }
}