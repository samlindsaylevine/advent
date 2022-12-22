package advent.year2022.day22

import advent.utils.Direction
import advent.utils.Point
import advent.utils.next
import java.io.File

class PasswordBoard(
  // Keys are valid open tiles; values are a map from direction to where you end up if you try to move that direction
  // from the open tile. (If there is a solid wall in that direction, the entry will just be the point itself.)
  private val neighborsByPoint: Map<Point, NeighborLookup>,
  private val moves: List<BoardMove>
) {
  constructor(input: String) : this(
    input.substringBefore("\n\n").toBoard(),
    BoardMove.parse(input.substringAfter("\n\n"))
  )

  fun password(): Int {
    val startingPosition = neighborsByPoint.keys
      .minWith(Comparator.comparing(Point::y).thenComparing(Point::x))

    val endState = moves.fold(BoardTraversal(startingPosition, Direction.E)) { traversal, move ->
//      println(draw(traversal))
//      println(move)
//      println()
      traversal.next(move)
    }

    return endState.password()
  }

  private fun BoardTraversal.next(move: BoardMove) = when (move) {
    // Note - in our coordinate system for this puzzle, down is actually +Y. So, we're going to reverse how we use
    // N and S - we'll handle that by just reversing the turn directions.
    TurnLeft -> this.copy(direction = this.direction.right())
    TurnRight -> this.copy(direction = this.direction.left())
    is GoForward -> this.copy(position = position.next(move.amount) { it.advance(this.direction) })
  }

  private fun Point.advance(direction: Direction): Point {
    val lookup = neighborsByPoint[this] ?: throw IllegalStateException("Missing lookup for $this")
    return lookup[direction] ?: throw IllegalStateException("Missing direction $direction for $this")
  }

  private fun draw(traversal: BoardTraversal): String =
    (neighborsByPoint.keys.minOf { it.y }..neighborsByPoint.keys.maxOf { it.y }).joinToString("\n") { y ->
      (neighborsByPoint.keys.minOf { it.x }..neighborsByPoint.keys.maxOf { it.x }).joinToString("") { x ->
        when (Point(x, y)) {
          traversal.position -> when (traversal.direction) {
            Direction.W -> "<"
            Direction.E -> ">"
            Direction.N -> "V"
            Direction.S -> "^"
          }
          in neighborsByPoint.keys -> "█"
          else -> " "
        }
      }
    }
}

private data class BoardTraversal(val position: Point, val direction: Direction) {
  fun facing() = when (this.direction) {
    Direction.E -> 0
    Direction.W -> 2
    // Remember, we're reversing N and S to match the coordinate system (where +Y is down).
    Direction.S -> 3
    Direction.N -> 1
  }

  fun password() = 1000 * position.y + 4 * position.x + facing()
}

private fun String.toBoard(): Map<Point, NeighborLookup> {
  val points = this.lines().flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, char -> if (char == ' ') null else Point(x + 1, y + 1) to char }
  }.toMap()

  val (openTiles, solidWalls) = points.entries.partition { it.value == '.' }
    .map { entry -> entry.map { it.key } }
    .map { it.toSet() }

  return openTiles.associateWith { it.neighborLookup(openTiles, solidWalls) }
}

private fun Point.neighborLookup(openTiles: Set<Point>, solidWalls: Set<Point>): NeighborLookup =
  Direction.values()
    .associateWith { this.neighborLookup(it, openTiles, solidWalls) }

// Where you end up if you walk in that direction.
private fun Point.neighborLookup(direction: Direction, openTiles: Set<Point>, solidWalls: Set<Point>): Point =
  when (val next = this + direction.toPoint()) {
    in openTiles -> next
    in solidWalls -> this
    else -> wraparound(next, direction, openTiles, solidWalls)
  }

private fun wraparound(walkedOff: Point, direction: Direction, openTiles: Set<Point>, solidWalls: Set<Point>): Point {
  val allPoints = openTiles + solidWalls
  val oppositeDirection = direction.left().left().toPoint()
  val oppositeEnd = generateSequence(walkedOff) { it + oppositeDirection }
    .drop(1)
    .takeWhile { it in allPoints }
    .last()

  return if (oppositeEnd in openTiles) oppositeEnd else (walkedOff + oppositeDirection)
}

private fun <T, R> Pair<T, T>.map(function: (T) -> R) = function(this.first) to function(this.second)

typealias NeighborLookup = Map<Direction, Point>

sealed class BoardMove {
  companion object {
    fun parse(input: String): List<BoardMove> = parse(input, emptyList())

    private tailrec fun parse(input: String, moves: List<BoardMove>): List<BoardMove> = when {
      input.isEmpty() -> moves
      input.first().isDigit() -> {
        val number = input.takeWhile { it.isDigit() }.toInt()
        parse(input.dropWhile { it.isDigit() }, moves + GoForward(number))
      }
      input.first() == 'L' -> parse(input.drop(1), moves + TurnLeft)
      input.first() == 'R' -> parse(input.drop(1), moves + TurnRight)
      else -> throw IllegalArgumentException("Unparseable character ${input.first()} in input")
    }
  }
}

object TurnLeft : BoardMove()
object TurnRight : BoardMove()
data class GoForward(val amount: Int) : BoardMove()

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day22/input.txt").readText()
    .trimEnd()

  val board = PasswordBoard(input)
  println(board.password())
}