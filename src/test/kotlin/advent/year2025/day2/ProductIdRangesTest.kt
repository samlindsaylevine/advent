package advent.year2025.day2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ProductIdRangesTest {

  @ParameterizedTest(name = "isInvalidId -- {0} -- is invalid")
  @CsvSource(
    "55",
    "6464",
    "123123"
  )
  fun `isInvalidId -- reference values -- is invalid`(id: Long) {
    val isInvalid = id.isInvalidId()

    assertThat(isInvalid).isTrue
  }

  @ParameterizedTest
  @CsvSource(
    "55",
    "6464",
    "123123"
  )
  fun `isInvalidId -- {1} -- is invalid`(id: Long) {
    val isInvalid = id.isInvalidId()

    assertThat(isInvalid).isTrue
  }


  @Test
  fun `invalidIdSum -- reference input -- 1227775554`() {
    val input =
      "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124"
    val ranges = ProductIdRanges(input)

    val sum = ranges.invalidIdSum()

    assertThat(sum).isEqualTo(1227775554)
  }
}