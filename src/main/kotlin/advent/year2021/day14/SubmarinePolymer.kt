package advent.year2021.day14

import advent.utils.maxOrThrow
import advent.utils.minOrThrow
import advent.utils.next
import java.io.File

class SubmarinePolymer(
  val currentFormula: String,
  val insertionRules: Map<Pair<Char, Char>, Char>
) {
  constructor(input: String) : this(input.substringBefore("\n\n").trim(),
    input.substringAfter("\n\n").trim()
      .lines()
      .associate { line ->
        val (from, to) = line.split(" -> ")
        val (firstChar, secondChar) = from.toCharArray()
        (firstChar to secondChar) to to.first()
      })

  fun next(): SubmarinePolymer {
    val pairs = currentFormula.zipWithNext()
    // We'll take the first character from each pair, along with the inserted element; then finish off with the last
    // character of the string, which won't be included from any pair.
    val nextFormula = pairs.joinToString(separator = "", postfix = currentFormula.last().toString()) {
      "${it.first}${insertionRules[it]}"
    }

    return SubmarinePolymer(nextFormula, insertionRules)
  }

  fun next(steps: Int): SubmarinePolymer = next(steps, SubmarinePolymer::next)

  fun quantityDifference(): Long {
    val counts = currentFormula.toCharArray().toList()
      .groupingBy { it }
      .eachCount()
      .map { (_, count) -> count }

    return (counts.maxOrThrow() - counts.minOrThrow()).toLong()
  }
}

fun main() {
  val polymer = File("src/main/kotlin/advent/year2021/day14/input.txt")
    .readText()
    .let(::SubmarinePolymer)

  println(polymer.next(10).quantityDifference())
  println(polymer.next(40).quantityDifference())
}