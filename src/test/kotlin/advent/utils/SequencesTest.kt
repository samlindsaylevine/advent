package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SequencesTest {

  @Test
  fun `takeWhileInclusive -- always -- includes elements, plus one that missed`() {
    val sequence = sequenceOf(1, 3, 5, 7, 8, 9, 11, 13)

    val elements = sequence.takeWhileInclusive { it % 2 == 1 }

    assertThat(elements.toList()).containsExactly(1, 3, 5, 7, 8)
  }
}