package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RationalsTest {

  @Test
  fun `sum -- one half and one third -- five sixths`() {
    val sum = Rational.of(1, 2) + Rational.of(1, 3)

    assertThat(sum).isEqualTo(Rational.of(5, 6))
  }

  @Test
  fun `difference -- one half and one third -- one sixth`() {
    val difference = Rational.of(1, 2) - Rational.of(1, 3)

    assertThat(difference).isEqualTo(Rational.of(1, 6))
  }

  @Test
  fun `product -- one half and two thirds -- one third`() {
    val product = Rational.of(1, 2) * Rational.of(2, 3)

    assertThat(product).isEqualTo(Rational.of(1, 3))
  }

  @Test
  fun `quotient -- one half and two thirds -- three quarters`() {
    val quotient = Rational.of(1, 2) / Rational.of(2, 3)

    assertThat(quotient).isEqualTo(Rational.of(3, 4))
  }

  @Test
  fun `toString -- unreduced input -- reduced representation`() {
    val rational = Rational.of(1000, 2000)

    assertThat(rational.toString()).isEqualTo("1/2")
  }

  @Test
  fun `equals -- reduced and unreduced representations -- are equal`() {
    val reduced = Rational.of(2, 3)
    val unreduced = Rational.of(14, 21)

    assertThat(reduced).isEqualTo(unreduced)
  }

  @Test
  fun `equals -- reduced and unreduced representations, with negative factor -- are equal`() {
    val reduced = Rational.of(2, 3)
    val unreduced = Rational.of(-14, -21)

    assertThat(reduced).isEqualTo(unreduced)
  }

  @Test
  fun `sum -- same denominator -- result has same denominator`() {
    val sum = Rational.of(1, 17161) + Rational.of(2, 17161)

    assertThat(sum).isEqualTo(Rational.of(3, 17161))
  }
}