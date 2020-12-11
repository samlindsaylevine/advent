package advent.year2020.day7

import advent.utils.loadingCache
import java.io.File

class BagRules(val rules: List<BagRule>) {
  constructor(input: String) : this(input.trim().lines().map(BagRule::parse))

  // Cache the contained-inside counts so we don't have to recalculate for any particular type of bag.
  private val containedInsideCache = loadingCache(::calculateContainedInside)

  fun validOutmost(inner: String): Set<String> = validOutmost(setOf(inner), emptySet())

  private tailrec fun validOutmost(inner: Set<String>, alreadyValid: Set<String>): Set<String> {
    if (inner.isEmpty()) return alreadyValid

    val nextCandidates = inner.flatMap { bag ->
      rules.filter { it.inner.containsKey(bag) }.map { it.outer }
    }.filter { it !in alreadyValid }
            .toSet()

    return validOutmost(nextCandidates, alreadyValid + nextCandidates)
  }

  fun containedInside(outermost: String): Int = containedInsideCache.get(outermost)

  private fun calculateContainedInside(outermost: String) =
          rules.first { it.outer == outermost }
                  .inner
                  .entries
                  // 1 for the bag itself, plus everything inside that one
                  .sumOf { it.value * (1 + containedInside(it.key)) }
}

data class BagRule(val outer: String, val inner: Map<String, Int>) {
  companion object {
    private val REGEX = "(.+) bags contain (.+).".toRegex()
    private val INNER_REGEX = "(\\d+) (.+) bags?".toRegex()

    fun parse(input: String): BagRule {
      val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable rule $input")

      val outer = match.groupValues[1]
      val inner = when (val inside = match.groupValues[2]) {
        "no other bags" -> emptyMap()
        else -> inside.split(", ").map(::parseInner).toMap()
      }
      return BagRule(outer, inner)
    }

    private fun parseInner(inner: String): Pair<String, Int> {
      val match = INNER_REGEX.matchEntire(inner) ?: throw IllegalArgumentException("Unparseable inner $inner")
      return match.groupValues[2] to match.groupValues[1].toInt()
    }
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day7/input.txt")
          .readText()

  val rules = BagRules(input)

  println(rules.validOutmost("shiny gold").size)
  println(rules.containedInside("shiny gold"))
}