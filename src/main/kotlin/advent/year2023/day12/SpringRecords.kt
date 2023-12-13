package advent.year2023.day12

import advent.utils.loadingCache
import java.io.File

/**
 * --- Day 12: Hot Springs ---
 * You finally reach the hot springs! You can see steam rising from secluded areas attached to the primary, ornate
 * building.
 * As you turn to enter, the researcher stops you. "Wait - I thought you were looking for the hot springs, weren't
 * you?" You indicate that this definitely looks like hot springs to you.
 * "Oh, sorry, common mistake! This is actually the onsen! The hot springs are next door."
 * You look in the direction the researcher is pointing and suddenly notice the massive metal helixes towering
 * overhead. "This way!"
 * It only takes you a few more steps to reach the main gate of the massive fenced-off area containing the springs. You
 * go through the gate and into a small administrative building.
 * "Hello! What brings you to the hot springs today? Sorry they're not very hot right now; we're having a lava shortage
 * at the moment." You ask about the missing machine parts for Desert Island.
 * "Oh, all of Gear Island is currently offline! Nothing is being manufactured at the moment, not until we get more
 * lava to heat our forges. And our springs. The springs aren't very springy unless they're hot!"
 * "Say, could you go up and see why the lava stopped flowing? The springs are too cold for normal operation, but we
 * should be able to find one springy enough to launch you up there!"
 * There's just one problem - many of the springs have fallen into disrepair, so they're not actually sure which
 * springs would even be safe to use! Worse yet, their condition records of which springs are damaged (your puzzle
 * input) are also damaged! You'll need to help them repair the damaged records.
 * In the giant field just outside, the springs are arranged into rows. For each row, the condition records show every
 * spring and whether it is operational (.) or damaged (#). This is the part of the condition records that is itself
 * damaged; for some springs, it is simply unknown (?) whether the spring is operational or damaged.
 * However, the engineer that produced the condition records also duplicated some of this information in a different
 * format! After the list of springs for a given row, the size of each contiguous group of damaged springs is listed in
 * the order those groups appear in the row. This list always accounts for every damaged spring, and each number is the
 * entire size of its contiguous group (that is, groups are always separated by at least one operational spring: ####
 * would always be 4, never 2,2).
 * So, condition records with no unknown spring conditions might look like this:
 * #.#.### 1,1,3
 * .#...#....###. 1,1,3
 * .#.###.#.###### 1,3,1,6
 * ####.#...#... 4,1,1
 * #....######..#####. 1,6,5
 * .###.##....# 3,2,1
 *
 * However, the condition records are partially damaged; some of the springs' conditions are actually unknown (?). For
 * example:
 * ???.### 1,1,3
 * .??..??...?##. 1,1,3
 * ?#?#?#?#?#?#?#? 1,3,1,6
 * ????.#...#... 4,1,1
 * ????.######..#####. 1,6,5
 * ?###???????? 3,2,1
 *
 * Equipped with this information, it is your job to figure out how many different arrangements of operational and
 * broken springs fit the given criteria in each row.
 * In the first line (???.### 1,1,3), there is exactly one way separate groups of one, one, and three broken springs
 * (in that order) can appear in that row: the first three unknown springs must be broken, then operational, then
 * broken (#.#), making the whole row #.#.###.
 * The second line is more interesting: .??..??...?##. 1,1,3 could be a total of four different arrangements. The last
 * ? must always be broken (to satisfy the final contiguous group of three broken springs), and each ?? must hide
 * exactly one of the two broken springs. (Neither ?? could be both broken springs or they would form a single
 * contiguous group of two; if that were true, the numbers afterward would have been 2,3 instead.) Since each ?? can
 * either be #. or .#, there are four possible arrangements of springs.
 * The last line is actually consistent with ten different arrangements! Because the first number is 3, the first and
 * second ? must both be . (if either were #, the first number would have to be 4 or higher). However, the remaining
 * run of unknown spring conditions have many different ways they could hold groups of two and one broken springs:
 * ?###???????? 3,2,1
 * .###.##.#...
 * .###.##..#..
 * .###.##...#.
 * .###.##....#
 * .###..##.#..
 * .###..##..#.
 * .###..##...#
 * .###...##.#.
 * .###...##..#
 * .###....##.#
 *
 * In this example, the number of possible arrangements for each row is:
 *
 * ???.### 1,1,3 - 1 arrangement
 * .??..??...?##. 1,1,3 - 4 arrangements
 * ?#?#?#?#?#?#?#? 1,3,1,6 - 1 arrangement
 * ????.#...#... 4,1,1 - 1 arrangement
 * ????.######..#####. 1,6,5 - 4 arrangements
 * ?###???????? 3,2,1 - 10 arrangements
 *
 * Adding all of the possible arrangement counts together produces a total of 21 arrangements.
 * For each row, count all of the different arrangements of operational and broken springs that meet the given
 * criteria. What is the sum of those counts?
 *
 * --- Part Two ---
 * As you look out at the field of springs, you feel like there are way more springs than the condition records list.
 * When you examine the records, you discover that they were actually folded up this whole time!
 * To unfold the records, on each row, replace the list of spring conditions with five copies of itself (separated by
 * ?) and replace the list of contiguous groups of damaged springs with five copies of itself (separated by ,).
 * So, this row:
 * .# 1
 * Would become:
 * .#?.#?.#?.#?.# 1,1,1,1,1
 * The first line of the above example would become:
 * ???.###????.###????.###????.###????.### 1,1,3,1,1,3,1,1,3,1,1,3,1,1,3
 * In the above example, after unfolding, the number of possible arrangements for some rows is now much larger:
 *
 * ???.### 1,1,3 - 1 arrangement
 * .??..??...?##. 1,1,3 - 16384 arrangements
 * ?#?#?#?#?#?#?#? 1,3,1,6 - 1 arrangement
 * ????.#...#... 4,1,1 - 16 arrangements
 * ????.######..#####. 1,6,5 - 2500 arrangements
 * ?###???????? 3,2,1 - 506250 arrangements
 *
 * After unfolding, adding all of the possible arrangement counts together produces 525152.
 * Unfold your condition records; what is the new sum of possible arrangement counts?
 *
 */
