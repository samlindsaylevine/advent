package advent.year2019.day22

import java.io.File

data class SpaceDeck(val cards: List<Int>) {

  constructor(numCards: Int = 10_007) : this((0 until numCards).toList())

  fun dealIntoNewStack() = SpaceDeck(this.cards.reversed())

  fun cut(n: Int) = when {
    n > 0 -> SpaceDeck(cards.drop(n) + cards.take(n))
    n < 0 -> SpaceDeck(cards.takeLast(-n) + cards.dropLast(-n))
    // n == 0
    else -> this
  }

  fun dealWithIncrement(n: Int): SpaceDeck {
    val newList = MutableList(cards.size) { 0 }
    cards.forEachIndexed { i, card -> newList[(i * n) % cards.size] = card }
    return SpaceDeck(newList)
  }
}

private fun String.toShuffleInstruction(): (SpaceDeck) -> SpaceDeck = when {
  this == "deal into new stack" -> SpaceDeck::dealIntoNewStack
  this.startsWith("cut ") -> { deck: SpaceDeck ->
    deck.cut(this.substringAfter("cut ").toInt())
  }
  this.startsWith("deal with increment ") -> { deck: SpaceDeck ->
    deck.dealWithIncrement(this.substringAfter("deal with increment ").toInt())
  }
  else -> throw IllegalArgumentException("Unrecognized instruction: $this")
}

fun List<String>.shuffle(startingDeck: SpaceDeck) = this.fold(startingDeck) { deck, instruction ->
  instruction.toShuffleInstruction()(deck)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2019/day22/input.txt")
          .readText()
          .trim()
          .lines()

  val result = input.shuffle(SpaceDeck())

  println(result.cards.indexOf(2019))
}
