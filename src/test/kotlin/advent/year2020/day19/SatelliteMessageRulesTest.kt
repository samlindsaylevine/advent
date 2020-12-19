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

  @ParameterizedTest(name = "regex with recursion -- {0} -- match is {1}")
  @CsvSource(
          "abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa, false",
          "bbabbbbaabaabba, true",
          "babbbbaabbbbbabbbbbbaabaaabaaa, true",
          "aaabbbbbbaaaabaababaabababbabaaabbababababaaa, true",
          "bbbbbbbaaaabbbbaaabbabaaa, true",
          "bbbababbbbaaaaaaaabbababaaababaabab, true",
          "ababaaaaaabaaab, true",
          "ababaaaaabbbaba, true",
          "baabbaaaabbaaaababbaababb, true",
          "abbbbabbbbaaaababbbbbbaaaababb, true",
          "aaaaabbaabaaaaababaa, true",
          "aaaabbaaaabbaaa, false",
          "aaaabbaabbaaaaaaabbbabbbaaabbaabaaa, true",
          "babaaabbbaaabaababbaabababaaab, false",
          "aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba, true"
  )
  fun `regex with recursion -- reference inputs -- pass or fail as per example`(input: String, expected: Boolean) {
    val rules = SatelliteMessageRules("""
      42: 9 14 | 10 1
      9: 14 27 | 1 26
      10: 23 14 | 28 1
      1: "a"
      11: 42 31
      5: 1 14 | 15 1
      19: 14 1 | 14 14
      12: 24 14 | 19 1
      16: 15 1 | 14 14
      31: 14 17 | 1 13
      6: 14 14 | 1 14
      2: 1 24 | 14 4
      0: 8 11
      13: 14 3 | 1 12
      15: 1 | 14
      17: 14 2 | 1 7
      23: 25 1 | 22 14
      28: 16 1
      4: 1 1
      20: 14 14 | 1 15
      3: 5 14 | 16 1
      27: 1 6 | 14 18
      14: "b"
      21: 14 1 | 1 14
      25: 1 1 | 1 14
      22: 14 14
      8: 42
      26: 14 22 | 1 20
      18: 15 15
      7: 14 5 | 1 21
      24: 14 1
    """.trimIndent())

    val matches = rules.regexWithRecursion().matches(input)

    assertThat(matches).isEqualTo(expected)
  }
}