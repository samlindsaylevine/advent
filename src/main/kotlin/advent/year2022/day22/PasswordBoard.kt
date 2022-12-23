package advent.year2022.day22

import advent.utils.Direction
import advent.utils.Point
import advent.utils.PointRange
import advent.utils.next
import java.io.File
import kotlin.math.sqrt

/**
 * --- Day 22: Monkey Map ---
 * The monkeys take you on a surprisingly easy trail through the jungle. They're even going in roughly the right
 * direction according to your handheld device's Grove Positioning System.
 * As you walk, the monkeys explain that the grove is protected by a force field. To pass through the force field, you
 * have to enter a password; doing so involves tracing a specific path on a strangely-shaped board.
 * At least, you're pretty sure that's what you have to do; the elephants aren't exactly fluent in monkey.
 * The monkeys give you notes that they took when they last saw the password entered (your puzzle input).
 * For example:
 *         ...#
 *         .#..
 *         #...
 *         ....
 * ...#.......#
 * ........#...
 * ..#....#....
 * ..........#.
 *         ...#....
 *         .....#..
 *         .#......
 *         ......#.
 *
 * 10R5L5R10L4R5L5
 *
 * The first half of the monkeys' notes is a map of the board. It is comprised of a set of open tiles (on which you can
 * move, drawn .) and solid walls (tiles which you cannot enter, drawn #).
 * The second half is a description of the path you must follow. It consists of alternating numbers and letters:
 *
 * A number indicates the number of tiles to move in the direction you are facing. If you run into a wall, you stop
 * moving forward and continue with the next instruction.
 * A letter indicates whether to turn 90 degrees clockwise (R) or counterclockwise (L). Turning happens in-place; it
 * does not change your current tile.
 *
 * So, a path like 10R5 means "go forward 10 tiles, then turn clockwise 90 degrees, then go forward 5 tiles".
 * You begin the path in the leftmost open tile of the top row of tiles. Initially, you are facing to the right (from
 * the perspective of how the map is drawn).
 * If a movement instruction would take you off of the map, you wrap around to the other side of the board. In other
 * words, if your next tile is off of the board, you should instead look in the direction opposite of your current
 * facing as far as you can until you find the opposite edge of the board, then reappear there.
 * For example, if you are at A and facing to the right, the tile in front of you is marked B; if you are at C and
 * facing down, the tile in front of you is marked D:
 *         ...#
 *         .#..
 *         #...
 *         ....
 * ...#.D.....#
 * ........#...
 * B.#....#...A
 * .....C....#.
 *         ...#....
 *         .....#..
 *         .#......
 *         ......#.
 *
 * It is possible for the next tile (after wrapping around) to be a wall; this still counts as there being a wall in
 * front of you, and so movement stops before you actually wrap to the other side of the board.
 * By drawing the last facing you had with an arrow on each tile you visit, the full path taken by the above example
 * looks like this:
 *         >>v#
 *         .#v.
 *         #.v.
 *         ..v.
 * ...#...v..v#
 * >>>v...>#.>>
 * ..#v...#....
 * ...>>>>v..#.
 *         ...#....
 *         .....#..
 *         .#......
 *         ......#.
 *
 * To finish providing the password to this strange input device, you need to determine numbers for your final row,
 * column, and facing as your final position appears from the perspective of the original map. Rows start from 1 at the
 * top and count downward; columns start from 1 at the left and count rightward. (In the above example, row 1, column 1
 * refers to the empty space with no tile on it in the top-left corner.) Facing is 0 for right (>), 1 for down (v), 2
 * for left (<), and 3 for up (^). The final password is the sum of 1000 times the row, 4 times the column, and the
 * facing.
 * In the above example, the final row is 6, the final column is 8, and the final facing is 0. So, the final password
 * is 1000 * 6 + 4 * 8 + 0: 6032.
 * Follow the path given in the monkeys' notes. What is the final password?
 *
 * --- Part Two ---
 * As you reach the force field, you think you hear some Elves in the distance. Perhaps they've already arrived?
 * You approach the strange input device, but it isn't quite what the monkeys drew in their notes. Instead, you are met
 * with a large cube; each of its six faces is a square of 50x50 tiles.
 * To be fair, the monkeys' map does have six 50x50 regions on it. If you were to carefully fold the map, you should be
 * able to shape it into a cube!
 * In the example above, the six (smaller, 4x4) faces of the cube are:
 *         1111
 *         1111
 *         1111
 *         1111
 * 222233334444
 * 222233334444
 * 222233334444
 * 222233334444
 *         55556666
 *         55556666
 *         55556666
 *         55556666
 *
 * You still start in the same position and with the same facing as before, but the wrapping rules are different. Now,
 * if you would walk off the board, you instead proceed around the cube. From the perspective of the map, this can look
 * a little strange. In the above example, if you are at A and move to the right, you would arrive at B facing down; if
 * you are at C and move down, you would arrive at D facing up:
 *         ...#
 *         .#..
 *         #...
 *         ....
 * ...#.......#
 * ........#..A
 * ..#....#....
 * .D........#.
 *         ...#..B.
 *         .....#..
 *         .#......
 *         ..C...#.
 *
 * Walls still block your path, even if they are on a different face of the cube. If you are at E facing up, your
 * movement is blocked by the wall marked by the arrow:
 *         ...#
 *         .#..
 *      -->#...
 *         ....
 * ...#..E....#
 * ........#...
 * ..#....#....
 * ..........#.
 *         ...#....
 *         .....#..
 *         .#......
 *         ......#.
 *
 * Using the same method of drawing the last facing you had with an arrow on each tile you visit, the full path taken
 * by the above example now looks like this:
 *         >>v#
 *         .#v.
 *         #.v.
 *         ..v.
 * ...#..^...v#
 * .>>>>>^.#.>>
 * .^#....#....
 * .^........#.
 *         ...#..v.
 *         .....#v.
 *         .#v<<<<.
 *         ..v...#.
 *
 * The final password is still calculated from your final position and facing from the perspective of the map. In this
 * example, the final row is 5, the final column is 7, and the final facing is 3, so the final password is 1000 * 5 + 4
 * * 7 + 3 = 5031.
 * Fold the map into a cube, then follow the path given in the monkeys' notes. What is the final password?
 *
 */
