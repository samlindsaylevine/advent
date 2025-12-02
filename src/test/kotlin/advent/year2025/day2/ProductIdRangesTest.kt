package advent.year2025.day2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ProductIdRangesTest {

  @ParameterizedTest(name = "isSequenceRepeatedTwice -- {0} -- true")
  @CsvSource(
    "55",
    "6464",
    "123123"
  )
  fun `isSequenceRepeatedTwice -- reference values -- true`(id: String) {
    val isRepeated = id.isSequenceRepeatedTwice()

    assertThat(isRepeated).isTrue
  }

  @ParameterizedTest(name = "isSequenceRepeated-- {0} -- true")
  @CsvSource(
    "12341234",
    "123123123",
    "1212121212",
    "1111111"
  )
  fun `isSequenceRepeated -- reference values -- true`(id: String) {
    val isRepeated = id.isSequenceRepeated()

    assertThat(isRepeated).isTrue
  }

  @Test
  fun `isSequenceRepeated -- not a repeated string -- false`() {
    val input = "1234567890"
    val isRepeated = input.isSequenceRepeated()

    assertThat(isRepeated).isFalse
  }


  @Test
  fun `invalidIdSum -- reference input -- 1227775554`() {
    val input =
      "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124"
    val ranges = ProductIdRanges(input)

    val sum = ranges.invalidIdSum()

    assertThat(sum).isEqualTo(1227775554)
  }

  @Test
  fun `invalidIdSum -- reference input, any number of repeats -- 4174379265`() {
    val input =
      "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124"
    val ranges = ProductIdRanges(input)

    val sum = ranges.invalidIdSum(String::isSequenceRepeated)

    assertThat(sum).isEqualTo(4174379265)
  }
}