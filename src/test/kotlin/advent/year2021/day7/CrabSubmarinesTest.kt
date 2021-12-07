package advent.year2021.day7

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CrabSubmarinesTest {

  val input = "16,1,2,0,4,2,7,1,2,14"

  @Test
  fun `optimal position -- reference input, simple costs -- 2`() {
    val submarines = CrabSubmarines(input)

    assertThat(submarines.optimalAlignment(CrabFuelCost.SIMPLE)).isEqualTo(2)
  }

  @ParameterizedTest
  @CsvSource(
    "1, 41",
    "2, 37",
    "3, 39",
    "10, 71"
  )
  fun `fuel cost -- positions in examples, simple fuel costs -- have listed costs`(position: Int, expectedCost: Int) {
    val submarines = CrabSubmarines(input)

    assertThat(submarines.fuelSpentToAlign(position, CrabFuelCost.SIMPLE)).isEqualTo(expectedCost)
  }

  @Test
  fun `optimal position -- reference input, quadratic costs -- 5`() {
    val submarines = CrabSubmarines(input)

    assertThat(submarines.optimalAlignment(CrabFuelCost.QUADRATIC)).isEqualTo(5)
  }

  @ParameterizedTest
  @CsvSource(
    "5, 168",
    "2, 206"
  )
  fun `fuel cost -- positions in examples, quadratic fuel costs -- have listed costs`(
    position: Int,
    expectedCost: Int
  ) {
    val submarines = CrabSubmarines(input)

    assertThat(submarines.fuelSpentToAlign(position, CrabFuelCost.QUADRATIC)).isEqualTo(expectedCost)
  }
}