package advent.year2021.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PowerDiagnosticTest {

  private val referenceInput = listOf(
    "00100",
    "11110",
    "10110",
    "10111",
    "10101",
    "01111",
    "00111",
    "11100",
    "10000",
    "11001",
    "00010",
    "01010"
  )

  @Test
  fun `gamma rate -- reference input -- 22`() {
    val diagnostic = PowerDiagnostic(referenceInput)

    assertThat(diagnostic.gammaRate).isEqualTo(22)
  }

  @Test
  fun `epsilon rate -- reference input -- 9`() {
    val diagnostic = PowerDiagnostic(referenceInput)

    assertThat(diagnostic.epsilonRate).isEqualTo(9)
  }

  @Test
  fun `power consumption -- reference input -- 198`() {
    val diagnostic = PowerDiagnostic(referenceInput)

    assertThat(diagnostic.powerConsumption).isEqualTo(198)
  }

  @Test
  fun `oxygen rating -- reference input -- 23`() {
    val diagnostic = PowerDiagnostic(referenceInput)

    assertThat(diagnostic.oxygenGeneratorRating).isEqualTo(23)
  }

  @Test
  fun `co2 rating  -- reference input -- 10`() {
    val diagnostic = PowerDiagnostic(referenceInput)

    assertThat(diagnostic.co2ScrubberRating).isEqualTo(10)
  }

  @Test
  fun `life support -- reference input -- 230`() {
    val diagnostic = PowerDiagnostic(referenceInput)

    assertThat(diagnostic.lifeSupportRating).isEqualTo(230)
  }
}