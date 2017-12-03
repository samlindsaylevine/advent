package advent.year2017.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ComplexTest {

    @Test
    fun `string constructor -- positive terms -- gives complex number`() {
        val complex = Complex("2 + 3i")

        assertThat(complex).isEqualTo(Complex(2, 3))
    }

    @Test
    fun `string constructor -- negative terms -- gives complex number`() {
        val complex = Complex("-3 - 4i")

        assertThat(complex).isEqualTo(Complex(-3, -4))
    }

    @Test
    fun `plus -- complex numbers -- yields expected sum`() {
        val result = Complex("12 - 9i") + Complex("-3 + 2i")

        assertThat(result).isEqualTo(Complex("9 - 7i"))
    }

    @Test
    fun `times -- complex numbers -- yields expected product`() {
        val result = Complex("1 + 2i") * Complex("2 - 1i")

        assertThat(result).isEqualTo(Complex("4 + 3i"))
    }

    @Test
    fun `times -- real times complex -- yields expected product`() {
        val result = 3 * Complex("1 + 2i")

        assertThat(result).isEqualTo(Complex("3 + 6i"))
    }

    @Test
    fun `pow -- i to the tenth -- negative one`() {
        val result = Complex.i.pow(10)

        assertThat(result).isEqualTo(Complex(-1, 0))
    }
}