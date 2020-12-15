package advent.year2020.day15

class ElvenMemoryGame(val startingNumbers: List<Int>) {

  fun spoken(): Sequence<Int> {
    val startingRemaining = startingNumbers.toMutableList()
    val numberToMostRecentSpeakIndex = mutableMapOf<Int, Int>()
    var index = 1
    var lastSpoken = 0

    return generateSequence {
      val starting = startingRemaining.removeFirstOrNull()

      val next: Int = if (starting != null) {
        starting
      } else {
        val previousIndex = numberToMostRecentSpeakIndex[lastSpoken]
        if (previousIndex == null) {
          0
        } else {
          index - previousIndex - 1
        }
      }

      numberToMostRecentSpeakIndex[lastSpoken] = index - 1
      index++
      lastSpoken = next
      next
    }
  }
}

fun main() {
  val game = ElvenMemoryGame(listOf(12, 20, 0, 6, 1, 17, 7))

  println(game.spoken().elementAt(2019))
  println(game.spoken().elementAt(30000000 - 1))
}