package advent.year2020.day13

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ShuttleBusTest {
  @Test
  fun `earliest product -- reference example -- 295`() {
    val buses = ShuttleBuses("7,13,x,x,59,x,31,19")

    val result = buses.earliestProduct(939)

    assertThat(result).isEqualTo(295)
  }

  @ParameterizedTest(name = "earliest departure -- {0} -- {1}")
  @CsvSource("7,13,x,x,59,x,31,19 > 1068781",
          "17,x,13,19 > 3417",
          "67,7,59,61 > 754018",
          "67,x,7,59,61 > 779210",
          "67,7,x,59,61 > 1261476",
          "1789,37,47,1889 > 1202161486",
          delimiter = '>'
  )
  fun `earliest departure -- reference examples -- reference values`(input: String, expected: Long) {
    val departures = DepartureTimes(input)

    val result = departures.earliest()

    assertThat(result).isEqualTo(expected)
  }
}