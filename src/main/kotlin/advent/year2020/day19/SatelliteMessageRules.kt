package advent.year2020.day19

import advent.year2020.day14.substringBetween
import java.io.File

class SatelliteMessageRules(input: String) {

  private val rules: Map<Int, SatelliteMessageRule> =
          input.lines().map(SatelliteMessageRule::parse).toMap()

  private val regexCache = mutableMapOf<Int, String>()

  fun regexString(ruleNumber: Int): String = regexCache.getOrPut(ruleNumber) {
    val rule = rules[ruleNumber] ?: throw IllegalArgumentException("No rule $ruleNumber")
    rule.regex(this)
  }

  fun regexWithRecursion(): Regex {
    // This is tuned to match our input only (and also the example). It won't work for the general case.
    requireRule(0, SingleGroupRule(listOf(8, 11)))
    requireRule(8, SingleGroupRule(listOf(42)))
    requireRule(11, SingleGroupRule(listOf(42, 31)))

    val fortyTwo = regexString(42)

    // This one is actually expressible as a regex - it's "forty-two+"; one or more copies of rule forty-two.
    val eightRegex = "$fortyTwo+"

    // This one is not expressible as a regex.
    //
    // It's "42 31" or "42 42 31 31" or ... "42^n 31^n".
    //
    // a^n b^n cannot be matched by a regular expression because it can't be
    // captured in a finite state automaton - you have to count an unbounded number of a's.
    // So, we're going to cheat a bit - we're going to just include up to 4 copies!
    //
    // This is, again, good enough for our specific input... if it wasn't, I would include more finite copies :B

    val thirtyOne = regexString(31)

    val elevenRegex = "(?:$fortyTwo$thirtyOne|" +
            "$fortyTwo$fortyTwo$thirtyOne$thirtyOne|" +
            "$fortyTwo$fortyTwo$fortyTwo$thirtyOne$thirtyOne$thirtyOne|" +
            "$fortyTwo$fortyTwo$fortyTwo$fortyTwo$thirtyOne$thirtyOne$thirtyOne$thirtyOne)"

    return (eightRegex + elevenRegex).toRegex()
  }

  private fun requireRule(number: Int, expectedRule: SatelliteMessageRule) {
    if (rules[number] != expectedRule) {
      throw IllegalStateException("Expected rule $number to be $expectedRule but was ${rules[number]}")
    }
  }
}

private sealed class SatelliteMessageRule {
  companion object {
    fun parse(line: String): Pair<Int, SatelliteMessageRule> {
      val number = line.substringBefore(": ").toInt()
      val ruleInput = line.substringAfter(": ")

      val rule = when {
        ruleInput.contains("\"") -> LiteralRule(ruleInput.substringBetween("\"", "\""))
        ruleInput.contains(" | ") -> MultiGroupRule(ruleInput.split(" | ").map { it.parseAsGroup() })
        else -> ruleInput.parseAsGroup()
      }

      return number to rule
    }

    private fun String.parseAsGroup() = SingleGroupRule(this.split(" ").map { it.toInt() })
  }

  abstract fun regex(rules: SatelliteMessageRules): String
}

private class LiteralRule(val literal: String) : SatelliteMessageRule() {
  override fun regex(rules: SatelliteMessageRules) = literal
}

private data class SingleGroupRule(val ruleNumbers: List<Int>) : SatelliteMessageRule() {
  override fun regex(rules: SatelliteMessageRules) = ruleNumbers.joinToString(separator = "") { rules.regexString(it) }
}

private class MultiGroupRule(val groups: List<SingleGroupRule>) : SatelliteMessageRule() {
  override fun regex(rules: SatelliteMessageRules) = groups.joinToString(prefix = "(?:",
          separator = "|",
          postfix = ")") { it.regex(rules) }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day19/input.txt")
          .readText()
          .trim()

  val rules = SatelliteMessageRules(input.substringBefore("\n\n"))

  val messages = input.substringAfter("\n\n").lines()

  val ruleZero = rules.regexString(0).toRegex()

  println(messages.count { ruleZero.matches(it) })
  println(messages.count { rules.regexWithRecursion().matches(it) })
}