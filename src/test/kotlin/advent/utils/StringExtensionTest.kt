package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StringExtensionTest {

  @Test
  fun `find all numbers -- 1 2 3 fish 100 -- 1, 2, 3, 100`() {
    val input = "1 2 3 fish 100"

    val numbers = input.findAllNumbers()

    assertThat(numbers).containsExactly(1, 2, 3, 100)
  }

  @Test
  fun `find all numbers -- with negatives -- includes negatives`() {
    // This could really be a bunch of separate test cases.
    val input = "-a - fish -100 x-y-z 0 45"

    val numbers = input.findAllNumbers()

    assertThat(numbers).containsExactly(-100, 0, 45)
  }
}