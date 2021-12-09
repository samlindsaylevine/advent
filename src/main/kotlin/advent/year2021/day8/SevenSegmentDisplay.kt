package advent.year2021.day8

import advent.utils.permutations
import java.io.File

enum class DisplayDigit(val segments: Set<Char>) {


  ZERO('a', 'b', 'c', 'e', 'f', 'g'),
  ONE('c', 'f'),
  TWO('a', 'c', 'd', 'e', 'g'),
  THREE('a', 'c', 'd', 'f', 'g'),
  FOUR('b', 'c', 'd', 'f'),
  FIVE('a', 'b', 'd', 'f', 'g'),
  SIX('a', 'b', 'd', 'e', 'f', 'g'),
  SEVEN('a', 'c', 'f'),
  EIGHT('a', 'b', 'c', 'd', 'e', 'f', 'g'),
  NINE('a', 'b', 'c', 'd', 'f', 'g');

  constructor (vararg segments: Char) : this(segments.toSet())

  companion object {
    val allSegments = values().map { it.segments }
      .reduce(Set<Char>::union)
      .sorted()
  }
}

class SignalPattern(val wires: Set<Char>) {
  constructor(input: String) : this(input.toSet())
}

class OutputValue(val digits: List<Set<Char>>) {
  constructor(input: String) : this(input.split(" ").map { it.toSet() })
}

class DisplayEntry(
  private val signalPatterns: List<SignalPattern>,
  private val outputValue: OutputValue
) {
  constructor(input: String) : this(
    input.substringBefore(" | ").split(" ").map(::SignalPattern),
    OutputValue(input.substringAfter(" | "))
  )

  fun uniqueLengthsInOutput(): Int {
    val uniqueLengths = DisplayDigit.values().groupingBy { it.segments.size }
      .eachCount()
      .filter { it.value == 1 }
      .map { it.key }

    return outputValue.digits.count { it.size in uniqueLengths }
  }

  /**
   * Returns a calculated mapping of the wire codes in this entry (in the patterns and outputs) to
   * the segment codes (as defined in [DisplayDigit]).
   *
   * We're going to flagrantly ignore the hint in part 1 and just brute force test all the permutations.
   */
  fun wiresToSegmentsMapping(): Map<Char, Char> = DisplayDigit.allSegments.permutations()
    .map { it.zip(DisplayDigit.allSegments).toMap() }
    .first(this::isConsistent)

  private fun isConsistent(mapping: Map<Char, Char>): Boolean {
    val allValues: List<Set<Char>> = signalPatterns.map { it.wires } + outputValue.digits
    return allValues.map { it.wiresToSegments(mapping) }
      .all { it.isDigit() }
  }

  private fun Set<Char>.wiresToSegments(mapping: Map<Char, Char>): Set<Char> =
    this.map { wire -> mapping[wire] ?: throw IllegalStateException("Missing wire $wire") }
      .toSet()

  private fun Set<Char>.isDigit() = DisplayDigit.values().any { it.segments == this }

  fun outputNumber(): Int {
    val mapping = wiresToSegmentsMapping()
    return outputValue.digits.map { it.map { wire -> mapping[wire] }.toSet() }
      .map { segments ->
        DisplayDigit.values().find { it.segments == segments }
          ?: throw IllegalStateException("No digit with segments $segments")
      }
      .map { it.ordinal }
      .joinToString("")
      .toInt()
  }
}

class DisplayEntries(val entries: List<DisplayEntry>) {
  constructor(input: String) : this(input.lines().map(::DisplayEntry))

  fun uniqueLengthsInOutput() = entries.sumOf { it.uniqueLengthsInOutput() }
  fun outputSum() = entries.sumOf { it.outputNumber() }
}

fun main() {
  val entries = File("src/main/kotlin/advent/year2021/day8/input.txt")
    .readText()
    .trim()
    .let(::DisplayEntries)

  println(entries.uniqueLengthsInOutput())
  println(entries.outputSum())
}