class SpringRecords(private val records: List<SpringRecord>) {
  constructor(input: String) : this(input.trim().lines().map(SpringRecord::of))

  fun sumOfCounts() = records.sumOf { it.arrangementCount() }
  fun unfolded() = SpringRecords(records.map { it.unfolded() })
}

data class SpringRecord(val conditions: List<SpringCondition>, val groupSizes: List<Int>) {
  companion object {
    fun of(input: String): SpringRecord {
      val (conditionString, sizesString) = input.split(" ")
      return SpringRecord(conditionString.map { c -> SpringCondition.values().first { c == it.display } },
              sizesString.split(",").map { it.toInt() })
    }
  }

  private fun <T> List<T>.repeat(times: Int) = (1..times).flatMap { this }
  private fun <T> List<T>.repeat(times: Int, separator: T) = (1..times).flatMap { this + listOf(separator) }.dropLast(1)

  fun unfolded() = SpringRecord(conditions.repeat(times = 5, separator = SpringCondition.UNKNOWN), groupSizes.repeat(5))

  fun arrangementCount(): Long {
    // Had this in here for some manual debugging to see the calculated values during part 1.
    // cache.asMap().entries.forEach { println("${it.key} : ${it.value}") }
    return arrangementCount(ArrangementLookup(this, false))
  }

  private fun dropCondition() = SpringRecord(conditions.drop(1), groupSizes)
  private fun dropGroup() = SpringRecord(conditions, groupSizes.drop(1))
  private fun decrementFirstGroup() = SpringRecord(conditions, listOf(groupSizes.first() - 1) + groupSizes.drop(1))
  private fun replaceFirst(condition: SpringCondition) = SpringRecord(listOf(condition) + conditions.drop(1), groupSizes)

  private data class ArrangementLookup(val record: SpringRecord, val inGroup: Boolean)

  private val cache = loadingCache(::arrangementCount)

  /**
   * We're going to advance through the conditions and recursively calculate the counts. We're using a cache of
   * already calculated values, originally because I had some correctness problems in part 1 and wanted to see the
   * sub-calculations to find and fix a bug. This was super helpful because it meant that part 2 ran basically just
   * the same way!
   *
   * While we are contemplating a record, we also consider whether we are "in a group", i.e., whether the previous
   * spring was damaged. If the current state is impossible, the count must be 0; if the current state has completed
   * successfully the count is 1. If the leading value is unknown we sum together the counts for if it is damaged
   * vs. operational.
   */
  private fun arrangementCount(lookup: ArrangementLookup): Long {
    val (record, inGroup) = lookup
    val (conditions, groupSizes) = record

    if (conditions.isEmpty()) {
      // If we made it all the way through the conditions, this is legal as long as there are no more
      // required damaged springs.
      return if (groupSizes.isEmpty() || groupSizes.size == 1 && groupSizes.first() == 0) 1 else 0
    }

    // Shortcut.
    if (conditions.size < groupSizes.sum()) return 0

    return when (conditions.first()) {
      SpringCondition.OPERATIONAL -> when {
        // We successfully completed a group! We can drop that group and this spring and move on.
        inGroup && groupSizes.first() == 0 -> cache[ArrangementLookup(record.dropCondition().dropGroup(), false)]
        // Not currently in a group, can just skip this spring.
        !inGroup -> cache[ArrangementLookup(record.dropCondition(), false)]
        // We were in a group, and completed it... but it was the wrong size. Impossible, thus 0 valid choices.
        else -> 0
      }

      SpringCondition.DAMAGED -> when {
        // There aren't supposed to be any springs left! Impossible, 0 valid choices.
        groupSizes.isEmpty() -> 0
        // We're in a group, but we have already found all the damaged springs and this is one too many. 0 valid choices.
        inGroup && groupSizes.first() == 0 -> 0
        // We're either already in, or now starting, a group - decrease the size of the group (the first in the list)
        // and continue.
        else -> cache[ArrangementLookup(record.dropCondition().decrementFirstGroup(), true)]
      }

      // Sum the valid options from both possibilities.
      SpringCondition.UNKNOWN -> cache[ArrangementLookup(record.replaceFirst(SpringCondition.OPERATIONAL), inGroup)] +
              cache[ArrangementLookup(record.replaceFirst(SpringCondition.DAMAGED), inGroup)]
    }
  }
}

enum class SpringCondition(val display: Char) {
  OPERATIONAL('.'),
  DAMAGED('#'),
  UNKNOWN('?')
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day12/input.txt").readText().trim()
  val records = SpringRecords(input)

  println(records.sumOfCounts())
  println(records.unfolded().sumOfCounts())
}