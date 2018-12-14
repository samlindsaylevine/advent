package advent.year2018.day11

import advent.year2018.day10.Coordinates
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FuelCellGridTest {

    @ParameterizedTest(name = "powerLevel -- grid serial number {0}, {1},{2} -- power is {3}")
    @CsvSource("8, 3, 5, 4",
            "57, 122, 79, -5",
            "39, 217, 196, 0",
            "71, 101, 153, 4")
    fun `powerLevel -- reference inputs -- reference values`(serialNumber: Int,
                                                             x: Int,
                                                             y: Int,
                                                             expectedPower: Int) {
        val grid = FuelCellGrid(serialNumber)

        val power = grid.powerLevel(Coordinates(x, y))

        assertThat(power).isEqualTo(expectedPower)
    }

    @ParameterizedTest(name = "upperLeftOfMostPowerfulSquare -- grid serial number {0} -- {1},{2}")
    @CsvSource("18, 33, 45",
            "42, 21, 61")
    fun `upperLeftOfMostPowerfulSquare -- reference inputs -- reference values`(serialNumber: Int,
                                                                                expectedX: Int,
                                                                                expectedY: Int) {
        val grid = FuelCellGrid(serialNumber)

        val upperLeft = grid.upperLeftOfMostPowerfulSquare()

        val expected = Coordinates(expectedX, expectedY)
        assertThat(upperLeft).isEqualTo(expected)
    }
}