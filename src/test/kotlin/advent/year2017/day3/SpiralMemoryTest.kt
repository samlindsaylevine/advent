package advent.year2017.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SpiralMemoryTest {

    @ParameterizedTest
    @CsvSource(
            "1, 0, 0",
            "2, 1, 0",
            "3, 1, 1",
            "4, 0, 1",
            "5, -1, 1",
            "6, -1, 0",
            "7, -1, -1",
            "8, 0, -1",
            "9, 1, -1",
            "10, 2, -1",
            "11, 2, 0",
            "12, 2, 1",
            "13, 2, 2",
            "14, 1, 2",
            "15, 0, 2",
            "16, -1, 2",
            "17, -2, 2",
            "18, -2, 1",
            "19, -2, 0",
            "20, -2, -1",
            "21, -2, -2",
            "22, -1, -2",
            "23, 0, -2")
    fun `position -- first few squares -- yield correct position`(squareNumber: Int, x: Int, y: Int) {
        val position = SpiralMemory().position(squareNumber)

        assertThat(position).isEqualTo(Pair(x, y))
    }

    @ParameterizedTest
    @CsvSource(
            "1, 0",
            "12, 3",
            "23, 2",
            "1024, 31")
    fun `distanceFromOrigin -- reference inputs -- yield reference output`(squareNumber: Int, expectedSteps: Int) {
        val steps = SpiralMemory().distanceFromOrigin(squareNumber)

        assertThat(steps).isEqualTo(expectedSteps)
    }

    @Test
    fun `stressTestValues -- first few values -- are as per reference`() {
        val expected = listOf(1, 1, 2, 4, 5, 10, 11, 23, 25, 26, 54, 57, 59, 122, 133, 142, 147,
                304, 330, 351, 362, 747, 806)

        val actual = SpiralMemory().stressTestValues().take(expected.size).toList()

        assertThat(actual).isEqualTo(expected)
    }

}