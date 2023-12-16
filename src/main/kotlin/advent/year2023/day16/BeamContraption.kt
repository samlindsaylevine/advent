package advent.year2023.day16

import advent.utils.Direction
import advent.utils.Point
import java.io.File

/**
 * --- Day 16: The Floor Will Be Lava ---
 * With the beam of light completely focused somewhere, the reindeer leads you deeper still into the Lava Production
 * Facility. At some point, you realize that the steel facility walls have been replaced with cave, and the doorways
 * are just cave, and the floor is cave, and you're pretty sure this is actually just a giant cave.
 * Finally, as you approach what must be the heart of the mountain, you see a bright light in a cavern up ahead. There,
 * you discover that the beam of light you so carefully focused is emerging from the cavern wall closest to the
 * facility and pouring all of its energy into a contraption on the opposite side.
 * Upon closer inspection, the contraption appears to be a flat, two-dimensional square grid containing empty space
 * (.), mirrors (/ and \), and splitters (| and -).
 * The contraption is aligned so that most of the beam bounces around the grid, but each tile on the grid converts some
 * of the beam's light into heat to melt the rock in the cavern.
 * You note the layout of the contraption (your puzzle input). For example:
 * .|...\....
 * |.-.\.....
 * .....|-...
 * ........|.
 * ..........
 * .........\
 * ..../.\\..
 * .-.-/..|..
 * .|....-|.\
 * ..//.|....
 *
 * The beam enters in the top-left corner from the left and heading to the right. Then, its behavior depends on what it
 * encounters as it moves:
 *
 * If the beam encounters empty space (.), it continues in the same direction.
 * If the beam encounters a mirror (/ or \), the beam is reflected 90 degrees depending on the angle of the mirror. For
 * instance, a rightward-moving beam that encounters a / mirror would continue upward in the mirror's column, while a
 * rightward-moving beam that encounters a \ mirror would continue downward from the mirror's column.
 * If the beam encounters the pointy end of a splitter (| or -), the beam passes through the splitter as if the
 * splitter were empty space. For instance, a rightward-moving beam that encounters a - splitter would continue in the
 * same direction.
 * If the beam encounters the flat side of a splitter (| or -), the beam is split into two beams going in each of the
 * two directions the splitter's pointy ends are pointing. For instance, a rightward-moving beam that encounters a |
 * splitter would split into two beams: one that continues upward from the splitter's column and one that continues
 * downward from the splitter's column.
 *
 * Beams do not interact with other beams; a tile can have many beams passing through it at the same time. A tile is
 * energized if that tile has at least one beam pass through it, reflect in it, or split in it.
 * In the above example, here is how the beam of light bounces around the contraption:
 * >|<<<\....
 * |v-.\^....
 * .v...|->>>
 * .v...v^.|.
 * .v...v^...
 * .v...v^..\
 * .v../2\\..
 * <->-/vv|..
 * .|<<<2-|.\
 * .v//.|.v..
 *
 * Beams are only shown on empty tiles; arrows indicate the direction of the beams. If a tile contains beams moving in
 * multiple directions, the number of distinct directions is shown instead. Here is the same diagram but instead only
 * showing whether a tile is energized (#) or not (.):
 * ######....
 * .#...#....
 * .#...#####
 * .#...##...
 * .#...##...
 * .#...##...
 * .#..####..
 * ########..
 * .#######..
 * .#...#.#..
 *
 * Ultimately, in this example, 46 tiles become energized.
 * The light isn't energizing enough tiles to produce lava; to debug the contraption, you need to start by analyzing
 * the current situation. With the beam starting in the top-left heading right, how many tiles end up being energized?
 *
 * --- Part Two ---
 * As you try to work out what might be wrong, the reindeer tugs on your shirt and leads you to a nearby control panel.
 * There, a collection of buttons lets you align the contraption so that the beam enters from any edge tile and heading
 * away from that edge. (You can choose either of two directions for the beam if it starts on a corner; for instance,
 * if the beam starts in the bottom-right corner, it can start heading either left or upward.)
 * So, the beam could start on any tile in the top row (heading downward), any tile in the bottom row (heading upward),
 * any tile in the leftmost column (heading right), or any tile in the rightmost column (heading left). To produce
 * lava, you need to find the configuration that energizes as many tiles as possible.
 * In the above example, this can be achieved by starting the beam in the fourth tile from the left in the top row:
 * .|<2<\....
 * |v-v\^....
 * .v.v.|->>>
 * .v.v.v^.|.
 * .v.v.v^...
 * .v.v.v^..\
 * .v.v/2\\..
 * <-2-/vv|..
 * .|<<<2-|.\
 * .v//.|.v..
 *
 * Using this configuration, 51 tiles are energized:
 * .#####....
 * .#.#.#....
 * .#.#.#####
 * .#.#.##...
 * .#.#.##...
 * .#.#.##...
 * .#.#####..
 * ########..
 * .#######..
 * .#...#.#..
 *
 * Find the initial beam configuration that energizes the largest number of tiles; how many tiles are energized in that
 * configuration?
 *
 */
