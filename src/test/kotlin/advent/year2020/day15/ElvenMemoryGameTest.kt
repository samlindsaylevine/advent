package advent.year2020.day15

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ElvenMemoryGameTest {

  @Test
  fun `sequence -- reference starting numbers -- reference first 10 values`() {
    val game = ElvenMemoryGame(listOf(0, 3, 6))

    val numbers = game.spoken().take(10).toList()

    assertThat(numbers).containsExactly(0, 3, 6, 0, 3, 3, 1, 0, 4, 0)
  }

  @Test
  fun `2020th number -- reference starting numbers -- 436`() {
    val game = ElvenMemoryGame(listOf(0, 3, 6))

    val number = game.spoken().elementAt(2019)

    assertThat(number).isEqualTo(436)
  }
}