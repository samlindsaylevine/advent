package advent.year2022.day17

import advent.year2022.FallingRocks
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
}