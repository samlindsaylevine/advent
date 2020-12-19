package advent.year2020.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class HomeworkExpressionTest {

  @ParameterizedTest(name = "valueWithoutPrecedence -- {0} -- {1}")
  @CsvSource("1 + 2 * 3 + 4 * 5 + 6, 71",
          "1 + (2 * 3) + (4 * (5 + 6)), 51",
          "2 * 3 + (4 * 5), 26",
          "5 + (8 * 3 + 9 + 3 * 4 * 3), 437",
          "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)), 12240",
          "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2, 13632")
  fun `valueWithoutPrecedence -- reference inputs -- reference values`(input: String, expected: Long) {
    val expression = HomeworkExpression(input)

    assertThat(expression.valueWithoutPrecedence).isEqualTo(expected)
  }

  @ParameterizedTest(name = "valueWithPrecedence -- {0} -- {1}")
  @CsvSource("1 + 2 * 3 + 4 * 5 + 6, 231",
          "1 + (2 * 3) + (4 * (5 + 6)), 51",
          "2 * 3 + (4 * 5), 46",
          "5 + (8 * 3 + 9 + 3 * 4 * 3), 1445",
          "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)), 669060",
          "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2, 23340")
  fun `valueWithPrecedence -- reference inputs -- reference values`(input: String, expected: Long) {
    val expression = HomeworkExpression(input)

    assertThat(expression.valueWithPrecedence).isEqualTo(expected)
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