package advent.year2022.day25

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SnafuNumberTest {

  @ParameterizedTest
  @CsvSource(
    "1, 1",
    "2, 2",
    "3, 1=",
    "4, 1-",
    "5, 10",
    "6, 11",
    "7, 12",
    "8, 2=",
    "9, 2-",
    "10, 20",
    "15, 1=0",
    "20, 1-0",
    "2022, 1=11-2",
    "12345, 1-0---0",
    "314159265, 1121-1110-1=0",
  )
  fun `parseSnafu -- reference inputs -- reference numbers`(decimal: Long, snafu: String) {
    val parsed = snafu.parseSnafu()

    assertThat(parsed).isEqualTo(decimal)
  }

  @ParameterizedTest
  @CsvSource(
    "1, 1",
    "2, 2",
    "3, 1=",
    "4, 1-",
    "5, 10",
    "6, 11",
    "7, 12",
    "8, 2=",
    "9, 2-",
    "10, 20",
    "15, 1=0",
    "20, 1-0",
    "2022, 1=11-2",
    "12345, 1-0---0",
    "314159265, 1121-1110-1=0",
  )
  fun `toSnafu -- reference numbers -- reference outputs`(decimal: Long, expected: String) {
    val snafu = decimal.toSnafu()

    assertThat(snafu).isEqualTo(expected)
  }
}