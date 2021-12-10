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

  @ParameterizedTest
  @CsvSource(
    "[({(<(())[]>[[{[]{<()<>>, }}]])})]",
    "[(()[<>])]({[<{<<[]>>(, )}>]})",
    "(((({<>}<{<{<>}{[]{[]{}, }}>}>))))",
    "{<[[]]>}<{[{[{[]{()[[[], ]]}}]}]}>",
    "<{([{{}}[<[[[<>{}]]]>[]], ])}>"
  )
  fun `validate -- incomplete line examples -- expected missing closers found`(input: String, expectedClosers: String) {
    val validation = NavigationSubsystem.validate(input)
    val expectedChars = expectedClosers.toCharArray().toList()

    assertThat(validation).isInstanceOf(IncompleteLine::class.java)
    assertThat((validation as? IncompleteLine)?.missingClosers).isEqualTo(expectedChars)
  }

  @ParameterizedTest
  @CsvSource(
    "[({(<(())[]>[[{[]{<()<>>, 288957",
    "[(()[<>])]({[<{<<[]>>(, 5566",
    "(((({<>}<{<{<>}{[]{[]{}, 1480781",
    "{<[[]]>}<{[{[{[]{()[[[], 995444",
    "<{([{{}}[<[[[<>{}]]]>[]], 294"
  )
  fun `incomplete line score -- incomplete line examples -- have expected scores`(input: String, expectedScore: Long) {
    val validation = NavigationSubsystem.validate(input)

    assertThat(validation).isInstanceOf(IncompleteLine::class.java)
    assertThat((validation as? IncompleteLine)?.score).isEqualTo(expectedScore)
  }

  @Test
  fun `autocomplete score -- reference input -- 288957`() {
    val navigation = NavigationSubsystem(input)

    assertThat(navigation.autocompleteScore()).isEqualTo(288957)
  }
}