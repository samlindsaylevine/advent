package advent.year2022.day8

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TreehouseForestTest {

  @ParameterizedTest
  @CsvSource(
    "3, 1",
    "2, 2",
    "1, 3",
    "3, 3"
  )
  fun `is point visible -- reference input, not supposed to be visible -- not visible`(x: Int, y: Int) {
    val point = Point(x, y)
    val input = """
      30373
      25512
      65332
      33549
      35390
    """.trimIndent()
    val forest = TreehouseForest(input)

    val trees = forest.visibleTrees()

    assertThat(trees).doesNotContain(point)
  }

  @ParameterizedTest
  @CsvSource(
    "1, 1",
    "2, 1",
    "1, 2",
    "3, 2",
    "2, 3"
  )
  fun `is point visible -- reference input, supposed to be visible -- visible`(x: Int, y: Int) {
    val point = Point(x, y)
    val input = """
      30373
      25512
      65332
      33549
      35390
    """.trimIndent()
    val forest = TreehouseForest(input)

    val trees = forest.visibleTrees()

    assertThat(trees).contains(point)
  }

  @Test
  fun `visible tree count -- reference input -- 21`() {
    val input = """
      30373
      25512
      65332
      33549
      35390
    """.trimIndent()
    val forest = TreehouseForest(input)

    val trees = forest.visibleTrees()

    assertThat(trees).hasSize(21)
  }

  @Test
  fun `max scenic score -- reference input -- 8`() {
    val input = """
      30373
      25512
      65332
      33549
      35390
    """.trimIndent()
    val forest = TreehouseForest(input)

    val score = forest.maxScenicScore()

    assertThat(score).isEqualTo(8)
  }
}