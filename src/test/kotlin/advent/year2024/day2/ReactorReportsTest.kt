package advent.year2024.day2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ReactorReportsTest {

    @ParameterizedTest(name = "isSafe -- {0} -- is safe")
    @CsvSource(
        "7 6 4 2 1",
        "1 3 6 7 9"
    )
    fun `safe examples`(input: String) {
        val report = ReactorReport(input)

        assertThat(report.isSafe()).isTrue()
    }

    @ParameterizedTest(name = "isSafe -- {0} -- is unsafe")
    @CsvSource(
        "1 2 7 8 9",
        "9 7 6 2 1",
        "1 3 2 4 5",
        "8 6 4 4 1"
    )
    fun `unsafe examples`(input: String) {
        val report = ReactorReport(input)

        assertThat(report.isSafe()).isFalse()
    }

    @Test
    fun `countSafe -- reference input -- 2`() {
        val input = """
            7 6 4 2 1
            1 2 7 8 9
            9 7 6 2 1
            1 3 2 4 5
            8 6 4 4 1
            1 3 6 7 9
        """.trimIndent()

        val reports = ReactorReports(input)
        val count = reports.countSafe()

        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `countSafe -- one bad level allowed -- 4`() {
        val input = """
            7 6 4 2 1
            1 2 7 8 9
            9 7 6 2 1
            1 3 2 4 5
            8 6 4 4 1
            1 3 6 7 9
        """.trimIndent()

        val reports = ReactorReports(input)
        val count = reports.countSafe(allowOneBadLevel = true)

        assertThat(count).isEqualTo(4)
    }
}