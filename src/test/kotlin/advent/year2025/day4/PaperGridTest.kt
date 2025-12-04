package advent.year2025.day4

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PaperGridTest {

  @Test
  fun `countAccessible -- reference example -- 13`() {
    val input = """
      ..@@.@@@@.
      @@@.@.@.@@
      @@@@@.@.@@
      @.@@@@..@.
      @@.@@@@.@@
      .@@@@@@@.@
      .@.@.@.@@@
      @.@@@.@@@@
      .@@@@@@@@.
      @.@.@@@.@.
    """.trimIndent()
    val grid = PaperGrid(input)

    val count = grid.countAccessible()

    Assertions.assertThat(count).isEqualTo(13)
  }

  @Test
  fun `totalRemovable -- reference example -- 43`() {
    val input = """
      ..@@.@@@@.
      @@@.@.@.@@
      @@@@@.@.@@
      @.@@@@..@.
      @@.@@@@.@@
      .@@@@@@@.@
      .@.@.@.@@@
      @.@@@.@@@@
      .@@@@@@@@.
      @.@.@@@.@.
    """.trimIndent()
    val grid = PaperGrid(input)

    val total = grid.totalRemovable()

    Assertions.assertThat(total).isEqualTo(43)
  }
}