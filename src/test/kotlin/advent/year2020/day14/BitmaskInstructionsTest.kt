package advent.year2020.day14

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BitmaskInstructionsTest {

  @ParameterizedTest(name = "bitmask apply -- reference mask, input {0} -- output {1}")
  @CsvSource("11, 73",
          "101, 101",
          "0, 64")
  fun `bitmask apply -- reference mask -- reference values`(input: Long, output: Long) {
    val bitmask = Bitmask("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X")

    val result = bitmask.apply(input)

    assertThat(result).isEqualTo(output)
  }

  @Test
  fun `instructions execute -- reference instructions -- has sum 165`() {
    val instructions = BitmaskInstructions("""
      mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
      mem[8] = 11
      mem[7] = 101
      mem[8] = 0
    """.trimIndent())

    val result = instructions.execute().sum()

    assertThat(result).isEqualTo(165)
  }

  @Test
  fun `possibleValues -- reference bitmask -- 26, 27, 58, 59`() {
    val bitmask = Bitmask("000000000000000000000000000000X1101X")

    val result = bitmask.possibleValues()

    assertThat(result).containsExactlyInAnyOrder(26L, 27L, 58L, 59L)
  }

  @Test
  fun `possibleValues -- reference bitmask on 42 -- reference value`() {
    val bitmask = Bitmask("000000000000000000000000000000X1001X")

    val result = bitmask.applyQuantum(42)

    assertThat(result.mask).isEqualTo("000000000000000000000000000000X1101X")
  }

  @Test
  fun `instructions execute quantum -- reference example -- sum 208`() {
    val instructions = BitmaskInstructions("""
      mask = 000000000000000000000000000000X1001X
      mem[42] = 100
      mask = 00000000000000000000000000000000X0XX
      mem[26] = 1
    """.trimIndent())

    val result = instructions.executeQuantum().sum()

    assertThat(result).isEqualTo(208)
  }
}