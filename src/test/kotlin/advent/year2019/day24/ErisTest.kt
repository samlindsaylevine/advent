package advent.year2019.day24

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ErisTest {

  @Test
  fun `next -- initial reference -- gives minute 1`() {
    val state = """
      ....#
      #..#.
      #..##
      ..#..
      #....
    """.trimIndent()

    val next = ErisState.parse(state).next()

    assertThat(next).isEqualTo(ErisState.parse("""
      #..#.
      ####.
      ###.#
      ##.##
      .##..
    """.trimIndent()))
  }

  @Test
  fun `next -- minute 1 reference -- gives minute 2`() {
    val state = """
      #..#.
      ####.
      ###.#
      ##.##
      .##..
    """.trimIndent()

    val next = ErisState.parse(state).next()

    assertThat(next).isEqualTo(ErisState.parse("""
      #####
      ....#
      ....#
      ...#.
      #.###
    """.trimIndent()))
  }

  @Test
  fun `next -- minute 2 reference -- gives minute 3`() {
    val state = """
      #####
      ....#
      ....#
      ...#.
      #.###
    """.trimIndent()

    val next = ErisState.parse(state).next()

    assertThat(next).isEqualTo(ErisState.parse("""
      #....
      ####.
      ...##
      #.##.
      .##.#
    """.trimIndent()))
  }

  @Test
  fun `next -- minute 3 reference -- gives minute 4`() {
    val state = """
      #....
      ####.
      ...##
      #.##.
      .##.#
    """.trimIndent()

    val next = ErisState.parse(state).next()

    assertThat(next).isEqualTo(ErisState.parse("""
      ####.
      ....#
      ##..#
      .....
      ##...
    """.trimIndent()))
  }

  @Test
  fun `firstToAppearTwice -- reference -- gives reference output`() {
    val state = """
      ....#
      #..#.
      #..##
      ..#..
      #....
    """.trimIndent()

    val firstToAppearTwice = Eris(ErisState.parse(state)).firstToAppearTwice()

    assertThat(firstToAppearTwice).isEqualTo(ErisState.parse("""
      .....
      .....
      .....
      #....
      .#...
    """.trimIndent()))
  }

  @Test
  fun `biodiversity -- reference -- gives reference value`() {
    val state = """
      .....
      .....
      .....
      #....
      .#...
    """.trimIndent()

    val biodiversity = ErisState.parse(state).biodiversity()

    assertThat(biodiversity).isEqualTo(2129920L)
  }
}