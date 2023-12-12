package advent.year2023.day12

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SpringRecordsTest {

  @ParameterizedTest(name = "arrangement count -- {0} - {1} arrangements")
  @CsvSource(delimiterString = " - ", value = [
    "## 2 - 1",
    "???.### 1,1,3 - 1",
    ".??..??...?##. 1,1,3 - 4",
    "?#?#?#?#?#?#?#? 1,3,1,6 - 1",
    "????.#...#... 4,1,1 - 1",
    "????.######..#####. 1,6,5 - 4",
    "?###???????? 3,2,1 - 10"
  ])
  fun `arrangementCount -- sample inputs -- expected counts`(input: String, expected: Long) {
    val record = SpringRecord.of(input)

    val arrangements = record.arrangementCount()

    assertThat(arrangements).isEqualTo(expected)
  }

  @Test
  fun `sum -- sample input records -- 21`() {
    val input = """
      ???.### 1,1,3
      .??..??...?##. 1,1,3
      ?#?#?#?#?#?#?#? 1,3,1,6
      ????.#...#... 4,1,1
      ????.######..#####. 1,6,5
      ?###???????? 3,2,1
    """.trimIndent()

    val records = SpringRecords(input)
    val sum = records.sumOfCounts()

    assertThat(sum).isEqualTo(21)
  }

  @ParameterizedTest(name = "arrangement count -- {0} unfolded - {1} arrangements")
  @CsvSource(delimiterString = " - ", value = [
    "???.### 1,1,3 - 1",
    ".??..??...?##. 1,1,3 - 16384",
    "?#?#?#?#?#?#?#? 1,3,1,6 - 1",
    "????.#...#... 4,1,1 - 16",
    "????.######..#####. 1,6,5 - 2500",
    "?###???????? 3,2,1 - 506250"
  ])
  fun `arrangementCount -- unfolded -- expected counts`(input: String, expected: Long) {
    val record = SpringRecord.of(input).unfolded()

    val arrangements = record.arrangementCount()

    assertThat(arrangements).isEqualTo(expected)
  }
}