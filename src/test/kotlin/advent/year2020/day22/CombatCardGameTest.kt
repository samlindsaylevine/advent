package advent.year2020.day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombatCardGameTest {

  private val input = """
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

  @Test
  fun `play -- reference input -- score 306`() {
    val game = CombatCardGame.parse(input)

    val score = game.play().score

    assertThat(score).isEqualTo(306)
  }

  @Test
  fun `play -- infinite loop -- player 1 wins`() {
    val input = """
      Player 1:
      43
      19

      Player 2:
      2
      29
      14
    """.trimIndent()

    val game = CombatCardGame.parse(input)

    val result = game.play(recursive = true)

    assertThat(result.winner).isEqualTo(Winner.PLAYER_1)
  }

  @Test
  fun `play -- reference input, recursive -- score 291`() {
    val game = CombatCardGame.parse(input)

    val score = game.play(recursive = true).score

    assertThat(score).isEqualTo(291)
  }
}