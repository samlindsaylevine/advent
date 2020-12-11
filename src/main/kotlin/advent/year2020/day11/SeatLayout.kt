package advent.year2020.day11

import advent.utils.Point
import java.io.File

data class SeatLayout(
        /**
         *  Either occupied or not.
         */
        val seats: Set<Point>,
        /**
         * Only the occupied ones - i.e., subset of [seats].
         */
        val occupied: Set<Point>) {

  companion object {
    fun parse(input: String): SeatLayout {
      val pointsToChar = input.trim()
              .lines()
              .flatMapIndexed { y, line ->
                line.mapIndexed { x, character -> Point(x, y) to character }
              }

      val seats = pointsToChar.filter { it.second == 'L' || it.second == '#' }
              .map { it.first }
              .toSet()
      val occupied = pointsToChar.filter { it.second == '#' }
              .map { it.first }
              .toSet()

      return SeatLayout(seats, occupied)
    }
  }

  fun next(): SeatLayout {
    val empty = seats - occupied
    val nextOccupied = empty.filter { it.adjacentOccupiedCount() == 0 }.toSet() +
            occupied.filter { it.adjacentOccupiedCount() < 4 }.toSet()

    return SeatLayout(seats, nextOccupied)
  }

  private fun Point.adjacentOccupiedCount() = this.eightNeighbors.count(occupied::contains)

  fun stableState() = stableState(this)

  private tailrec fun stableState(current: SeatLayout): SeatLayout {
    val next = current.next()

    return if (next == current) next else stableState(next)
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day11/input.txt")
          .readText()

  val layout = SeatLayout.parse(input)

  println(layout.stableState().occupied.size)
}