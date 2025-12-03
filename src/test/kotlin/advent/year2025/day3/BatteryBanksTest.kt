package advent.year2025.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BatteryBanksTest {

  @ParameterizedTest
  @CsvSource(
    "987654321111111, 98",
    "811111111111119, 89",
    "234234234234278, 78",
    "818181911112111, 92"
  )
  fun `largest joltage -- reference examples, 2 digits -- reference values`(input: String, expected: Long) {
    val batteryBank = BatteryBank(input)

    val joltage = batteryBank.largestJoltage(2)

    assertThat(joltage).isEqualTo(expected)
  }

  @ParameterizedTest
  @CsvSource(
    "987654321111111, 987654321111",
    "811111111111119, 811111111119",
    "234234234234278, 434234234278",
    "818181911112111, 888911112111"
  )
  fun `largest joltage -- reference examples, 12 digits -- reference values`(input: String, expected: Long) {
    val batteryBank = BatteryBank(input)

    val joltage = batteryBank.largestJoltage(12)

    assertThat(joltage).isEqualTo(expected)
  }

  @Test
  fun `total output joltage -- reference example -- 357`() {
    val input = """
      987654321111111
      811111111111119
      234234234234278
      818181911112111
    """.trimIndent()
    val banks = BatteryBanks(input)

    val joltage = banks.totalOutputJoltage()

    assertThat(joltage).isEqualTo(357L)
  }

  @Test
  fun `total output joltage -- reference example, 12 digits -- 3121910778619`() {
    val input = """
      987654321111111
      811111111111119
      234234234234278
      818181911112111
    """.trimIndent()
    val banks = BatteryBanks(input)

    val joltage = banks.totalOutputJoltage(numDigits = 12)

    assertThat(joltage).isEqualTo(3121910778619L)
  }
}