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

private class SingleGroupRule(val ruleNumbers: List<Int>) : SatelliteMessageRule() {
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
}