class PasswordBoard private constructor(
  // Keys are valid open tiles; values are a map from direction to where you end up if you try to move that direction
  // from the open tile. (If there is a solid wall in that direction, the entry will just be the point itself.)
  private val neighborsByPoint: Map<Point, NeighborLookup>,
  private val moves: List<BoardMove>
) {
  constructor(input: String, cube: Boolean = false) : this(
    input.substringBefore("\n\n").toBoard(cube = cube),
    BoardMove.parse(input.substringAfter("\n\n"))
  )

  fun password(): Int {
    val startingPosition = neighborsByPoint.keys.upperLeft()

    val endState = moves.fold(BoardTraversal(startingPosition, Direction.E)) { traversal, move ->
      traversal.next(move)
    }

    return endState.password()
  }

  private fun BoardTraversal.next(move: BoardMove): BoardTraversal {
    return when (move) {
      // Note - in our coordinate system for this puzzle, down is actually +Y. So, we're going to reverse how we use
      // N and S - we'll handle that by just reversing the turn directions.
      TurnLeft -> this.copy(direction = this.direction.right())
      TurnRight -> this.copy(direction = this.direction.left())
      is GoForward -> this.next(move.amount) { it.advance() }
    }
  }

  private fun BoardTraversal.advance(): BoardTraversal {
    val lookup = neighborsByPoint[this.position] ?: throw IllegalStateException("Missing lookup for $this")
    return lookup[this.direction] ?: throw IllegalStateException("Missing direction $direction for $this")
  }

  @Suppress("UNUSED")
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

private fun Set<Point>.upperLeft(): Point = this.minWith(Comparator.comparing(Point::y).thenComparing(Point::x))

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

private fun String.toBoard(cube: Boolean = false): Map<Point, NeighborLookup> {
  val points = this.lines().flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, char -> if (char == ' ') null else Point(x + 1, y + 1) to char }
  }.toMap()

  val (openTiles, solidWalls) = points.entries.partition { it.value == '.' }
    .map { entry -> entry.map { it.key } }
    .map { it.toSet() }

  val wraparoundFunction: WraparoundFunction = if (cube) {
    Cube(openTiles, solidWalls)::wrap
  } else {
    { point, direction -> wraparound(point, direction, openTiles, solidWalls) }
  }

  return openTiles.associateWith { it.neighborLookup(openTiles, solidWalls, wraparoundFunction) }
}

private typealias WraparoundFunction = (Point, Direction) -> BoardTraversal

private fun Point.neighborLookup(
  openTiles: Set<Point>,
  solidWalls: Set<Point>,
  wraparoundFunction: WraparoundFunction
): NeighborLookup =
  Direction.values()
    .associateWith { this.neighborLookup(it, openTiles, solidWalls, wraparoundFunction) }

// Where you end up if you walk in that direction.
private fun Point.neighborLookup(
  direction: Direction,
  openTiles: Set<Point>,
  solidWalls: Set<Point>,
  wraparoundFunction: WraparoundFunction /* = (advent.utils.Point, advent.utils.Direction, kotlin.collections.Set<advent.utils.Point>, kotlin.collections.Set<advent.utils.Point>) -> advent.utils.Point */
): BoardTraversal {
  return when (val next = this + direction.toPoint()) {
    in openTiles -> BoardTraversal(position = next, direction = direction)
    in solidWalls -> BoardTraversal(position = this, direction = direction)
    else -> wraparoundFunction(next, direction)
  }
}

