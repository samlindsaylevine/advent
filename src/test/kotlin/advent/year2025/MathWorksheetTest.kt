package advent.year2025

import advent.year2025.day6.MathWorksheet
import org.assertj.core.api.Assertions.assertThat
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

    assertThat(total).isEqualTo(4277556)
  }
}