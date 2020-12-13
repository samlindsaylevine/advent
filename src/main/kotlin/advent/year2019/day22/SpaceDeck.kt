package advent.year2019.day22

import advent.utils.multiplicativeInverse
import java.io.File

data class SpaceDeck(val cards: List<Int>) {

  constructor(numCards: Int = 10_007) : this((0 until numCards).toList())
}

sealed class ShuffleInstruction {
  abstract operator fun invoke(deck: SpaceDeck): SpaceDeck

  // If this instruction is executed on a deck, which original position ends up at position i?
  abstract fun originalPosition(i: Long, numCards: Long): Long
}

object DealIntoNewStack : ShuffleInstruction() {
  override operator fun invoke(deck: SpaceDeck) = SpaceDeck(deck.cards.reversed())

  override fun originalPosition(i: Long, numCards: Long) = numCards - 1 - i
}

class Cut(val n: Int) : ShuffleInstruction() {
  override operator fun invoke(deck: SpaceDeck) = when {
    n > 0 -> SpaceDeck(deck.cards.drop(n) + deck.cards.take(n))
    n < 0 -> SpaceDeck(deck.cards.takeLast(-n) + deck.cards.dropLast(-n))
    // n == 0
    else -> deck
  }

  override fun originalPosition(i: Long, numCards: Long) = when {
    n > 0 && (i < numCards - n) -> i + n
    n > 0 && (i >= numCards - n) -> i - numCards + n
    n < 0 && (i < -n) -> i + numCards + n
    n < 0 && (i >= -n) -> i + n
    // n == 0
    else -> i
  }
}

class DealWithIncrement(val n: Int) : ShuffleInstruction() {
  override operator fun invoke(deck: SpaceDeck): SpaceDeck {
    val newList = MutableList(deck.cards.size) { 0 }
    deck.cards.forEachIndexed { i, card -> newList[(i * n) % deck.cards.size] = card }
    return SpaceDeck(newList)
  }

  /**
   * Card x went to position (x * n) % numCards.
   *
   * So, we need to solve for x: (x * n) % numCards = i.
   * This is the multiplicative inverse mod numCards!
   */
  override fun originalPosition(i: Long, numCards: Long) = (i * multiplicativeInverse(n.toLong(), numCards)) % numCards

}

private fun String.toShuffleInstruction(): ShuffleInstruction = when {
  this == "deal into new stack" -> DealIntoNewStack
  this.startsWith("cut ") -> Cut(this.substringAfter("cut ").toInt())
  this.startsWith("deal with increment ") -> DealWithIncrement(this.substringAfter("deal with increment ").toInt())
  else -> throw IllegalArgumentException("Unrecognized instruction: $this")
}

fun List<String>.toInstructions() = this.map { it.toShuffleInstruction() }

fun List<ShuffleInstruction>.shuffle(startingDeck: SpaceDeck) = this.fold(startingDeck) { deck, instruction ->
  instruction(deck)
}

/**
 * If this list of instructions is executed on a deck, which original position ends up in position i?
 */
fun List<ShuffleInstruction>.originalPosition(i: Long, numCards: Long) = this.reversed()
        .fold(i) { position, instruction -> instruction.originalPosition(position, numCards) }

/**
 * Trying to see if we have a cycle in some small number of executions -- but we don't.
 */
fun List<ShuffleInstruction>.countToCycle(i: Long, numCards: Long) = this.countToCycle(originalPosition(i, numCards),
        i,
        numCards,
        1)

private tailrec fun List<ShuffleInstruction>.countToCycle(currentPos: Long,
                                                          targetPos: Long,
                                                          numCards: Long,
                                                          numExecutions: Long): Long = when (targetPos) {
  currentPos -> numExecutions
  else -> countToCycle(this.originalPosition(currentPos, numCards), targetPos, numCards, numExecutions + 1)
}

fun puzzleInput() = File("src/main/kotlin/advent/year2019/day22/input.txt")
        .readText()
        .trim()
        .lines()
        .toInstructions()

fun main() {
  val input = puzzleInput()

  val result = input.shuffle(SpaceDeck())

  println(result.cards.indexOf(2019))
}
