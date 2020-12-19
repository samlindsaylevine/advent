package advent.year2020.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class HomeworkExpressionTest {

  @ParameterizedTest(name = "value -- {0} -- {1}")
  @CsvSource("1 + 2 * 3 + 4 * 5 + 6, 71",
          "1 + (2 * 3) + (4 * (5 + 6)), 51",
          "2 * 3 + (4 * 5), 26",
          "5 + (8 * 3 + 9 + 3 * 4 * 3), 437",
          "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)), 12240",
          "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2, 13632")
  fun `value -- reference inputs -- reference values`(input: String, expected: Long) {
    println("input: $input")

    val expression = HomeworkExpression(input)

    assertThat(expression.value).isEqualTo(expected)
  }

  @Test
  fun `reverse polish notation -- 3 4 5 * - -- -17`() {
    val result = listOf("3", "4", "5", "*", "-").evaluateReversePolishNotation()

    assertThat(result).isEqualTo(-17L)
  }

  @Test
  fun `reverse polish notation -- 3 4 - 5 * -- -5`() {
    val result = listOf("3", "4", "-", "5", "*").evaluateReversePolishNotation()

    assertThat(result).isEqualTo(-5L)
  }

  @Test
  fun `to reverse polish notation -- simple example -- gives expected result`() {
    val input = listOf("3", "-", "4", "*", "5")

    val result = input.toReversePolishNotation()

    assertThat(result).isEqualTo(listOf("3", "4", "-", "5", "*"))
  }
}