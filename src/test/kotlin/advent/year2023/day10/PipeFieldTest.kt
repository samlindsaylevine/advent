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

  @Test
  fun `enclosed tile count -- first example -- 4`() {
    val input = """
      ...........
      .S-------7.
      .|F-----7|.
      .||.....||.
      .||.....||.
      .|L-7.F-J|.
      .|..|.|..|.
      .L--J.L--J.
      ...........
    """.trimIndent()

    val field = PipeField(input)
    val enclosed = field.enclosedTileCount()

    assertThat(enclosed).isEqualTo(4)
  }

  @Test
  fun `enclosed tile count -- squeak through -- 4`() {
    val input = """
      ..........
      .S------7.
      .|F----7|.
      .||....||.
      .||....||.
      .|L-7F-J|.
      .|..||..|.
      .L--JL--J.
      ..........
    """.trimIndent()

    val field = PipeField(input)
    val enclosed = field.enclosedTileCount()

    assertThat(enclosed).isEqualTo(4)
  }

  @Test
  fun `enclosed tile count -- larger example -- 8`() {
    val input = """
      .F----7F7F7F7F-7....
      .|F--7||||||||FJ....
      .||.FJ||||||||L7....
      FJL7L7LJLJ||LJ.L-7..
      L--J.L7...LJS7F-7L7.
      ....F-J..F7FJ|L7L7L7
      ....L7.F7||L7|.L7L7|
      .....|FJLJ|FJ|F7|.LJ
      ....FJL-7.||.||||...
      ....L---J.LJ.LJLJ...
    """.trimIndent()

    val field = PipeField(input)
    val enclosed = field.enclosedTileCount()

    assertThat(enclosed).isEqualTo(8)
  }

  @Test
  fun `enclosed tile count -- last example -- 10`() {
    val input = """
      FF7FSF7F7F7F7F7F---7
      L|LJ||||||||||||F--J
      FL-7LJLJ||||||LJL-77
      F--JF--7||LJLJ7F7FJ-
      L---JF-JLJ.||-FJLJJ7
      |F|F-JF---7F7-L7L|7|
      |FFJF7L7F-JF7|JL---7
      7-L-JL7||F7|L7F-7F7|
      L.L7LFJ|||||FJL7||LJ
      L7JLJL-JLJLJL--JLJ.L
    """.trimIndent()

    val field = PipeField(input)
    val enclosed = field.enclosedTileCount()

    assertThat(enclosed).isEqualTo(10)
  }

}