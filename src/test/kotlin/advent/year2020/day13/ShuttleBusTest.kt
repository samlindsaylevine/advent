package advent.year2020.day13

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShuttleBusTest {
  @Test
  fun `earliest product -- reference example -- 295`() {
    val buses = ShuttleBuses("7,13,x,x,59,x,31,19")

    val result = buses.earliestProduct(939)

    assertThat(result).isEqualTo(295)
  }
}