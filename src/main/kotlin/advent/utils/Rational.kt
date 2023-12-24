package advent.utils

import advent.year2019.day10.gcd
import java.math.BigInteger
import javax.annotation.processing.Generated

/**
 * Represents a rational number. Always guaranteed to be represented in reduced terms.
 */
class Rational private constructor(val numerator: BigInteger, val denominator: BigInteger) : Number() {
  companion object {
    fun of(numerator: Long, denominator: Long) = of(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator))

    fun of(numerator: BigInteger, denominator: BigInteger): Rational {
      if (denominator < BigInteger.ZERO) return of(-numerator, -denominator)
      val gcd = gcd(numerator, denominator)
      return Rational(numerator / gcd, denominator / gcd)
    }

    fun of(value: Long) = of(value, 1)
  }

  operator fun plus(other: Rational) = of(
          this.numerator * other.denominator + this.denominator * other.numerator,
          this.denominator * other.denominator
  )

  operator fun minus(other: Rational) = this + (-1) * other
  operator fun times(other: Rational) = of(this.numerator * other.numerator, this.denominator * other.denominator)
  operator fun div(other: Rational) = of(this.numerator * other.denominator, this.denominator * other.numerator)

  override fun toString() = "$numerator/$denominator"

  fun toBigInteger() = if (denominator == BigInteger.ONE) numerator else throw IllegalStateException("$this is not an integer")
  override fun toLong() = toBigInteger().toLong()
  override fun toShort() = toLong().toShort()
  override fun toByte() = toLong().toByte()
  override fun toChar() = toLong().toInt().toChar()
  override fun toInt() = toLong().toInt()
  override fun toDouble() = numerator.toDouble() / denominator.toDouble()
  override fun toFloat() = toDouble().toFloat()

  @Generated
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Rational

    if (numerator != other.numerator) return false
    return denominator == other.denominator
  }

  @Generated
  override fun hashCode(): Int {
    var result = numerator.hashCode()
    result = 31 * result + denominator.hashCode()
    return result
  }

}

operator fun Int.times(rational: Rational) = Rational.of(rational.numerator * this.toBigInteger(), rational.denominator)
operator fun Long.times(rational: Rational) = Rational.of(rational.numerator * this.toBigInteger(), rational.denominator)