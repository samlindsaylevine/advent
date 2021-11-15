package advent.year2018.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FrequencyDriftTest {

    @Test
    fun `parsing -- reference input -- reference total frequency`() {
        val input = """
            +1
            -2
            +3
            +1
        """.trimIndent()

        val result = FrequencyDrift(input).result

        assertThat(result).isEqualTo(3)
    }

    @ParameterizedTest
    @CsvSource("1;-2;3;1, 2",
            "1;-1, 0",
            "3;3;4;-2;-4, 10",
            "-6;3;8;5;-6, 5",
            "7;7;-2;-7;-4, 14")
    fun `firstFrequencyReachedTwice -- reference inputs -- reference outputs`(input: String, expected: Int) {
        val frequencyDrift = FrequencyDrift(input.split(";").map { it.toInt() })

        val firstFrequencyReachedTwice = frequencyDrift.firstFrequencyReachedTwice

        assertThat(firstFrequencyReachedTwice).isEqualTo(expected)
    }
}