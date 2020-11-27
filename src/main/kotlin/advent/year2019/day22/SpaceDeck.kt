package advent.year2019.day22

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

/**
 * This, in general, requires some use of the Euclidean algorithm...
 * but here, we happen to know that the only numCards we use are _prime numbers_, i.e., 10007 and
 * 119315717514047.
 *
 * Since x^(phi(m)) = 1 mod m; primality gives us phi(m) = m - 1; so our multiplicative inverse is x^(m-2) mod m.
 */
fun multiplicativeInverse(x: Long, m: Long): Long {
  // We're cheating a little - the reference examples use 10 cards, which isn't prime, so we pre-calculate the phi
  // function just for that value.
  val phi = if (m == 10L) 4 else m - 1
  return expMod(x, phi - 1, m)
}

/**
 * Calculate x^n mod m.
 */
tailrec fun expMod(x: Long, n: Long, m: Long, current: Long = 1): Long = when {
  n == 0L -> current
  n % 2 == 0L -> expMod((x * x) % m, n / 2, m, current)
  else -> expMod(x, n - 1, m, (x * current) % m)
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
