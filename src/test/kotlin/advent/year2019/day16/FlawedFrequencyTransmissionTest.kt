package advent.year2019.day16

import advent.utils.digits
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FlawedFrequencyTransmissionTest {

    @Test
    fun `pattern -- index 0 -- 1, 0, -1, 0 and so on`() {
        val pattern = FlawedFrequencyTransmission().pattern(0)

        assertThat(pattern.take(8).toList())
                .containsExactly(1, 0, -1, 0, 1, 0, -1, 0)
    }

    @Test
    fun `pattern -- index 1 -- 0, 1, 1, 0, 0, -1, -1, 0, 0 and so on`() {
        val pattern = FlawedFrequencyTransmission().pattern(1)

        assertThat(pattern.take(15).toList())
                .containsExactly(0, 1, 1, 0, 0, -1, -1, 0, 0, 1, 1, 0, 0, -1, -1)
    }

    @Test
    fun `pattern -- index 2 -- 0, 0, 1, 1, 1, 0, 0, 0, -1, -1, -1 and so on`() {
        val pattern = FlawedFrequencyTransmission().pattern(2)

        assertThat(pattern.take(11).toList())
                .containsExactly(0, 0, 1, 1, 1, 0, 0, 0, -1, -1, -1)
    }

    @ParameterizedTest(name = "phase -- {0} -- {1}")
    @CsvSource("12345678, 48226158",
            "48226158, 34040438",
            "34040438, 03415518",
            "03415518, 01029498")
    fun `phase -- reference inputs -- reference outputs`(input: String, expected: String) {
        val digits = input.digits()

        val output = FlawedFrequencyTransmission().phase(digits)

        assertThat(output).isEqualTo(expected.digits())
    }

    @ParameterizedTest(name = "100 phases -- {0} -- first 8 digits are {1}")
    @CsvSource("80871224585914546619083218645595, 24176176",
            "19617804207202209144916044189917, 73745418",
            "69317163492948606335995924319873, 52432133")
    fun `100 phases -- reference inputs -- reference first 8 digits`(input: String, expected: String) {
        val digits = input.digits()

        val output = FlawedFrequencyTransmission().phases(digits, 100)

        assertThat(output.take(8)).isEqualTo(expected.digits())
    }

    @ParameterizedTest(name = "message -- {0} -- {1}")
    @CsvSource("03036732577212944063491565474664, 84462026",
            "02935109699940807407585447034323, 78725270",
            "03081770884921959731165446850517, 53553731")
    fun `message -- reference inputs -- reference 8 digits`(input: String, expected: String) {
        val digits = input.digits()

        val output = FlawedFrequencyTransmission().message(digits)

        assertThat(output).isEqualTo(expected.digits())
    }
}