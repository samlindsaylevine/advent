package advent.year2023.day9

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OasisReportTest {

  @Test
  fun `sum of next -- reference input -- 114`() {
    val input = """
      0 3 6 9 12 15
      1 3 6 10 15 21
      10 13 16 21 30 45
    """.trimIndent()

    val report = OasisReport(input)
    val result = report.sumOfNext()

    assertThat(result).isEqualTo(114)
  }

  @Test
  fun `sum of previous -- reference input -- 2`() {
    val input = """
      0 3 6 9 12 15
      1 3 6 10 15 21
      10 13 16 21 30 45
    """.trimIndent()

    val report = OasisReport(input)
    val result = report.sumOfPrevious()

    assertThat(result).isEqualTo(2)
  }
}