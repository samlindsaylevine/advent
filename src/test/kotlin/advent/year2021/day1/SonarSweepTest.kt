package advent.year2021.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SonarSweepTest {

  private val reference = SonarSweep(listOf(
    199,
    200,
    208,
    210,
    200,
    207,
    240,
    269,
    260,
    263
  ))

  @Test
  fun `increases -- reference input -- 7 increases`() {
    val increases = reference.increases

    assertThat(increases).isEqualTo(7)
  }

  @Test
  fun `slidingWindowIncreases -- reference input -- 5 increases`() {
    val increases = reference.slidingWindowIncreases

    assertThat(increases).isEqualTo(5)
  }
}