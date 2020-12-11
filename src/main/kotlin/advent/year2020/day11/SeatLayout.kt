package advent.year2020.day11

import advent.utils.Point
import advent.utils.times
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

  private val xRange = 0..seats.maxOf { it.x }
  private val yRange = 0..seats.maxOf { it.y }
  private val empty = seats - occupied

  fun next(): SeatLayout = nextBy(::adjacentOccupiedCount, 4)
  fun nextBySightLines(): SeatLayout = nextBy(::visibleOccupiedCount, 5)

  private fun nextBy(countNeighbors: (Point) -> Int, tooFull: Int): SeatLayout {
    val nextOccupied = empty.filter { countNeighbors(it) == 0 }.toSet() +
            occupied.filter { countNeighbors(it) < tooFull }.toSet()

    return SeatLayout(seats, nextOccupied)
  }

  private fun adjacentOccupiedCount(point: Point) = point.eightNeighbors.count(occupied::contains)

  fun visibleOccupiedCount(point: Point) = point.visibleSeats().count(occupied::contains)

  private fun Point.visibleSeats() = eightDirections()
          .mapNotNull { this.firstVisibleSeat(it) }

  private fun eightDirections() = (-1..1).flatMap { x ->
    (-1..1).map { y -> Point(x, y) }
  }.minus(Point(0, 0))

  private fun path(origin: Point, direction: Point) = generateSequence(1) { it + 1 }
          .map { origin + it * direction }

  private fun Point.firstVisibleSeat(direction: Point): Point? = path(this, direction)
          .takeWhile { it.x in xRange && it.y in yRange }
          .firstOrNull { seats.contains(it) }

  fun stableState() = stableState(this, SeatLayout::next)
  fun stableBySightLines() = stableState(this, SeatLayout::nextBySightLines)

  private tailrec fun stableState(current: SeatLayout, transform: (SeatLayout) -> SeatLayout): SeatLayout {
    val next = transform(current)

    return if (next == current) next else stableState(next, transform)
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day11/input.txt")
          .readText()

  val layout = SeatLayout.parse(input)

  println(layout.stableState().occupied.size)
  // This takes a couple seconds - we could speed it up by not recalculating sightlines for every state, and only once
  // for the whole layout... but CPU time is cheap and developer time is expensive :)
  println(layout.stableBySightLines().occupied.size)
}