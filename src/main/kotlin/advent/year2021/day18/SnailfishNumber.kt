package advent.year2021.day18

import advent.utils.pairs
import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * --- Day 18: Snailfish ---
 * You descend into the ocean trench and encounter some snailfish. They say they saw the sleigh keys! They'll even tell
 * you which direction the keys went if you help one of the smaller snailfish with his math homework.
 * Snailfish numbers aren't like regular numbers. Instead, every snailfish number is a pair - an ordered list of two
 * elements. Each element of the pair can be either a regular number or another pair.
 * Pairs are written as [x,y], where x and y are the elements within the pair. Here are some example snailfish numbers,
 * one snailfish number per line:
 * [1,2]
 * [[1,2],3]
 * [9,[8,7]]
 * [[1,9],[8,5]]
 * [[[[1,2],[3,4]],[[5,6],[7,8]]],9]
 * [[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]
 * [[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]
 *
 * This snailfish homework is about addition. To add two snailfish numbers, form a pair from the left and right
 * parameters of the addition operator. For example, [1,2] + [[3,4],5] becomes [[1,2],[[3,4],5]].
 * There's only one problem: snailfish numbers must always be reduced, and the process of adding two snailfish numbers
 * can result in snailfish numbers that need to be reduced.
 * To reduce a snailfish number, you must repeatedly do the first action in this list that applies to the snailfish
 * number:
 *
 * If any pair is nested inside four pairs, the leftmost such pair explodes.
 * If any regular number is 10 or greater, the leftmost such regular number splits.
 *
 * Once no action in the above list applies, the snailfish number is reduced.
 * During reduction, at most one action applies, after which the process returns to the top of the list of actions. For
 * example, if split produces a pair that meets the explode criteria, that pair explodes before other splits occur.
 * To explode a pair, the pair's left value is added to the first regular number to the left of the exploding pair (if
 * any), and the pair's right value is added to the first regular number to the right of the exploding pair (if any).
 * Exploding pairs will always consist of two regular numbers. Then, the entire exploding pair is replaced with the
 * regular number 0.
 * Here are some examples of a single explode action:
 *
 * [[[[[9,8],1],2],3],4] becomes [[[[0,9],2],3],4] (the 9 has no regular number to its left, so it is not added to any
 * regular number).
 * [7,[6,[5,[4,[3,2]]]]] becomes [7,[6,[5,[7,0]]]] (the 2 has no regular number to its right, and so it is not added to
 * any regular number).
 * [[6,[5,[4,[3,2]]]],1] becomes [[6,[5,[7,0]]],3].
 * [[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]] becomes [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]] (the pair [3,2] is unaffected
 * because the pair [7,3] is further to the left; [3,2] would explode on the next action).
 * [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]] becomes [[3,[2,[8,0]]],[9,[5,[7,0]]]].
 *
 * To split a regular number, replace it with a pair; the left element of the pair should be the regular number divided
 * by two and rounded down, while the right element of the pair should be the regular number divided by two and rounded
 * up. For example, 10 becomes [5,5], 11 becomes [5,6], 12 becomes [6,6], and so on.
 * Here is the process of finding the reduced result of [[[[4,3],4],4],[7,[[8,4],9]]] + [1,1]:
 * after addition: [[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]
 * after explode:  [[[[0,7],4],[7,[[8,4],9]]],[1,1]]
 * after explode:  [[[[0,7],4],[15,[0,13]]],[1,1]]
 * after split:    [[[[0,7],4],[[7,8],[0,13]]],[1,1]]
 * after split:    [[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]
 * after explode:  [[[[0,7],4],[[7,8],[6,0]]],[8,1]]
 *
 * Once no reduce actions apply, the snailfish number that remains is the actual result of the addition operation:
 * [[[[0,7],4],[[7,8],[6,0]]],[8,1]].
 * The homework assignment involves adding up a list of snailfish numbers (your puzzle input). The snailfish numbers
 * are each listed on a separate line. Add the first snailfish number and the second, then add that result and the
 * third, then add that result and the fourth, and so on until all numbers in the list have been used once.
 * For example, the final sum of this list is [[[[1,1],[2,2]],[3,3]],[4,4]]:
 * [1,1]
 * [2,2]
 * [3,3]
 * [4,4]
 *
 * The final sum of this list is [[[[3,0],[5,3]],[4,4]],[5,5]]:
 * [1,1]
 * [2,2]
 * [3,3]
 * [4,4]
 * [5,5]
 *
 * The final sum of this list is [[[[5,0],[7,4]],[5,5]],[6,6]]:
 * [1,1]
 * [2,2]
 * [3,3]
 * [4,4]
 * [5,5]
 * [6,6]
 *
 * Here's a slightly larger example:
 * [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
 * [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
 * [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
 * [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
 * [7,[5,[[3,8],[1,4]]]]
 * [[2,[2,2]],[8,[8,1]]]
 * [2,9]
 * [1,[[[9,3],9],[[9,0],[0,7]]]]
 * [[[5,[7,4]],7],1]
 * [[[[4,2],2],6],[8,7]]
 *
 * The final sum [[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]] is found after adding up the above snailfish
 * numbers:
 *   [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
 * + [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
 * = [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]
 *
 *   [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]
 * + [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
 * = [[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]
 *
 *   [[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]
 * + [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
 * = [[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]
 *
 *   [[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]
 * + [7,[5,[[3,8],[1,4]]]]
 * = [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]
 *
 *   [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]
 * + [[2,[2,2]],[8,[8,1]]]
 * = [[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]
 *
 *   [[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]
 * + [2,9]
 * = [[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]
 *
 *   [[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]
 * + [1,[[[9,3],9],[[9,0],[0,7]]]]
 * = [[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]
 *
 *   [[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]
 * + [[[5,[7,4]],7],1]
 * = [[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]
 *
 *   [[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]
 * + [[[[4,2],2],6],[8,7]]
 * = [[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]
 *
 * To check whether it's the right answer, the snailfish teacher only checks the magnitude of the final sum. The
 * magnitude of a pair is 3 times the magnitude of its left element plus 2 times the magnitude of its right element.
 * The magnitude of a regular number is just that number.
 * For example, the magnitude of [9,1] is 3*9 + 2*1 = 29; the magnitude of [1,9] is 3*1 + 2*9 = 21. Magnitude
 * calculations are recursive: the magnitude of [[9,1],[1,9]] is 3*29 + 2*21 = 129.
 * Here are a few more magnitude examples:
 *
 * [[1,2],[[3,4],5]] becomes 143.
 * [[[[0,7],4],[[7,8],[6,0]]],[8,1]] becomes 1384.
 * [[[[1,1],[2,2]],[3,3]],[4,4]] becomes 445.
 * [[[[3,0],[5,3]],[4,4]],[5,5]] becomes 791.
 * [[[[5,0],[7,4]],[5,5]],[6,6]] becomes 1137.
 * [[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]] becomes 3488.
 *
 * So, given this example homework assignment:
 * [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
 * [[[5,[2,8]],4],[5,[[9,9],0]]]
 * [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
 * [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
 * [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
 * [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
 * [[[[5,4],[7,7]],8],[[8,3],8]]
 * [[9,3],[[9,9],[6,[4,9]]]]
 * [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
 * [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
 *
 * The final sum is:
 * [[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]
 * The magnitude of this final sum is 4140.
 * Add up all of the snailfish numbers from the homework assignment in the order they appear. What is the magnitude of
 * the final sum?
 *
 * --- Part Two ---
 * You notice a second question on the back of the homework assignment:
 * What is the largest magnitude you can get from adding only two of the snailfish numbers?
 * Note that snailfish addition is not commutative - that is, x + y and y + x can produce different results.
 * Again considering the last example homework assignment above:
 * [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
 * [[[5,[2,8]],4],[5,[[9,9],0]]]
 * [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
 * [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
 * [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
 * [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
 * [[[[5,4],[7,7]],8],[[8,3],8]]
 * [[9,3],[[9,9],[6,[4,9]]]]
 * [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
 * [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
 *
 * The largest magnitude of the sum of any two snailfish numbers in this list is 3993. This is the magnitude of
 * [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]] + [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]], which reduces to
 * [[[[7,8],[6,6]],[[6,0],[7,7]]],[[[7,8],[8,8]],[[7,9],[0,6]]]].
 * What is the largest magnitude of any sum of two different snailfish numbers from the homework assignment?
 *
 */
sealed class SnailfishNumber {
  companion object {

    fun parse(input: String) = parseWithRemaining(input).first

    private fun parseWithRemaining(input: String): Pair<SnailfishNumber, String> = if (input.startsWith("[")) {
      val rest = input.drop(1)
      val (left, commaAndRight) = parseWithRemaining(rest)
      require(commaAndRight.startsWith(",")) { "Expected comma in middle of pair; found $commaAndRight" }
      val rightString = commaAndRight.drop(1)
      val (right, bracketAndRest) = parseWithRemaining(rightString)
      require(bracketAndRest.startsWith("]")) { "Expected close bracket to finish pair; found $bracketAndRest" }
      SnailfishPair(left, right) to bracketAndRest.drop(1)
    } else {
      val literal = input.takeWhile { it.isDigit() }
      RegularNumber(literal.toInt()) to input.dropWhile { it.isDigit() }
    }
  }

  operator fun plus(other: SnailfishNumber) = SnailfishPair(this, other).reduced()

  fun reduced(): SnailfishNumber {
    val exploded = this.exploded()
    if (exploded != this) return exploded.reduced()
    val split = this.split()
    if (split != this) return split.reduced()
    return this
  }

  /**
   * Return a new instance of this number with the provided node replaced, wherever it is in the tree, with the new one.
   */
  abstract fun replaced(original: SnailfishNumber, new: SnailfishNumber): SnailfishNumber

  abstract fun magnitude(): Long

  private fun exploded(): SnailfishNumber {
    val explosion = findExplosion()
    return if (explosion == null) {
      this
    } else {
      val leftNumber = explosion.pair.first as RegularNumber
      val rightNumber = explosion.pair.second as RegularNumber
      this.replaced(explosion.pair, RegularNumber(0))
        .incremented(explosion.previousNumber, leftNumber.value)
        .incremented(explosion.nextNumber, rightNumber.value)
    }
  }

  /**
   * Returns a sequence of chains from the root of this number to each individual node. Every node in the tree will
   * have exactly one entry in the sequence where it is the final element in a list.
   */
  fun sequence(parents: List<SnailfishNumber> = emptyList()): Sequence<List<SnailfishNumber>> {
    val thisNode = this
    return sequence {
      val withThis = parents + thisNode
      yield(withThis)
      if (thisNode is SnailfishPair) {
        yieldAll(thisNode.first.sequence(withThis))
        yieldAll(thisNode.second.sequence(withThis))
      }
    }
  }

  private fun findExplosion(): ExplodingPair? {
    var previousNumber: RegularNumber? = null
    var pair: SnailfishPair? = null

    this.sequence().forEach {
      val current = it.last()
      val existingPair = pair
      when {
        existingPair == null && current is RegularNumber -> previousNumber = current
        existingPair == null && current is SnailfishPair && it.size >= 5 -> pair = current
        existingPair != null && current is RegularNumber && !it.contains(existingPair) ->
          return ExplodingPair(existingPair, previousNumber, current)
      }
    }

    return pair?.let { ExplodingPair(it, previousNumber, null) }
  }

  private data class ExplodingPair(
    val pair: SnailfishPair,
    val previousNumber: RegularNumber?,
    val nextNumber: RegularNumber?
  )

  /**
   * If numberToIncrement is non-null, replace it in our tree with a number incremented by amount. If it is null,
   * do nothing.
   */
  private fun incremented(numberToIncrement: RegularNumber?, amount: Int) = if (numberToIncrement == null) {
    this
  } else {
    this.replaced(numberToIncrement, RegularNumber(numberToIncrement.value + amount))
  }

  private fun split(): SnailfishNumber {
    val toSplit = this.sequence().map { it.last() }
      .filterIsInstance<RegularNumber>()
      .firstOrNull { it.value >= 10 }
    return if (toSplit == null) {
      this
    } else {
      val left = floor(toSplit.value / 2f).roundToInt().let(::RegularNumber)
      val right = ceil(toSplit.value / 2f).roundToInt().let(::RegularNumber)
      this.replaced(toSplit, SnailfishPair(left, right))
    }
  }
}

fun List<SnailfishNumber>.sum() = this.reduce(SnailfishNumber::plus)
fun List<SnailfishNumber>.largestPairwiseMagnitude() = this.pairs()
  .filter { !(it.first === it.second) }
  .map { (x, y) -> x + y }
  .map { it.magnitude() }
  .maxOrNull()

class RegularNumber(val value: Int) : SnailfishNumber() {
  override fun toString() = value.toString()
  override fun replaced(original: SnailfishNumber, new: SnailfishNumber) = if (this === original) new else this
  override fun magnitude() = value.toLong()
}

class SnailfishPair(val first: SnailfishNumber, val second: SnailfishNumber) : SnailfishNumber() {
  override fun toString() = "[$first,$second]"
  override fun replaced(original: SnailfishNumber, new: SnailfishNumber) = when {
    first === original -> SnailfishPair(new, second)
    second === original -> SnailfishPair(first, new)
    else -> SnailfishPair(first.replaced(original, new), second.replaced(original, new))
  }

  override fun magnitude() = 3 * first.magnitude() + 2 * second.magnitude()
}

fun main() {
  val homework = File("src/main/kotlin/advent/year2021/day18/input.txt")
    .readLines()
    .filter { it.isNotBlank() }
    .map(SnailfishNumber::parse)

  println(homework.sum().magnitude())
  println(homework.largestPairwiseMagnitude())
}