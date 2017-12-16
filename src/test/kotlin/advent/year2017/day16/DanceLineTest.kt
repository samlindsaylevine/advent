package advent.year2017.day16

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DanceLineTest {

    private val REFERENCE_INPUT = listOf(
            "s1",
            "x3/4",
            "pe/b"
    )

    @Test
    fun `constructor -- size 16 -- abcdefghijklmnop`() {
        val order = DanceLine(16).order

        assertThat(order).isEqualTo("abcdefghijklmnop")
    }

    @Test
    fun `spin -- abcde spin 3 -- cdeab`() {
        val result = DanceLine(5).spin(3)

        assertThat(result.order).isEqualTo("cdeab")
    }

    @ParameterizedTest(name = "exchange -- abcde positions {0}, {1} -- {2}")
    @CsvSource("2, 4, abedc",
            "1, 0, bacde",
            "4, 0, ebcda",
            "4, 3, abced")
    fun `exchange -- abcde exchange positions -- expected result`(posA: Int, posB: Int, expected: String) {
        val result = DanceLine(5).exchange(posA, posB)

        assertThat(result.order).isEqualTo(expected)
    }

    @Test
    fun `partner -- abcde partner b, d -- adcbe`() {
        val result = DanceLine(5).partner('b', 'd')

        assertThat(result.order).isEqualTo("adcbe")
    }

    @Test
    fun `dance -- reference input -- reference output`() {
        val line = DanceLine(5)

        val result = line.dance(REFERENCE_INPUT)

        assertThat(result.order).isEqualTo("baedc")
    }

    @Test
    fun `dance -- reference input, two dances -- reference output`() {
        val line = DanceLine(5)

        val result = line.dance(REFERENCE_INPUT, 2)

        assertThat(result.order).isEqualTo("ceadb")
    }


}