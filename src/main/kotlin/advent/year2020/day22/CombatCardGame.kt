package advent.year2020.day22

import java.io.File
import java.util.*


class CombatCardGame(private val playerOneDeck: List<Int>,
                     private val playerTwoDeck: List<Int>) {

  companion object {
    fun parse(input: String): CombatCardGame {
      val sections = input.split("\n\n")
      return CombatCardGame(parseSection(sections[0]), parseSection(sections[1]))
    }

    private fun parseSection(section: String) = section.lines()
            .drop(1)
            .map { it.toInt() }
            .let(::LinkedList)
  }

  /**
   * Plays the game until one player's deck is empty and returns their score.
   */
  fun play(recursive: Boolean = false): GameResult {
    val seenStates = mutableSetOf<Pair<List<Int>, List<Int>>>()

    // Make mutable copies so we don't mess up the original.
    val playerOne = LinkedList(playerOneDeck)
    val playerTwo = LinkedList(playerTwoDeck)

    while (playerOne.isNotEmpty() && playerTwo.isNotEmpty()) {

      val currentState = playerOne.toList() to playerTwo.toList()
      if (seenStates.contains(currentState)) {
        return GameResult(winner = Winner.PLAYER_1, playerOne.score())
      }
      seenStates.add(currentState)

      val playerOneCard = playerOne.pop()
      val playerTwoCard = playerTwo.pop()

      val roundWinner = when {
        recursive && playerOne.size >= playerOneCard && playerTwo.size >= playerTwoCard -> {
          val subgame = CombatCardGame(LinkedList(playerOne.take(playerOneCard)),
                  LinkedList(playerTwo.take(playerTwoCard)))
          subgame.play(recursive = true).winner
        }
        playerOneCard > playerTwoCard -> Winner.PLAYER_1
        else -> Winner.PLAYER_2
      }

      when (roundWinner) {
        Winner.PLAYER_1 -> {
          playerOne.addLast(playerOneCard)
          playerOne.addLast(playerTwoCard)
        }
        Winner.PLAYER_2 -> {
          playerTwo.addLast(playerTwoCard)
          playerTwo.addLast(playerOneCard)
        }
      }
    }

    val winningDeck = if (playerOne.isEmpty()) playerTwo else playerOne

    return GameResult(if (playerOne.isEmpty()) Winner.PLAYER_2 else Winner.PLAYER_1,
            winningDeck.score())
  }

  private fun Deque<Int>.score() = this.mapIndexed { i, num -> (this.size - i) * num }
          .sum()
}

enum class Winner {
  PLAYER_1, PLAYER_2
}

data class GameResult(val winner: Winner, val score: Int)

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day22/input.txt")
          .readText()
          .trim()

  val game = CombatCardGame.parse(input)
  println(game.play())
  println(game.play(recursive = true))
}