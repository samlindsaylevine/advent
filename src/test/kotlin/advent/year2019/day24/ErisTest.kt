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

    val next = SimpleErisState.parse(state).next()

    assertThat(next).isEqualTo(SimpleErisState.parse("""
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

    val next = SimpleErisState.parse(state).next()

    assertThat(next).isEqualTo(SimpleErisState.parse("""
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

    val next = SimpleErisState.parse(state).next()

    assertThat(next).isEqualTo(SimpleErisState.parse("""
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

    val next = SimpleErisState.parse(state).next()

    assertThat(next).isEqualTo(SimpleErisState.parse("""
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

    val firstToAppearTwice = SimpleEris(SimpleErisState.parse(state)).firstToAppearTwice()

    assertThat(firstToAppearTwice).isEqualTo(SimpleErisState.parse("""
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

    val biodiversity = SimpleErisState.parse(state).biodiversity()

    assertThat(biodiversity).isEqualTo(2129920L)
  }

  @Test
  fun `recursive eris -- reference example, advanced 10 times -- has reference state`() {
    val initialFloor = RecursiveErisState(SimpleErisState.parse("""
      ....#
      #..#.
      #.?##
      ..#..
      #....
    """.trimIndent()))

    val advanced = initialFloor.advanced(10)

    val expected = RecursiveErisState(mapOf(-5 to SimpleErisState.parse("""
      ..#..
      .#.#.
      ..?.#
      .#.#.
      ..#..
    """.trimIndent()),
            -4 to SimpleErisState.parse("""
      ...#.
      ...##
      ..?..
      ...##
      ...#.
    """.trimIndent()),
            -3 to SimpleErisState.parse("""
      #.#..
      .#...
      ..?..
      .#...
      #.#..
    """.trimIndent()),
            -2 to SimpleErisState.parse("""
      .#.##
      ....#
      ..?.#
      ...##
      .###.
    """.trimIndent()),
            -1 to SimpleErisState.parse("""
      #..##
      ...##
      ..?..
      ...#.
      .####
    """.trimIndent()),
            0 to SimpleErisState.parse("""
      .#...
      .#.##
      .#?..
      .....
      .....
    """.trimIndent()),
            1 to SimpleErisState.parse("""
      .##..
      #..##
      ..?.#
      ##.##
      #####
    """.trimIndent()),
            2 to SimpleErisState.parse("""
      ###..
      ##.#.
      #.?..
      .#.##
      #.#..
    """.trimIndent()),
            3 to SimpleErisState.parse("""
      ..###
      .....
      #.?..
      #....
      #...#
    """.trimIndent()),
            4 to SimpleErisState.parse("""
      .###.
      #..#.
      #.?..
      ##.#.
      .....
    """.trimIndent()),
            5 to SimpleErisState.parse("""
      ####.
      #..#.
      #.?#.
      ####.
      .....
    """.trimIndent())))

    assertThat(advanced).isEqualTo(expected)
    assertThat(advanced.bugCount()).isEqualTo(99)
  }
}