package advent.year2022.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ElfCommunicationSignalTest {

  @ParameterizedTest
  @CsvSource(
    "mjqjpqmgbljsphdztnvjfqwrcgsmlb, 7",
    "bvwbjplbgvbhsrlpgdmjqwftvncz, 5",
    "nppdvjthqldpwncqszvftbrmjlhg, 6",
    "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg, 10",
    "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw, 11"
  )
  fun `first marker -- reference inputs -- reference values`(input: String, expectedFirstMarker: Int) {
    val signal = ElfCommunicationSignal(input)

    val marker = signal.firstMarker()

    assertThat(marker).isEqualTo(expectedFirstMarker)
  }

  @ParameterizedTest
  @CsvSource(
    "mjqjpqmgbljsphdztnvjfqwrcgsmlb, 19",
    "bvwbjplbgvbhsrlpgdmjqwftvncz, 23",
    "nppdvjthqldpwncqszvftbrmjlhg, 23",
    "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg, 29",
    "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw, 26"
  )
  fun `first marker -- reference inputs, 14 chars -- reference values`(input: String, expectedFirstMarker: Int) {
    val signal = ElfCommunicationSignal(input)

    val marker = signal.firstMarker(14)

    assertThat(marker).isEqualTo(expectedFirstMarker)
  }
}