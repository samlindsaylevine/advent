package advent.year2020.day22

import java.io.File
import java.util.*


class CombatCardGame(val playerOne: Deque<Int>,
                     val playerTwo: Deque<Int>) {

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
  fun play(): Int {
    while (playerOne.isNotEmpty() && playerTwo.isNotEmpty()) {
      val playerOneCard = playerOne.pop()
      val playerTwoCard = playerTwo.pop()

      if (playerOneCard > playerTwoCard) {
        playerOne.addLast(playerOneCard)
        playerOne.addLast(playerTwoCard)
      } else {
        playerTwo.addLast(playerTwoCard)
        playerTwo.addLast(playerOneCard)
      }
    }

    val winningPlayer = if (playerOne.isEmpty()) playerTwo else playerOne

    return winningPlayer.mapIndexed { i, num -> (winningPlayer.size - i) * num }
            .sum()
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day22/input.txt")
          .readText()
          .trim()

  val game = CombatCardGame.parse(input)
  println(game.play())
}