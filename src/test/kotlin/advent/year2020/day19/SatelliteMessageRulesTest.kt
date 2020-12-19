package advent.year2020.day19

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SatelliteMessageRulesTest {

  @Test
  fun `regex -- first rules -- expected regex`() {
    val rules = SatelliteMessageRules("""
      0: 1 2
      1: "a"
      2: 1 3 | 3 1
      3: "b"
    """.trimIndent())

    val regexString = rules.regexString(0)

    assertThat(regexString).isEqualTo("""a(?:ab|ba)""")
  }

  @Test
  fun `regex -- second rules -- expected regex`() {
    val rules = SatelliteMessageRules("""
      0: 4 1 5
      1: 2 3 | 3 2
      2: 4 4 | 5 5
      3: 4 5 | 5 4
      4: "a"
      5: "b"
    """.trimIndent())

    val regexString = rules.regexString(0)

    assertThat(regexString).isEqualTo("""a(?:(?:aa|bb)(?:ab|ba)|(?:ab|ba)(?:aa|bb))b""")
  }

  @ParameterizedTest(name = "regex test -- first rules, input {0} -- match is {1}")
  @CsvSource("aab, true",
          "aba, true",
          "abb, false",
          "bab, false")
  fun `regex test -- first rules -- matches as per examples`(input: String, expected: Boolean) {
    val rules = SatelliteMessageRules("""
      0: 1 2
      1: "a"
      2: 1 3 | 3 1
      3: "b"
    """.trimIndent())

    val matches = rules.regexString(0).toRegex().matches(input)

    assertThat(matches).isEqualTo(expected)
  }

  @ParameterizedTest(name = "regex test -- second rules, input {0} -- match is {1}")
  @CsvSource("ababbb, true",
          "abbbab, true",
          "bababa, false",
          "aaabbb, false",
          "aaaabbb, false")
  fun `regex test -- second rules -- matches as per examples`(input: String, expected: Boolean) {
    val rules = SatelliteMessageRules("""
      0: 4 1 5
      1: 2 3 | 3 2
      2: 4 4 | 5 5
      3: 4 5 | 5 4
      4: "a"
      5: "b"
    """.trimIndent())

    val matches = rules.regexString(0).toRegex().matches(input)

    assertThat(matches).isEqualTo(expected)
  }
}