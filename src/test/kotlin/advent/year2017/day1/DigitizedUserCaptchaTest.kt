package advent.year2017.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DigitizedUserCaptchaTest {

    @ParameterizedTest
    @CsvSource(
            "1122, 3",
            "1111, 4",
            "1234, 0",
            "91212129, 9")
    fun `matchesNextSolution -- reference inputs -- yield reference output`(input: String, output: Int) {
        val solution = DigitizedUserCaptcha(input).matchesNextSolution()

        assertThat(solution).isEqualTo(output)
    }

    @ParameterizedTest
    @CsvSource(
            "1212, 6",
            "1221, 0",
            "123425, 4",
            "123123, 12",
            "12131415, 4")
    fun `matchesHalfwayAroundSolution -- reference inputs -- yield reference output`(input: String, output: Int) {
        val solution = DigitizedUserCaptcha(input).matchesHalfwayAroundSolution()

        assertThat(solution).isEqualTo(output)
    }

}