class BeamContraption(val points: Map<Point, BeamSquare>) {
  constructor(input: String) : this(input.let {
    val lines = it.trim().lines()
    val height = lines.size
    // We're going to ensure that the first line has the highest Y value to match with our Direction conventions.
    lines.flatMapIndexed { y, line ->
      line.mapIndexed { x, c -> Point(x, height - y) to BeamSquare.values().first { enum -> enum.c == c } }
    }.toMap()
  })

  fun energizedTileCount(initial: Beam = Beam(upperLeft(), Direction.E)): Int = traceBeam(initial)
          .map { it.position }
          .toSet()
          .size

  fun optimumEnergizedTileCount() = incomingBeams().maxOf { this.energizedTileCount(it) }

  private fun incomingBeams(): Sequence<Beam> = sequence {
    val maxY = points.keys.maxOf { it.y }
    val maxX = points.keys.maxOf { it.x }
    yieldAll((0..maxX).map { x -> Beam(Point(x, 0), Direction.N) })
    yieldAll((0..maxX).map { x -> Beam(Point(x, maxY), Direction.S) })
    yieldAll((0..maxY).map { y -> Beam(Point(0, y), Direction.E) })
    yieldAll((0..maxY).map { y -> Beam(Point(maxX, y), Direction.W) })
  }

  private fun upperLeft() = Point(0, points.keys.maxOf { it.y })

  // We keep a set of all the beam positions we have visited, because it is possible to get into a loop. (This happens
  // in the example input - we hit a beam splitter from both sides.)
  private fun traceBeam(initial: Beam, visited: MutableSet<Beam> = mutableSetOf()): Sequence<Beam> = sequence {
    var current = initial
    while (current.position in points.keys && current !in visited) {
      visited.add(current)
      yield(current)
      val square = points[current.position] ?: throw IllegalStateException("Can't find point $current")
      when (val next = square.newDirection(current.direction)) {
        is SingleDirection -> current = current.move(next.direction)
        is TwoDirections -> {
          yieldAll(traceBeam(current.move(next.first), visited))
          yieldAll(traceBeam(current.move(next.second), visited))
        }
      }
    }
  }
}

enum class BeamSquare(val c: Char) {

  EMPTY('.') {
    override fun newDirection(previousDirection: Direction) = SingleDirection(previousDirection)
  },

  UPPER_RIGHT_MIRROR('/') {
    override fun newDirection(previousDirection: Direction) = when (previousDirection) {
      Direction.N -> Direction.E
      Direction.W -> Direction.S
      Direction.E -> Direction.N
      Direction.S -> Direction.W
    }.let(::SingleDirection)
  },

  UPPER_LEFT_MIRROR('\\') {
    override fun newDirection(previousDirection: Direction) = when (previousDirection) {
      Direction.N -> Direction.W
      Direction.W -> Direction.N
      Direction.E -> Direction.S
      Direction.S -> Direction.E
    }.let(::SingleDirection)
  },

  VERTICAL_SPLITTER('|') {
    override fun newDirection(previousDirection: Direction) = when (previousDirection) {
      Direction.N -> SingleDirection(Direction.N)
      Direction.S -> SingleDirection(Direction.S)
      Direction.E, Direction.W -> TwoDirections(Direction.N, Direction.S)
    }
  },

  HORIZONTAL_SPLITTER('-') {
    override fun newDirection(previousDirection: Direction) = when (previousDirection) {
      Direction.W -> SingleDirection(Direction.W)
      Direction.E -> SingleDirection(Direction.E)
      Direction.N, Direction.S -> TwoDirections(Direction.W, Direction.E)
    }
  };

  abstract fun newDirection(previousDirection: Direction): DirectionResult
}

data class Beam(val position: Point, val direction: Direction) {
  fun move(newDirection: Direction) = Beam(newDirection.toPoint() + this.position, newDirection)
}

sealed interface DirectionResult
data class SingleDirection(val direction: Direction) : DirectionResult
data class TwoDirections(val first: Direction, val second: Direction) : DirectionResult

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day16/input.txt").readText().trim()
  val contraption = BeamContraption(input)

  println(contraption.energizedTileCount())
  println(contraption.optimumEnergizedTileCount())
}