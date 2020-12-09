package advent.year2020.day9

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExchangeMaskingAdditionSystemTest {

  @Test
  fun `firstInvalid -- reference example -- 127`() {
    val system = ExchangeMaskingAdditionSystem(preambleLength = 5)

    val input = listOf<Long>(
            35,
            20,
            15,
            25,
            47,
            40,
            62,
            55,
            65,
            95,
            102,
            117,
            150,
            182,
            127,
            219,
            299,
            277,
            309,
            576
    )

    val first = system.firstInvalid(input)

    assertThat(first).isEqualTo(127L)
  }

  @Test
  fun `encryptionWeakness -- reference example -- 62`() {
    val system = ExchangeMaskingAdditionSystem(preambleLength = 5)

    val input = listOf<Long>(
            35,
            20,
            15,
            25,
            47,
            40,
            62,
            55,
            65,
            95,
            102,
            117,
            150,
            182,
            127,
            219,
            299,
            277,
            309,
            576
    )

    val weakness = system.encryptionWeakness(input)

    assertThat(weakness).isEqualTo(62L)
  }
}