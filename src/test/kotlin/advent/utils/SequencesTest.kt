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

  @Test
  fun `find linear recurrence -- repeat digits of pi, plus 100 each time -- finds recurrence`() {
    val sequence = sequenceOf(
            0, 0, 0,
            3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5,
            103, 101, 104, 101, 105, 109, 102, 106, 105, 103, 105,
            203, 201, 204, 201, 205, 209, 202, 206, 205, 203, 205
    )

    val recurrence = sequence.findLinearRecurrence()

    assertThat(recurrence).isEqualTo(
            LinearRecurrence(
                    firstIndex = 3,
                    period = 11,
                    deltaPerCycle = 100,
                    individualValues = listOf(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5)
            )
    )
    assertThat(recurrence?.get(25)).isEqualTo(203)
  }

  @Test
  fun `size -- finite sequence -- size equal to number of elements`() {
    val sequence = sequenceOf(1, 2, 3, 4, 5, 6)

    val size = sequence.size()

    assertThat(size).isEqualTo(6)
  }
}