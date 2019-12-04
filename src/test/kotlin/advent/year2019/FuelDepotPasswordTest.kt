package advent.year2019

import advent.year2019.day4.FuelDepotPassword
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FuelDepotPasswordTest {

    @ParameterizedTest(name = "isValid -- {0} -- {1}")
    @CsvSource("111111, true",
            "223450, false",
            "123789, false")
    fun `isValid -- reference values -- reference validity`(number: Int, expected: Boolean) {
        val password = FuelDepotPassword(number)

        val actual = password.isValid()

        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest(name = "isValid -- {0} -- {1}")
    @CsvSource("112233, true",
            "123444, false",
            "111122, true")
    fun `isValidExcludingLargerGroups -- reference values -- reference validity`(number: Int, expected: Boolean) {
        val password = FuelDepotPassword(number)

        val actual = password.isValidExcludingLargerGroups()

        assertThat(actual).isEqualTo(expected)
    }
}