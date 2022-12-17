package advent.year2022.day17

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FallingRocksTest {
  private val input = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"

  @Test
  fun `height -- reference input, 2022 rocks -- 3068`() {
    val rocks = FallingRocks(input)

    val result = rocks.result(2022)

    assertThat(result.height()).isEqualTo(3068)
  }

  @Test
  fun `calculate height -- reference input, 2022 rocks -- 3068`() {
    val rocks = FallingRocks(input)

    assertThat(rocks.calculateHeight(2022)).isEqualTo(3068)
  }

  @Test
  fun `calculate height -- reference input, 1000000000000 rocks -- 1514285714288`() {
    val rocks = FallingRocks(input)

    val result = rocks.calculateHeight(1000000000000L)

    assertThat(result).isEqualTo(1514285714288L)
  }
}