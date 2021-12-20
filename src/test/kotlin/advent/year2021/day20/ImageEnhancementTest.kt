package advent.year2021.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ImageEnhancementTest {

  private val example = """
    ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

    #..#.
    #....
    ##..#
    ..#..
    ..###
  """.trimIndent().let(::ImageEnhancement)

  @Test
  fun `count -- after two steps -- 35`() {
    val count = example.next(2).count()

    assertThat(count).isEqualTo(35)
  }

  @Test
  fun `count -- after fifty steps -- 3351`() {
    val count = example.next(50).count()

    assertThat(count).isEqualTo(3351)
  }
}