package advent.year2024.day7

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CalibrationEquationsTest {

    @Test
    fun `totalCalibrationResult -- reference input -- 3749`() {
        val input = """
            190: 10 19
            3267: 81 40 27
            83: 17 5
            156: 15 6
            7290: 6 8 6 15
            161011: 16 10 13
            192: 17 8 14
            21037: 9 7 18 13
            292: 11 6 16 20
        """.trimIndent()
        val equations = CalibrationEquations(input)

        val result = equations.totalCalibrationResult()

        assertThat(result).isEqualTo(3749)
    }

    @Test
    fun `totalCalibrationResult -- reference input, allow concatenation -- 11387`() {
        val input = """
            190: 10 19
            3267: 81 40 27
            83: 17 5
            156: 15 6
            7290: 6 8 6 15
            161011: 16 10 13
            192: 17 8 14
            21037: 9 7 18 13
            292: 11 6 16 20
        """.trimIndent()
        val equations = CalibrationEquations(input)

        val result = equations.totalCalibrationResult(allowConcatenation = true)

        assertThat(result).isEqualTo(11387)
    }
}