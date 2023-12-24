package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MatricesTest {

  @Test
  fun `rref -- system of 3 equations -- reveals solutions`() {
    val input = listOf(
            listOf(1.0, 2.0, -1.0, 2.0),
            listOf(1.0, 1.0, -1.0, 0.0),
            listOf(2.0, -1.0, 1.0, 3.0)
    )

    val rref = input.rref()

    assertThat(rref).containsExactly(
            listOf(1.0, 0.0, 0.0, 1.0),
            listOf(0.0, 1.0, 0.0, 2.0),
            listOf(0.0, 0.0, 1.0, 3.0)
    )
  }

  // Sample set of equations solved by Wolfram Alpha.
  @Test
  fun `rref -- rational values -- reveals solutions`() {
    val input = listOf(
            listOf(Rational.of(1), Rational.of(2), Rational.of(3), Rational.of(5)),
            listOf(Rational.of(7), Rational.of(11), Rational.of(13), Rational.of(17)),
            listOf(Rational.of(19), Rational.of(23), Rational.of(31), Rational.of(37))
    )

    val rref = rrefRational(input)

    val one = Rational.of(1)
    val zero = Rational.of(0)
    assertThat(rref).containsExactly(
            listOf(one, zero, zero, Rational.of(-5, 3)),
            listOf(zero, one, zero, Rational.of(-2, 21)),
            listOf(zero, zero, one, Rational.of(16, 7))
    )
  }
}