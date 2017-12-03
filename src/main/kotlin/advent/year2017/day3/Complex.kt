package advent.year2017.day3

/**
 * Representation of a complex number with only integer coefficients.
 */
data class Complex(val real: Int, val complex: Int) {

    constructor(rep: String) : this(parse(rep))

    private constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    companion object {
        val i = Complex(0, 1)

        /**
         * Doesn't currently support purely real or purely complex numbers, probably should.
         */
        private val REGEX = "(-?)(\\d+)\\s*([+-])+\\s*(\\d+)i".toRegex()

        private fun parse(rep: String): Pair<Int, Int> {
            val match = REGEX.matchEntire(rep) ?: throw IllegalArgumentException("Not complex number: $rep")
            val realPartNegative = match.groupValues[1] == "-"
            val realPart = (if (realPartNegative) -1 else 1) * match.groupValues[2].toInt()
            val complexPartNegative = match.groupValues[3] == "-"
            val complexPart = (if (complexPartNegative) -1 else 1) * match.groupValues[4].toInt()

            return Pair(realPart, complexPart)
        }
    }

    operator fun plus(other: Complex) = Complex(this.real + other.real, this.complex + other.complex)

    operator fun times(other: Complex) = Complex(this.real * other.real - this.complex * other.complex,
            this.real * other.complex + this.complex * other.real)

    operator fun times(other: Int) = Complex(other * this.real, other * this.complex)

    operator fun unaryMinus() = Complex(-this.real, -this.complex)

    fun pow(exponent: Int): Complex = when {
        exponent < 0 -> throw IllegalArgumentException("Negative exponents not supported")
        exponent == 0 -> Complex(1, 0)
        exponent == 1 -> this
        (exponent % 2 == 0) -> (this * this).pow(exponent / 2)
        else -> this * (this.pow(exponent - 1))
    }
}

operator fun Int.times(complex: Complex) = complex.times(this)