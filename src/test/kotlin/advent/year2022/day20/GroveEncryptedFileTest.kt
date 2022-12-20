package advent.year2022.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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

    assertThat(file.numbers()).isEqualTo(listOf(1, 2, -3, 4, 0, 3, -2))
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

    assertThat(file.numbers()).isEqualTo(listOf(0, 0, 2, 0))
  }
}