package advent.year2019.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FuelCounterUpperTest {

    @ParameterizedTest(name = "fuelRequired -- mass {0} -- requires {1} fuel")
    @CsvSource("12, 2",
            "1969, 654",
            "100756, 33583")
    fun `fuelRequired -- reference inputs -- reference values`(mass: Int, expectedFuel: Int) {
        val actual = FuelCounterUpper().fuelRequired(mass)

        assertThat(actual).isEqualTo(expectedFuel)
    }

    @ParameterizedTest(name = "totalFuelRequired -- mass {0} -- requires {1} fuel")
    @CsvSource("14, 2",
            "1969, 966",
            "100756, 50346")
    fun `totalFuelRequired -- reference inputs -- reference values`(mass: Int, expectedFuel: Int) {
        val actual = FuelCounterUpper().totalFuelRequired(mass)

        assertThat(actual).isEqualTo(expectedFuel)
    }
}