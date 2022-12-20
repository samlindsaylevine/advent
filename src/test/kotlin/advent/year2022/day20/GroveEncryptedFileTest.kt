package advent.year2022.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GroveEncryptedFileTest {

  private val input = """
    1
    2
    -3
    3
    -2
    0
    4
  """.trimIndent()

  @Test
  fun `mix -- reference input -- 1, 2, -3, 4, 0, 3, -2`() {
    val file = GroveEncryptedFile.parse(input)

    file.mix()

    assertThat(file.numbers()).containsExactly(1, 2, -3, 4, 0, 3, -2)
  }

  @Test
  fun `grove coordinates -- reference input -- 3`() {
    val file = GroveEncryptedFile.parse(input)

    file.mix()

    assertThat(file.groveCoordinates()).isEqualTo(3)
  }

  @Test
  fun `first node -- moves to the right -- first node is updated`() {
    val file = GroveEncryptedFile.from(2, 0, 0, 0)

    file.mix()

    assertThat(file.numbers()).containsExactly(0, 0, 2, 0)
  }

  @ParameterizedTest
  @CsvSource(
    delimiterString = ": ",
    value = [
      "0: 811589153, 1623178306, -2434767459, 2434767459, -1623178306, 0, 3246356612",
      "1: 0, -2434767459, 3246356612, -1623178306, 2434767459, 1623178306, 811589153",
      "2: 0, 2434767459, 1623178306, 3246356612, -2434767459, -1623178306, 811589153",
      "3: 0, 811589153, 2434767459, 3246356612, 1623178306, -1623178306, -2434767459",
      "4: 0, 1623178306, -2434767459, 811589153, 2434767459, 3246356612, -1623178306",
      "5: 0, 811589153, -1623178306, 1623178306, -2434767459, 3246356612, 2434767459",
      "6: 0, 811589153, -1623178306, 3246356612, -2434767459, 1623178306, 2434767459",
      "7: 0, -2434767459, 2434767459, 1623178306, -1623178306, 811589153, 3246356612",
      "8: 0, 1623178306, 3246356612, 811589153, -2434767459, 2434767459, -1623178306",
      "9: 0, 811589153, 1623178306, -2434767459, 3246356612, 2434767459, -1623178306",
      "10: 0, -2434767459, 1623178306, 3246356612, -1623178306, 2434767459, 811589153"
    ]
  )
  fun `mix -- reference input, decrypted, mixed multiple times -- has reference values`(
    times: Int,
    expectedString: String
  ) {
    val file = GroveEncryptedFile.parse(input)

    val decrypted = file.decrypted()
    decrypted.mix(times = times)

    val expected = expectedString.split(", ").map { it.toLong() }
    assertThat(decrypted.numbers()).isEqualTo(expected)
  }
}