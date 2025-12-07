package advent.year2025.day6

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MathWorksheetTest {
  @Test
  fun `grandTotal -- reference example -- 4277556`() {
    val input = """
      123 328  51 64 
       45 64  387 23 
        6 98  215 314
      *   +   *   +  
    """.trimIndent()
    val worksheet = MathWorksheet.of(input)

    val total = worksheet.grandTotal()

    Assertions.assertThat(total).isEqualTo(4277556)
  }

  @Test
  fun `rotateLeft -- sample numbers -- give expected result`() {
    val input = listOf(
      "123 328  51 64 ",
      " 45 64  387 23 ",
      "  6 98  215 314"
    )

    val rotated = input.rotateLeft()

    Assertions.assertThat(rotated).containsExactly(
      "  4",
      "431",
      "623",
      "   ",
      "175",
      "581",
      " 32",
      "   ",
      "8  ",
      "248",
      "369",
      "   ",
      "356",
      "24 ",
      "1  "
    )
  }

  @Test
  fun `verticalTotal -- reference input -- 3263827`() {
    val input = """
        123 328  51 64 
         45 64  387 23 
          6 98  215 314
        *   +   *   +  
      """.trimIndent()
    val worksheet = MathWorksheet.of(input)

    val total = worksheet.verticalTotal()

    Assertions.assertThat(total).isEqualTo(3263827L)
  }
}