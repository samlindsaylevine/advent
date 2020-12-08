package advent.year2020.day8

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HandheldGameTest {

  @Test
  fun `runUntilFirstRepeat -- reference example -- has 5 in accumulator`() {
    val game = HandheldGame.parse("""
      nop +0
      acc +1
      jmp +4
      acc +3
      jmp -3
      acc -99
      acc +1
      jmp -4
      acc +6
    """.trimIndent())

    game.run()

    assertThat(game.accumulator).isEqualTo(5)
  }

  @Test
  fun `fixed -- reference example -- has 8 in accumulator`() {
    val game = HandheldGame.parse("""
      nop +0
      acc +1
      jmp +4
      acc +3
      jmp -3
      acc -99
      acc +1
      jmp -4
      acc +6
    """.trimIndent())

    val fixed = game.fixed()

    assertThat(fixed.accumulator).isEqualTo(8)
  }
}