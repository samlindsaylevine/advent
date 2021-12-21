package advent.year2021.day21

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class DiracDiceTest {

  val example = """
    Player 1 starting position: 4
    Player 2 starting position: 8
  """.trimIndent().let(::DiracDice)

  @Test
  fun `deterministic turn -- from example -- has example result`() {
    val next = example.nextDeterministic()

    assertThat(next).isEqualTo(
      DiracDice(
        players = DiracDice.Player(position = 10, score = 10) to DiracDice.Player(position = 8, score = 0),
        turnsElapsed = 1
      )
    )
  }

  @Test
  fun `deterministic game -- as per example -- has product 739785`() {
    val product = example.deterministicGame().product()

    assertThat(product).isEqualTo(739785)
  }

  @Test
  @Disabled("This is probably too slow to run regularly, but does pass!")
  fun `multiverse -- as per example -- reference number of wins`() {
    val result = DiracMultiverse(example).resolve()

    assertThat(result.playerOneWins).isEqualTo(444356092776315)
    assertThat(result.playerTwoWins).isEqualTo(341960390180808)
  }
}