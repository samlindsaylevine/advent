package advent.year2020.day11

import advent.utils.Point
import advent.utils.times
import java.io.File

/**
 * --- Day 11: Seating System ---
 * Your plane lands with plenty of time to spare. The final leg of your journey is a ferry that goes directly to the
 * tropical island where you can finally start your vacation. As you reach the waiting area to board the ferry, you
 * realize you're so early, nobody else has even arrived yet!
 * By modeling the process people use to choose (or abandon) their seat in the waiting area, you're pretty sure you can
 * predict the best place to sit. You make a quick map of the seat layout (your puzzle input).
 * The seat layout fits neatly on a grid. Each position is either floor (.), an empty seat (L), or an occupied seat
 * (#). For example, the initial seat layout might look like this:
 * L.LL.LL.LL
 * LLLLLLL.LL
 * L.L.L..L..
 * LLLL.LL.LL
 * L.LL.LL.LL
 * L.LLLLL.LL
 * ..L.L.....
 * LLLLLLLLLL
 * L.LLLLLL.L
 * L.LLLLL.LL
 * 
 * Now, you just need to model the people who will be arriving shortly. Fortunately, people are entirely predictable
 * and always follow a simple set of rules. All decisions are based on the number of occupied seats adjacent to a given
 * seat (one of the eight positions immediately up, down, left, right, or diagonal from the seat). The following rules
 * are applied to every seat simultaneously:
 * 
 * If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes occupied.
 * If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat becomes empty.
 * Otherwise, the seat's state does not change.
 * 
 * Floor (.) never changes; seats don't move, and nobody sits on the floor.
 * After one round of these rules, every seat in the example layout becomes occupied:
 * #.##.##.##
 * #######.##
 * #.#.#..#..
 * ####.##.##
 * #.##.##.##
 * #.#####.##
 * ..#.#.....
 * ##########
 * #.######.#
 * #.#####.##
 * 
 * After a second round, the seats with four or more occupied adjacent seats become empty again:
 * #.LL.L#.##
 * #LLLLLL.L#
 * L.L.L..L..
 * #LLL.LL.L#
 * #.LL.LL.LL
 * #.LLLL#.##
 * ..L.L.....
 * #LLLLLLLL#
 * #.LLLLLL.L
 * #.#LLLL.##
 * 
 * This process continues for three more rounds:
 * #.##.L#.##
 * #L###LL.L#
 * L.#.#..#..
 * #L##.##.L#
 * #.##.LL.LL
 * #.###L#.##
 * ..#.#.....
 * #L######L#
 * #.LL###L.L
 * #.#L###.##
 * 
 * #.#L.L#.##
 * #LLL#LL.L#
 * L.L.L..#..
 * #LLL.##.L#
 * #.LL.LL.LL
 * #.LL#L#.##
 * ..L.L.....
 * #L#LLLL#L#
 * #.LLLLLL.L
 * #.#L#L#.##
 * 
 * #.#L.L#.##
 * #LLL#LL.L#
 * L.#.L..#..
 * #L##.##.L#
 * #.#L.LL.LL
 * #.#L#L#.##
 * ..L.L.....
 * #L#L##L#L#
 * #.LLLLLL.L
 * #.#L#L#.##
 * 
 * At this point, something interesting happens: the chaos stabilizes and further applications of these rules cause no
 * seats to change state! Once people stop moving around, you count 37 occupied seats.
 * Simulate your seating area by applying the seating rules repeatedly until no seats change state. How many seats end
 * up occupied?
 * 
 * --- Part Two ---
 * As soon as people start to arrive, you realize your mistake. People don't just care about adjacent seats - they care
 * about the first seat they can see in each of those eight directions!
 * Now, instead of considering just the eight immediately adjacent seats, consider the first seat in each of those
 * eight directions. For example, the empty seat below would see eight occupied seats:
 * .......#.
 * ...#.....
 * .#.......
 * .........
 * ..#L....#
 * ....#....
 * .........
 * #........
 * ...#.....
 * 
 * The leftmost empty seat below would only see one empty seat, but cannot see any of the occupied ones:
 * .............
 * .L.L.#.#.#.#.
 * .............
 * 
 * The empty seat below would see no occupied seats:
 * .##.##.
 * #.#.#.#
 * ##...##
 * ...L...
 * ##...##
 * #.#.#.#
 * .##.##.
 * 
 * Also, people seem to be more tolerant than you expected: it now takes five or more visible occupied seats for an
 * occupied seat to become empty (rather than four or more from the previous rules). The other rules still apply: empty
 * seats that see no occupied seats become occupied, seats matching no rule don't change, and floor never changes.
 * Given the same starting layout as above, these new rules cause the seating area to shift around as follows:
 * L.LL.LL.LL
 * LLLLLLL.LL
 * L.L.L..L..
 * LLLL.LL.LL
 * L.LL.LL.LL
 * L.LLLLL.LL
 * ..L.L.....
 * LLLLLLLLLL
 * L.LLLLLL.L
 * L.LLLLL.LL
 * 
 * #.##.##.##
 * #######.##
 * #.#.#..#..
 * ####.##.##
 * #.##.##.##
 * #.#####.##
 * ..#.#.....
 * ##########
 * #.######.#
 * #.#####.##
 * 
 * #.LL.LL.L#
 * #LLLLLL.LL
 * L.L.L..L..
 * LLLL.LL.LL
 * L.LL.LL.LL
 * L.LLLLL.LL
 * ..L.L.....
 * LLLLLLLLL#
 * #.LLLLLL.L
 * #.LLLLL.L#
 * 
 * #.L#.##.L#
 * #L#####.LL
 * L.#.#..#..
 * ##L#.##.##
 * #.##.#L.##
 * #.#####.#L
 * ..#.#.....
 * LLL####LL#
 * #.L#####.L
 * #.L####.L#
 * 
 * #.L#.L#.L#
 * #LLLLLL.LL
 * L.L.L..#..
 * ##LL.LL.L#
 * L.LL.LL.L#
 * #.LLLLL.LL
 * ..L.L.....
 * LLLLLLLLL#
 * #.LLLLL#.L
 * #.L#LL#.L#
 * 
 * #.L#.L#.L#
 * #LLLLLL.LL
 * L.L.L..#..
 * ##L#.#L.L#
 * L.L#.#L.L#
 * #.L####.LL
 * ..#.#.....
 * LLL###LLL#
 * #.LLLLL#.L
 * #.L#LL#.L#
 * 
 * #.L#.L#.L#
 * #LLLLLL.LL
 * L.L.L..#..
 * ##L#.#L.L#
 * L.L#.LL.L#
 * #.LLLL#.LL
 * ..#.L.....
 * LLL###LLL#
 * #.LLLLL#.L
 * #.L#LL#.L#
 * 
 * Again, at this point, people stop shifting around and the seating area reaches equilibrium. Once this occurs, you
 * count 26 occupied seats.
 * Given the new visibility method and the rule change for occupied seats becoming empty, once equilibrium is reached,
 * how many seats end up occupied?
 * 
 */
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