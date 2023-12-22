package advent.utils

import advent.year2019.day10.gcd
import javax.annotation.processing.Generated

/**
 * Represents a rational number. Always guaranteed to be represented in reduced terms.
 */
class Rational private constructor(val numerator: Long, val denominator: Long) {
  companion object {
    fun of(numerator: Long, denominator: Long): Rational {
      if (denominator < 0) return of(-numerator, -denominator)
      val gcd = gcd(numerator, denominator)
      return Rational(numerator / gcd, denominator / gcd)
    }
  }

  operator fun plus(other: Rational) = of(
          this.numerator * other.denominator + this.denominator * other.numerator,
          this.denominator * other.denominator
  )

  operator fun minus(other: Rational) = this + (-1) * other
  operator fun times(other: Rational) = of(this.numerator * other.numerator, this.denominator * other.denominator)
  operator fun div(other: Rational) = of(this.numerator * other.denominator, this.denominator * other.numerator)

  override fun toString() = "$numerator/$denominator"

  fun toLong() = if (denominator == 1L) numerator else throw IllegalStateException("$this is not an integer")

  @Generated
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Rational

    if (numerator != other.numerator) return false
    if (denominator != other.denominator) return false

    return true
  }

  @Generated
  override fun hashCode(): Int {
    var result = numerator.hashCode()
    result = 31 * result + denominator.hashCode()
    return result
  }
}

operator fun Int.times(rational: Rational) = Rational.of(rational.numerator * this, rational.denominator)
operator fun Long.times(rational: Rational) = Rational.of(rational.numerator * this, rational.denominator)