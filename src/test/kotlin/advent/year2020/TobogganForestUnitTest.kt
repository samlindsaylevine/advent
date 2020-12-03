package advent.year2020

import advent.year2020.day3.TobogganForest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TobogganForestUnitTest {

  @ParameterizedTest
  @CsvSource("1, 1, 2",
          "3, 1, 7",
          "5, 1, 3",
          "7, 1, 4",
          "1, 2, 2")
  fun `treesEncountered -- reference inputs -- reference counts`(right: Int, down: Int, trees: Int) {
    val forest = TobogganForest.parse("""
      ..##.......
      #...#...#..
      .#....#..#.
      ..#.#...#.#
      .#...##..#.
      ..#.##.....
      .#.#.#....#
      .#........#
      #.##...#...
      #...##....#
      .#..#...#.#
    """.trimIndent())

    val treesEncountered = forest.treesEncountered(right, down)

    assertThat(treesEncountered).isEqualTo(trees)
  }

  @Test
  fun `slopesProduct -- reference input -- reference total product`() {
    val forest = TobogganForest.parse("""
      ..##.......
      #...#...#..
      .#....#..#.
      ..#.#...#.#
      .#...##..#.
      ..#.##.....
      .#.#.#....#
      .#........#
      #.##...#...
      #...##....#
      .#..#...#.#
    """.trimIndent())

    val product = forest.slopesProduct()

    assertThat(product).isEqualTo(336)
  }
}