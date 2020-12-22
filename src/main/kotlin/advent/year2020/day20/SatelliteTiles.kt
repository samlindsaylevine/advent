package advent.year2020.day20

import advent.utils.Point
import advent.utils.PointRange
import advent.year2020.day20.SatelliteTile.Companion.northeastCorner
import advent.year2020.day20.SatelliteTile.Companion.northwestCorner
import advent.year2020.day20.SatelliteTile.Companion.southeastCorner
import advent.year2020.day20.SatelliteTile.Companion.southwestCorner
import java.io.File
import kotlin.IllegalStateException
import kotlin.math.sqrt

class SatelliteTiles(input: String) {

  private val tiles = input.split("\n\n")
          .map(::SatelliteTile)

  // We enforce that the number of tiles must be a square number.
  // This is the number of tiles on one edge of the puzzle, i.e., the sqrt of the number of tiles
  private val size: Int = tiles.size.let {
    val output = sqrt(tiles.size.toDouble()).toInt()
    require(output * output == tiles.size) { "Invalid number of tiles ${tiles.size}, must be a square number" }
    output
  }

  fun solve(): SatelliteTileSolution {
    val openLocations: List<Point> = (0 until size).flatMap { y ->
      (0 until size).map { x -> Point(x, y) }
    }

    val emptySolution = SatelliteTileSolution(emptyMap(), openLocations, tiles)
    return emptySolution.solve() ?: throw IllegalStateException("Unsolveable tile set")
  }
}

/**
 * A solution (possibly in progress).
 *
 * @param placed The keys in this map (as well as the points in openTileLocations) are the coordinates of the tiles
 * themselves, in the greater solution (not to be confused with the points within a tile, which are on a different
 * scale).
 */
class SatelliteTileSolution(val placed: Map<Point, TileOrientation>,
                            private val openTileLocations: List<Point>,
                            private val unplacedTiles: List<SatelliteTile>) {
  init {
    require(openTileLocations.size == unplacedTiles.size) {
      "Should be the same number of unused tiles (${unplacedTiles.size} as open spaces ({${openTileLocations.size})"
    }
  }

  /**
   * Depth-first solve the problem by considering orientiations that can fit in the first open location.
   */
  fun solve(): SatelliteTileSolution? {
    if (openTileLocations.isEmpty()) return this

    val openLocation = openTileLocations.first()

    return unplacedTiles.asSequence()
            .flatMap { it.orientations }
            .filter { orientation -> fits(openLocation, orientation) }
            .mapNotNull { orientation ->
              SatelliteTileSolution(placed + (openLocation to orientation),
                      openTileLocations - openLocation,
                      unplacedTiles - orientation.tile).solve()
            }.firstOrNull()
  }

  private fun fits(position: Point, orientation: TileOrientation): Boolean {
    // Since we try tiles left-to-right, top-to-bottom, we only need to check the new tile's left and upper edges.
    val above = placed[position + Point(0, -1)]
    val toTheLeft = placed[position + Point(-1, 0)]

    return (above == null || above.southEdge == orientation.northEdge) &&
            (toTheLeft == null || toTheLeft.eastEdge == orientation.westEdge)
  }

  fun cornerProduct(): Long {
    val maxX = placed.keys.maxOf { it.x }
    val maxY = placed.keys.maxOf { it.y }
    val corners = listOf(
            Point(0, 0),
            Point(maxX, 0),
            Point(maxX, maxY),
            Point(0, maxY),
    )

    val ids = corners.map {
      val orientation = placed[it] ?: throw IllegalStateException("No corner tile selected for $it")
      orientation.tile.id
    }

    return ids.map { it.toLong() }.reduce(Long::times)
  }
}

/**
 * A tile definition. This tile could be placed in 8 different orientations (rotated and flipped).
 */
class SatelliteTile(val id: Int,
                    val onPixels: Set<Point>) {
  constructor(input: String) : this(
          id = input.lines()
                  .first()
                  .substringBefore(":")
                  .substringAfterLast(" ")
                  .toInt(),
          onPixels = input.lines()
                  .drop(1)
                  .flatMapIndexed { y, line ->
                    line.mapIndexedNotNull { x, char -> if (char == '#') Point(x, y) else null }
                  }
                  .toSet())

  companion object {
    const val size = 10

    val northwestCorner = Point(0, 0)
    val northeastCorner = Point(size - 1, 0)
    val southwestCorner = Point(0, size - 1)
    val southeastCorner = Point(size - 1, size - 1)
  }

  private fun Set<Point>.flipped() = this.map { Point((SatelliteTile.size - 1) - it.x, it.y) }.toSet()

  // Rotated 90 degrees clockwise.
  private fun Set<Point>.rotated() = this.map { Point((SatelliteTile.size - 1) - it.y, it.x) }.toSet()

  val orientations = sequenceOf(
          onPixels,
          onPixels.rotated(),
          onPixels.rotated().rotated(),
          onPixels.rotated().rotated().rotated(),
          onPixels.flipped(),
          onPixels.flipped().rotated(),
          onPixels.flipped().rotated().rotated(),
          onPixels.flipped().rotated().rotated().rotated()
  ).map { TileOrientation(it, this) }
}

class TileOrientation(val onPixels: Set<Point>,
                      val tile: SatelliteTile) {
  // All edges are always top-to-bottom left-to-right.

  val westEdge = edge(northwestCorner..southwestCorner)
  val northEdge = edge(northwestCorner..northeastCorner)
  val eastEdge = edge(northeastCorner..southeastCorner)
  val southEdge = edge(southwestCorner..southeastCorner)

  private fun edge(range: PointRange) = range.map { onPixels.contains(it) }

  override fun toString() = (0 until SatelliteTile.size).joinToString("\n") { y ->
    (0 until SatelliteTile.size).joinToString("") { x -> if (Point(x, y) in onPixels) "#" else "." }
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day20/input.txt")
          .readText()
          .trim()

  val tiles = SatelliteTiles(input)
  val solution = tiles.solve()

  println(solution.cornerProduct())
}