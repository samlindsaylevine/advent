package advent.year2018.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AdvanceTest {

    @ParameterizedTest(name = "advance -- start with 1, halve or add 9, {0} times -- is {1}")
    @CsvSource("0, 1",
            "1, 10",
            "2, 5",
            "3, 14",
            "4, 7",
            "5, 16",
            "6, 8",
            "7, 4",
            "8, 2",
            "9, 1",
            "10, 10",
            "11, 5")
    fun `advance -- halve or add 9 -- gives expected values for small numbers of repeats`(times: Int, expected: Int) {
        fun step(input: Int) = if (input % 2 == 0) input / 2 else input + 9

        val result = advance(times = times, start = 1) { step(it) }

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `advance -- halve or add 9, huge number of repeats -- gives expected value`() {
        fun step(input: Int) = if (input % 2 == 0) input / 2 else input + 9

        // You can see that we hit the loop after step 1. Then, every 9 steps after that loops.
        val result = advance(times = 9_000_005, start = 1) { step(it) }

        assertThat(result).isEqualTo(16)
    }

    @Test
    fun `advance -- short-circuits through loops`() {
        val visited = mutableListOf<Int>()

        fun step(input: Int): Int {
            visited.add(input)
            return 1 - input
        }

        val result = advance(times = 1000, start = 1) { step(it) }

        assertThat(result).isEqualTo(1)
        assertThat(visited).containsExactly(1, 0)
    }
}