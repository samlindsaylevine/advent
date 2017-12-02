package advent.year2017.day2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CorruptedSpreadsheetTest {

    @Test
    fun `parse -- reference input -- converted to 2d array`() {
        val input = """5 1 9 5
7 5 3
2 4 6 8"""

        val result = CorruptedSpreadsheet.parse(input.split("\n").asSequence())

        assertThat(result).containsExactly(listOf(5, 1, 9, 5),
                listOf(7, 5, 3),
                listOf(2, 4, 6, 8))
    }

    @Test
    fun `checksum -- reference input -- reference output`() {
        val input = CorruptedSpreadsheet(listOf(
                listOf(5, 1, 9, 5),
                listOf(7, 5, 3),
                listOf(2, 4, 6, 8)))

        val checksum = input.checksum()

        assertThat(checksum).isEqualTo(18)
    }

    @Test
    fun `evenlyDivisibleSum -- reference input -- reference output`() {
        val input = CorruptedSpreadsheet(listOf(
                listOf(5, 9, 2, 8),
                listOf(9, 4, 7, 3),
                listOf(3, 8, 6, 5)))

        val evenlyDivisibleSum = input.evenlyDivisibleSum()

        assertThat(evenlyDivisibleSum).isEqualTo(9)
    }
}