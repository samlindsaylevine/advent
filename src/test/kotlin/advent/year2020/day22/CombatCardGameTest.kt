package advent.year2020.day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombatCardGameTest {

  @Test
  fun `play -- reference input -- score 306`() {
    val input = """
      Player 1:
      9
      2
      6
      3
      1

      Player 2:
      5
      8
      4
      7
      10
    """.trimIndent()

    val game = CombatCardGame.parse(input)

    val score = game.play()

    assertThat(score).isEqualTo(306)
  }
}