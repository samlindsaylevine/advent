package advent.year2021.day10

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class NavigationSubsystemTest {

  val input = """
    [({(<(())[]>[[{[]{<()<>>
    [(()[<>])]({[<{<<[]>>(
    {([(<{}[<>[]}>{[]{[(<()>
    (((({<>}<{<{<>}{[]{[]{}
    [[<[([]))<([[{}[[()]]]
    [{[{({}]{}}([{[{{{}}([]
    {<[[]]>}<{[{[{[]{()[[[]
    [<(<(<(<{}))><([]([]()
    <{([([[(<>()){}]>(<<{{
    <{([{{}}[<[[[<>{}]]]>[]]
  """.trimIndent().trim().lines()

  @ParameterizedTest
  @CsvSource(
    "{([(<{}[<>[]}>{[]{[(<()>, }",
    "[[<[([]))<([[{}[[()]]], )",
    "[{[{({}]{}}([{[{{{}}([], ]",
    "[<(<(<(<{}))><([]([](), )",
    "<{([([[(<>()){}]>(<<{{, >"
  )
  fun `validate -- corrupted line examples -- first invalid char is found`(input: String, expectedChar: Char) {
    val validation = NavigationSubsystem.validate(input)

    assertThat(validation).isInstanceOf(CorruptedLine::class.java)
    assertThat((validation as? CorruptedLine)?.firstIllegalCharacter).isEqualTo(expectedChar)
  }

  @Test
  fun `corruptedSyntaxErrorScore -- reference input -- reference value`() {
    val navigation = NavigationSubsystem(input)

    assertThat(navigation.corruptedSyntaxErrorScore()).isEqualTo(26397)
  }
}