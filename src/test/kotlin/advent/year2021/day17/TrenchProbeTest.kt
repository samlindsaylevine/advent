package advent.year2021.day17

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TrenchProbeTest {

  private val example = "target area: x=20..30, y=-10..-5".let(::TrenchTarget)

  @Test
  fun `maxHeightToHit -- example -- 45`() {
    assertThat(example.maxHeightToHit()).isEqualTo(45)
  }

  @Test
  fun `probesThatHit -- example -- size 112`() {
    assertThat(example.probesThatHit()).hasSize(112)
  }
}