private fun wraparound(
  walkedOff: Point,
  direction: Direction,
  openTiles: Set<Point>,
  solidWalls: Set<Point>
): BoardTraversal {
  val allPoints = openTiles + solidWalls
  val oppositeDirection = direction.left().left().toPoint()
  val oppositeEnd = generateSequence(walkedOff) { it + oppositeDirection }
    .drop(1)
    .takeWhile { it in allPoints }
    .last()

  val nextPosition = if (oppositeEnd in openTiles) oppositeEnd else (walkedOff + oppositeDirection)
  return BoardTraversal(nextPosition, direction)
}

private class Cube(openTiles: Set<Point>, val solidWalls: Set<Point>) {

  init {
    (openTiles + solidWalls).let { allPoints ->
      val squareLength = sqrt(allPoints.size.toDouble() / 6).toInt()
      @Suppress("UNUSED_VARIABLE") val squares = findSquares(allPoints, squareLength)
      // Placeholder from when I was actually trying to do the geometry dynamically...
    }
  }

  // This is a silly hard-coded set of mappings that only works for my exact input.
  // I tried the general case for a while it but it was hard!
  private val mappings: List<CubeMapping> = listOf(
    CubeMapping(
      Point(50, 1)..Point(50, 50),
      Direction.W,
      Point(1, 150)..Point(1, 101),
      Direction.E
    ),
    CubeMapping(
      Point(51, 0)..Point(100, 0),
      Direction.S,
      Point(1, 151)..Point(1, 200),
      Direction.E
    ),
    CubeMapping(
      Point(101, 0)..Point(150, 0),
      Direction.S,
      Point(1, 200)..Point(50, 200),
      Direction.S
    ),
    CubeMapping(
      Point(151, 1)..Point(151, 50),
      Direction.E,
      Point(100, 150)..Point(100, 101),
      Direction.W
    ),
    CubeMapping(
      Point(150, 51)..Point(101, 51),
      Direction.N,
      Point(100, 100)..Point(100, 51),
      Direction.W
    ),
    CubeMapping(
      Point(101, 51)..Point(101, 100),
      Direction.E,
      Point(101, 50)..Point(150, 50),
      Direction.S
    ),
    CubeMapping(
      Point(101, 101)..Point(101, 150),
      Direction.E,
      Point(150, 50)..Point(150, 1),
      Direction.W
    ),
    CubeMapping(
      Point(51, 151)..Point(100, 151),
      Direction.N,
      Point(50, 151)..Point(50, 200),
      Direction.W
    ),
    CubeMapping(
      Point(51, 151)..Point(51, 200),
      Direction.E,
      Point(51, 150)..Point(100, 150),
      Direction.S
    ),
    CubeMapping(
      Point(1, 201)..Point(50, 201),
      Direction.N,
      Point(101, 1)..Point(150, 1),
      Direction.N
    ),
    CubeMapping(
      Point(0, 151)..Point(0, 200),
      Direction.W,
      Point(51, 1)..Point(100, 1),
      Direction.N
    ),
    CubeMapping(
      Point(0, 101)..Point(0, 150),
      Direction.W,
      Point(51, 50)..Point(51, 1),
      Direction.E
    ),
    CubeMapping(
      Point(1, 100)..Point(50, 100),
      Direction.S,
      Point(51, 51)..Point(51, 100),
      Direction.E
    ),
    CubeMapping(
      Point(50, 51)..Point(50, 100),
      Direction.W,
      Point(1, 101)..Point(50, 101),
      Direction.N
    )
  )

  fun wrap(point: Point, direction: Direction): BoardTraversal {
    val mapping = mappings.first { it.contains(point, direction) }
    val next = mapping.map(point)
    return if (next.position in solidWalls) BoardTraversal(point - direction.toPoint(), direction) else next
  }

  private data class CubeMapping(
    val source: PointRange,
    val sourceDirection: Direction,
    val target: PointRange,
    val direction: Direction
  ) {
    init {
      require(source.toList().size == target.toList().size) {
        "Mismatched ranges! $source $target"
      }
    }

    fun contains(point: Point, direction: Direction) = point in source && direction == sourceDirection
    fun map(point: Point): BoardTraversal {
      val index = source.indexOf(point)
      return BoardTraversal(position = target.elementAt(index), direction)
    }
  }


}

// Returns the upper left of each square.
private fun findSquares(
  pointsRemaining: Set<Point>,
  squareLength: Int,
  squaresSoFar: Set<Point> = emptySet()
): Set<Point> {
  if (pointsRemaining.isEmpty()) return squaresSoFar
  val next = pointsRemaining.upperLeft()
  val nextPoints = pointsRemaining
    .filter { (it.x - next.x) !in (0 until squareLength) || (it.y - next.y) !in (0 until squareLength) }
    .toSet()
  return findSquares(nextPoints, squareLength, squaresSoFar + next)
}

private fun <T, R> Pair<T, T>.map(function: (T) -> R) = function(this.first) to function(this.second)

private typealias NeighborLookup = Map<Direction, BoardTraversal>

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

  val cube = PasswordBoard(input, cube = true)
  println(cube.password())
}