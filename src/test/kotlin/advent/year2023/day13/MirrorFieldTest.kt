package advent.year2023.day13

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MirrorFieldTest {

  @Test
  fun `lines -- first example -- vertical line 5`() {
    val input = """
      #.##..##.
      ..#.##.#.
      ##......#
      ##......#
      ..#.##.#.
      ..##..##.
      #.#.##.#.
    """.trimIndent()
    val field = MirrorField(input)

    assertThat(field.verticalLineX).isEqualTo(5)
    assertThat(field.horizontalLineY).isNull()
  }

  @Test
  fun `lines -- second example -- horizontal line 4`() {
    val input = """
      #...##..#
      #....#..#
      ..##..###
      #####.##.
      #####.##.
      ..##..###
      #....#..#
    """.trimIndent()
    val field = MirrorField(input)

    assertThat(field.verticalLineX).isNull()
    assertThat(field.horizontalLineY).isEqualTo(4)
  }

  @Test
  fun `summarize -- sample input -- 405`() {
    val input = """
      #.##..##.
      ..#.##.#.
      ##......#
      ##......#
      ..#.##.#.
      ..##..##.
      #.#.##.#.

      #...##..#
      #....#..#
      ..##..###
      #####.##.
      #####.##.
      ..##..###
      #....#..#
    """.trimIndent()
    val fields = MirrorFields(input)

    val sum = fields.summarize()

    assertThat(sum).isEqualTo(405)
  }

  @Test
  fun `lines -- first example, desmudged -- horizontal line 3`() {
    val input = """
      #.##..##.
      ..#.##.#.
      ##......#
      ##......#
      ..#.##.#.
      ..##..##.
      #.#.##.#.
    """.trimIndent()
    val field = MirrorField(input).desmudged()

    assertThat(field.verticalLineX).isNull()
    assertThat(field.horizontalLineY).isEqualTo(3)
  }

  @Test
  fun `lines -- second example, desmudged -- horizontal line 1`() {
    val input = """
      #...##..#
      #....#..#
      ..##..###
      #####.##.
      #####.##.
      ..##..###
      #....#..#
    """.trimIndent()
    val field = MirrorField(input).desmudged()

    assertThat(field.verticalLineX).isNull()
    assertThat(field.horizontalLineY).isEqualTo(1)
  }


  @Test
  fun `summarize -- sample input, desmudged -- 400`() {
    val input = """
      #.##..##.
      ..#.##.#.
      ##......#
      ##......#
      ..#.##.#.
      ..##..##.
      #.#.##.#.

      #...##..#
      #....#..#
      ..##..###
      #####.##.
      #####.##.
      ..##..###
      #....#..#
    """.trimIndent()
    val fields = MirrorFields(input)

    val sum = fields.desmudged().summarize()

    assertThat(sum).isEqualTo(400)
  }
}