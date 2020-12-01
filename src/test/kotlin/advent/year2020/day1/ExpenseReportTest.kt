package advent.year2020.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExpenseReportTest {

  @Test
  fun `product -- reference example -- reference output`() {
    val report = ExpenseReport(listOf(
            1721,
            979,
            366,
            299,
            675,
            1456
    ))

    val product = report.product()

    assertThat(product).isEqualTo(514579)
  }

  @Test
  fun `tripleProduct -- reference example -- reference output`() {
    val report = ExpenseReport(listOf(
            1721,
            979,
            366,
            299,
            675,
            1456
    ))

    val product = report.tripleProduct()

    assertThat(product).isEqualTo(241861950)
  }
}