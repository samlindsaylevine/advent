package advent.year2023.day15

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class HolidayAsciiStringHelperTest {

  @ParameterizedTest(name = "hash -- {0} -- yields {1}")
  @CsvSource(
          "rn=1,30",
          "cm-,253",
          "qp=3,97",
          "cm=2,47",
          "qp-,14",
          "pc=4,180",
          "ot=9,9",
          "ab=5,197",
          "pc-,48",
          "pc=6,214",
          "ot=7,231"
  )
  fun `hash -- reference inputs -- have reference values`(input: String, expectedHash: Int) {
    val hash = input.hashed()

    assertThat(hash).isEqualTo(expectedHash)
  }

  @Test
  fun `focusing power -- reference input -- 145`() {
    val input = """rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"""
    val sequence = InitializationSequence(input)

    val power = sequence.result().focusingPower()

    assertThat(power).isEqualTo(145)
  }
}