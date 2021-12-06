package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapsTest {

  @Test
  fun `merge -- integers -- sums where appropriate`() {
    val a = mapOf(
      1 to 1,
      2 to 2,
      3 to 3,
      4 to 4
    )

    val b = mapOf(
      3 to 3,
      4 to 4,
      5 to 5
    )

    val result = a.merge(b, Int::plus)

    assertThat(result).isEqualTo(
      mapOf(
        1 to 1,
        2 to 2,
        3 to 6,
        4 to 8,
        5 to 5
      )
    )
  }
}