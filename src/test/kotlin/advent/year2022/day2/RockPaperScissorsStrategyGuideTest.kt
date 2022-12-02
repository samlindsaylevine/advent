package advent.year2022.day2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RockPaperScissorsStrategyGuideTest {

  @Test
  fun `total score -- reference input, with second column as move -- 15`() {
    val input = """
      A Y
      B X
      C Z
    """.trimIndent()
    val guide = RockPaperScissorsStrategyGuide.withSecondColumnAsMove(input)

    val score = guide.score

    assertThat(score).isEqualTo(15)
  }

  @Test
  fun `total score -- reference input, with second column as outcome -- 12`() {
    val input = """
      A Y
      B X
      C Z
    """.trimIndent()
    val guide = RockPaperScissorsStrategyGuide.withSecondColumnAsOutcome(input)

    val score = guide.score

    assertThat(score).isEqualTo(12)
  }
}