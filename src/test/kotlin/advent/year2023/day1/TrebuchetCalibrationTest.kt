package advent.year2023.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TrebuchetCalibrationTest {

    @Test
    fun `calibration sum -- reference input -- 142`() {
        val input = """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()

        val calibration = TrebuchetCalibration(input)
        val sum = calibration.calibrationSum()

        assertThat(sum).isEqualTo(142)
    }

    @Test
    fun `sum with spelled out digits -- reference input -- 281`() {
        val input = """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent()

        val calibration = TrebuchetCalibration(input)
        val sum = calibration.literateCalibrationSum()

        assertThat(sum).isEqualTo(281)
    }
}