package advent.year2021.day14

import advent.utils.maxOrThrow
import advent.utils.merge
import advent.utils.minOrThrow
import advent.utils.next
import java.io.File

/**
 * --- Day 14: Extended Polymerization ---
 * The incredible pressures at this depth are starting to put a strain on your submarine. The submarine has
 * polymerization equipment that would produce suitable materials to reinforce the submarine, and the nearby
 * volcanically-active caves should even have the necessary input elements in sufficient quantities.
 * The submarine manual contains instructions for finding the optimal polymer formula; specifically, it offers a
 * polymer template and a list of pair insertion rules (your puzzle input). You just need to work out what polymer
 * would result after repeating the pair insertion process a few times.
 * For example:
 * NNCB
 *
 * CH -> B
 * HH -> N
 * CB -> H
 * NH -> C
 * HB -> C
 * HC -> B
 * HN -> C
 * NN -> C
 * BH -> H
 * NC -> B
 * NB -> B
 * BN -> B
 * BB -> N
 * BC -> B
 * CC -> N
 * CN -> C
 *
 * The first line is the polymer template - this is the starting point of the process.
 * The following section defines the pair insertion rules. A rule like AB -> C means that when elements A and B are
 * immediately adjacent, element C should be inserted between them. These insertions all happen simultaneously.
 * So, starting with the polymer template NNCB, the first step simultaneously considers all three pairs:
 *
 * The first pair (NN) matches the rule NN -> C, so element C is inserted between the first N and the second N.
 * The second pair (NC) matches the rule NC -> B, so element B is inserted between the N and the C.
 * The third pair (CB) matches the rule CB -> H, so element H is inserted between the C and the B.
 *
 * Note that these pairs overlap: the second element of one pair is the first element of the next pair. Also, because
 * all pairs are considered simultaneously, inserted elements are not considered to be part of a pair until the next
 * step.
 * After the first step of this process, the polymer becomes NCNBCHB.
 * Here are the results of a few steps using the above rules:
 * Template:     NNCB
 * After step 1: NCNBCHB
 * After step 2: NBCCNBBBCBHCB
 * After step 3: NBBBCNCCNBBNBNBBCHBHHBCHB
 * After step 4: NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB
 *
 * This polymer grows quickly. After step 5, it has length 97; After step 10, it has length 3073. After step 10, B
 * occurs 1749 times, C occurs 298 times, H occurs 161 times, and N occurs 865 times; taking the quantity of the most
 * common element (B, 1749) and subtracting the quantity of the least common element (H, 161) produces 1749 - 161 =
 * 1588.
 * Apply 10 steps of pair insertion to the polymer template and find the most and least common elements in the result.
 * What do you get if you take the quantity of the most common element and subtract the quantity of the least common
 * element?
 *
 * --- Part Two ---
 * The resulting polymer isn't nearly strong enough to reinforce the submarine. You'll need to run more steps of the
 * pair insertion process; a total of 40 steps should do it.
 * In the above example, the most common element is B (occurring 2192039569602 times) and the least common element is H
 * (occurring 3849876073 times); subtracting these produces 2188189693529.
 * Apply 40 steps of pair insertion to the polymer template and find the most and least common elements in the result.
 * What do you get if you take the quantity of the most common element and subtract the quantity of the least common
 * element?
 *
 */
class SubmarinePolymer(
  /**
   * In part one, we could keep the whole string; but in part two, there is no way (a >2188189693529 length string is
   * not going to fit in memory).
   *
   * Instead, we count all the pairs that appear in the string, as a zipWithNext() would return.
   */
  val currentFormula: Map<Pair<Char, Char>, Long>,
  private val insertionRules: Map<Pair<Char, Char>, Char>,
  private val lastCharacter: Char
) {
  constructor(input: String) : this(
    input.substringBefore("\n\n").trim().pairCount(),
    input.substringAfter("\n\n").trim()
      .lines()
      .associate { line ->
        val (from, to) = line.split(" -> ")
        val (firstChar, secondChar) = from.toCharArray()
        (firstChar to secondChar) to to.first()
      },
    input.substringBefore("\n\n").trim().last()
  )

  fun next(): SubmarinePolymer {
    val nextFormula = currentFormula.map { (pair, count) ->
      val (firstChar, secondChar) = pair
      val generatedChar = insertionRules[pair]
        ?: throw IllegalArgumentException("Unrecognized pair $pair")
      mapOf(
        (firstChar to generatedChar) to count,
        (generatedChar to secondChar) to count
      )
    }.reduce(Map<Pair<Char, Char>, Long>::merge)

    return SubmarinePolymer(nextFormula, insertionRules, lastCharacter)
  }

  fun next(steps: Int): SubmarinePolymer = next(steps, SubmarinePolymer::next)

  private fun characterCounts(): Map<Char, Long> {
    // We'll count just the first character of each pair, so that we don't double count.
    val firstCharacterCounts = currentFormula.map { (pair, count) -> pair.first to count }
      .groupingBy { (char, count) -> char }
      .fold(0L) { acc, pair -> acc + pair.second }

    // Since we only counted the first character, that means we haven't yet counted the last character of the string.
    // That character can never change!
    val lastCharacterCount = mapOf(lastCharacter to 1L)

    return firstCharacterCounts.merge(lastCharacterCount)
  }

  fun quantityDifference(): Long {
    val counts = characterCounts()
      .map { (_, count) -> count }

    return (counts.maxOrThrow() - counts.minOrThrow()).toLong()
  }
}

fun String.pairCount(): Map<Pair<Char, Char>, Long> = this.zipWithNext()
  .groupingBy { it }
  .eachCount()
  .mapValues { (_, count) -> count.toLong() }

fun main() {
  val polymer = File("src/main/kotlin/advent/year2021/day14/input.txt")
    .readText()
    .let(::SubmarinePolymer)

  println(polymer.next(10).quantityDifference())
  println(polymer.next(40).quantityDifference())
}