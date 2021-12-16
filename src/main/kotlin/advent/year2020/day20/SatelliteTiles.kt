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

/**
 * --- Day 20: Jurassic Jigsaw ---
 * The high-speed train leaves the forest and quickly carries you south. You can even see a desert in the distance!
 * Since you have some spare time, you might as well see if there was anything interesting in the image the Mythical
 * Information Bureau satellite captured.
 * After decoding the satellite messages, you discover that the data actually contains many small images created by the
 * satellite's camera array. The camera array consists of many cameras; rather than produce a single square image, they
 * produce many smaller square image tiles that need to be reassembled back into a single image.
 * Each camera in the camera array returns a single monochrome image tile with a random unique ID number.  The tiles
 * (your puzzle input) arrived in a random order.
 * Worse yet, the camera array appears to be malfunctioning: each image tile has been rotated and flipped to a random
 * orientation. Your first task is to reassemble the original image by orienting the tiles so they fit together.
 * To show how the tiles should be reassembled, each tile's image data includes a border that should line up exactly
 * with its adjacent tiles. All tiles have this border, and the border lines up exactly when the tiles are both
 * oriented correctly. Tiles at the edge of the image also have this border, but the outermost edges won't line up with
 * any other tiles.
 * For example, suppose you have the following nine tiles:
 * Tile 2311:
 * ..##.#..#.
 * ##..#.....
 * #...##..#.
 * ####.#...#
 * ##.##.###.
 * ##...#.###
 * .#.#.#..##
 * ..#....#..
 * ###...#.#.
 * ..###..###
 * 
 * Tile 1951:
 * #.##...##.
 * #.####...#
 * .....#..##
 * #...######
 * .##.#....#
 * .###.#####
 * ###.##.##.
 * .###....#.
 * ..#.#..#.#
 * #...##.#..
 * 
 * Tile 1171:
 * ####...##.
 * #..##.#..#
 * ##.#..#.#.
 * .###.####.
 * ..###.####
 * .##....##.
 * .#...####.
 * #.##.####.
 * ####..#...
 * .....##...
 * 
 * Tile 1427:
 * ###.##.#..
 * .#..#.##..
 * .#.##.#..#
 * #.#.#.##.#
 * ....#...##
 * ...##..##.
 * ...#.#####
 * .#.####.#.
 * ..#..###.#
 * ..##.#..#.
 * 
 * Tile 1489:
 * ##.#.#....
 * ..##...#..
 * .##..##...
 * ..#...#...
 * #####...#.
 * #..#.#.#.#
 * ...#.#.#..
 * ##.#...##.
 * ..##.##.##
 * ###.##.#..
 * 
 * Tile 2473:
 * #....####.
 * #..#.##...
 * #.##..#...
 * ######.#.#
 * .#...#.#.#
 * .#########
 * .###.#..#.
 * ########.#
 * ##...##.#.
 * ..###.#.#.
 * 
 * Tile 2971:
 * ..#.#....#
 * #...###...
 * #.#.###...
 * ##.##..#..
 * .#####..##
 * .#..####.#
 * #..#.#..#.
 * ..####.###
 * ..#.#.###.
 * ...#.#.#.#
 * 
 * Tile 2729:
 * ...#.#.#.#
 * ####.#....
 * ..#.#.....
 * ....#..#.#
 * .##..##.#.
 * .#.####...
 * ####.#.#..
 * ##.####...
 * ##..#.##..
 * #.##...##.
 * 
 * Tile 3079:
 * #.#.#####.
 * .#..######
 * ..#.......
 * ######....
 * ####.#..#.
 * .#...#.##.
 * #.#####.##
 * ..#.###...
 * ..#.......
 * ..#.###...
 * 
 * By rotating, flipping, and rearranging them, you can find a square arrangement that causes all adjacent borders to
 * line up:
 * #...##.#.. ..###..### #.#.#####.
 * ..#.#..#.# ###...#.#. .#..######
 * .###....#. ..#....#.. ..#.......
 * ###.##.##. .#.#.#..## ######....
 * .###.##### ##...#.### ####.#..#.
 * .##.#....# ##.##.###. .#...#.##.
 * #...###### ####.#...# #.#####.##
 * .....#..## #...##..#. ..#.###...
 * #.####...# ##..#..... ..#.......
 * #.##...##. ..##.#..#. ..#.###...
 * 
 * #.##...##. ..##.#..#. ..#.###...
 * ##..#.##.. ..#..###.# ##.##....#
 * ##.####... .#.####.#. ..#.###..#
 * ####.#.#.. ...#.##### ###.#..###
 * .#.####... ...##..##. .######.##
 * .##..##.#. ....#...## #.#.#.#...
 * ....#..#.# #.#.#.##.# #.###.###.
 * ..#.#..... .#.##.#..# #.###.##..
 * ####.#.... .#..#.##.. .######...
 * ...#.#.#.# ###.##.#.. .##...####
 * 
 * ...#.#.#.# ###.##.#.. .##...####
 * ..#.#.###. ..##.##.## #..#.##..#
 * ..####.### ##.#...##. .#.#..#.##
 * #..#.#..#. ...#.#.#.. .####.###.
 * .#..####.# #..#.#.#.# ####.###..
 * .#####..## #####...#. .##....##.
 * ##.##..#.. ..#...#... .####...#.
 * #.#.###... .##..##... .####.##.#
 * #...###... ..##...#.. ...#..####
 * ..#.#....# ##.#.#.... ...##.....
 * 
 * For reference, the IDs of the above tiles are:
 * 1951    2311    3079
 * 2729    1427    2473
 * 2971    1489    1171
 * 
 * To check that you've assembled the image correctly, multiply the IDs of the four corner tiles together. If you do
 * this with the assembled tiles from the example above, you get 1951 * 3079 * 2971 * 1171 = 20899048083289.
 * Assemble the tiles into an image. What do you get if you multiply together the IDs of the four corner tiles?
 * 
 * --- Part Two ---
 * Now, you're ready to check the image for sea monsters.
 * The borders of each tile are not part of the actual image; start by removing them.
 * In the example above, the tiles become:
 * .#.#..#. ##...#.# #..#####
 * ###....# .#....#. .#......
 * ##.##.## #.#.#..# #####...
 * ###.#### #...#.## ###.#..#
 * ##.#.... #.##.### #...#.##
 * ...##### ###.#... .#####.#
 * ....#..# ...##..# .#.###..
 * .####... #..#.... .#......
 * 
 * #..#.##. .#..###. #.##....
 * #.####.. #.####.# .#.###..
 * ###.#.#. ..#.#### ##.#..##
 * #.####.. ..##..## ######.#
 * ##..##.# ...#...# .#.#.#..
 * ...#..#. .#.#.##. .###.###
 * .#.#.... #.##.#.. .###.##.
 * ###.#... #..#.##. ######..
 * 
 * .#.#.### .##.##.# ..#.##..
 * .####.## #.#...## #.#..#.#
 * ..#.#..# ..#.#.#. ####.###
 * #..####. ..#.#.#. ###.###.
 * #####..# ####...# ##....##
 * #.##..#. .#...#.. ####...#
 * .#.###.. ##..##.. ####.##.
 * ...###.. .##...#. ..#..###
 * 
 * Remove the gaps to form the actual image:
 * .#.#..#.##...#.##..#####
 * ###....#.#....#..#......
 * ##.##.###.#.#..######...
 * ###.#####...#.#####.#..#
 * ##.#....#.##.####...#.##
 * ...########.#....#####.#
 * ....#..#...##..#.#.###..
 * .####...#..#.....#......
 * #..#.##..#..###.#.##....
 * #.####..#.####.#.#.###..
 * ###.#.#...#.######.#..##
 * #.####....##..########.#
 * ##..##.#...#...#.#.#.#..
 * ...#..#..#.#.##..###.###
 * .#.#....#.##.#...###.##.
 * ###.#...#..#.##.######..
 * .#.#.###.##.##.#..#.##..
 * .####.###.#...###.#..#.#
 * ..#.#..#..#.#.#.####.###
 * #..####...#.#.#.###.###.
 * #####..#####...###....##
 * #.##..#..#...#..####...#
 * .#.###..##..##..####.##.
 * ...###...##...#...#..###
 * 
 * Now, you're ready to search for sea monsters! Because your image is monochrome, a sea monster will look like this:
 *                   # 
 * #    ##    ##    ###
 *  #  #  #  #  #  #   
 * 
 * When looking for this pattern in the image, the spaces can be anything; only the # need to match. Also, you might
 * need to rotate or flip your image before it's oriented correctly to find sea monsters. In the above image, after
 * flipping and rotating it to the appropriate orientation, there are two sea monsters (marked with O):
 * .####...#####..#...###..
 * #####..#..#.#.####..#.#.
 * .#.#...#.###...#.##.O#..
 * #.O.##.OO#.#.OO.##.OOO##
 * ..#O.#O#.O##O..O.#O##.##
 * ...#.#..##.##...#..#..##
 * #.##.#..#.#..#..##.#.#..
 * .###.##.....#...###.#...
 * #.####.#.#....##.#..#.#.
 * ##...#..#....#..#...####
 * ..#.##...###..#.#####..#
 * ....#.##.#.#####....#...
 * ..##.##.###.....#.##..#.
 * #...#...###..####....##.
 * .#.##...#.##.#.#.###...#
 * #.###.#..####...##..#...
 * #.###...#.##...#.##O###.
 * .O##.#OO.###OO##..OOO##.
 * ..O#.O..O..O.#O##O##.###
 * #.#..##.########..#..##.
 * #.#####..#.#...##..#....
 * #....##..#.#########..##
 * #...#.....#..##...###.##
 * #..###....##.#...##.##.#
 * 
 * Determine how rough the waters are in the sea monsters' habitat by counting the number of # that are not part of a
 * sea monster. In the above example, the habitat's water roughness is 273.
 * How many # are not part of a sea monster?
 * 
 */
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

  private val maxX by lazy { placed.keys.maxOf { it.x } }
  private val maxY by lazy { placed.keys.maxOf { it.y } }

  fun cornerProduct(): Long {
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

  fun toImage(): SatelliteImage {
    val newTileSize = SatelliteTile.size - 2
    val points = placed.entries
            .flatMap { (tilePoint, orientation) ->
              orientation.withoutBorders()
                      // Adjust each tile's points to its position in the full image, based on which tile it is.
                      .map { Point(tilePoint.x * newTileSize + it.x, tilePoint.y * newTileSize + it.y) }
            }
            .toSet()

    val possibleImages = points.orientations(newTileSize * maxX)
            .map(::SatelliteImage)

    return possibleImages.maxByOrNull { it.seaMonsterPositions.size }
            ?: throw IllegalStateException("Somehow had no possible orientations")
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

  val orientations = onPixels.orientations(size)
          .map { TileOrientation(it, this) }
}

private fun Set<Point>.flipped(width: Int) = this.map { Point((width - 1) - it.x, it.y) }.toSet()

// Rotated 90 degrees clockwise.
private fun Set<Point>.rotated(width: Int) = this.map { Point((width - 1) - it.y, it.x) }.toSet()

private fun Set<Point>.orientations(width: Int) = sequenceOf(
        this,
        this.rotated(width),
        this.rotated(width).rotated(width),
        this.rotated(width).rotated(width).rotated(width),
        this.flipped(width),
        this.flipped(width).rotated(width),
        this.flipped(width).rotated(width).rotated(width),
        this.flipped(width).rotated(width).rotated(width).rotated(width))

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

  /**
   * Drop the outer borders of each tile, and re-scale so the new upper left is again at 0, 0.
   */
  fun withoutBorders() = onPixels
          .filter { it.x != 0 && it.x != SatelliteTile.size - 1 && it.y != 0 && it.y != SatelliteTile.size - 1 }
          .map { Point(it.x - 1, it.y - 1) }
          .toSet()
}

class SatelliteImage(private val onPixels: Set<Point>) {
  companion object {
    private val seaMonsterString = """
                        # 
      #    ##    ##    ###
       #  #  #  #  #  #   
    """.trimIndent()

    // Offsets from the "tail", i.e., the far left point; that's at (0, 1), so we offset everything else by that amount.
    private val seaMonsterOffsets = seaMonsterString.lines()
            .flatMapIndexed { y, line ->
              line.mapIndexedNotNull { x, char ->
                if (char == '#') Point(x, y - 1) else null
              }
            }
            .toSet()
  }

  /**
   * All positions of sea monsters, based on their tails.
   */
  val seaMonsterPositions: Set<Point> by lazy {
    onPixels.filter { possibleTail ->
      onPixels.containsAll(seaMonsterOffsets.map { it + possibleTail })
    }.toSet()
  }

  val waterRoughness: Int by lazy {
    val seaMonsterPixels = seaMonsterPositions.flatMap { tail ->
      seaMonsterOffsets.map { it + tail }
    }.toSet()

    val water = onPixels - seaMonsterPixels

    water.size
  }

  private val seaMonsterPixels: Set<Point> by lazy {
    seaMonsterPositions.flatMap { tail -> seaMonsterOffsets.map { it + tail} }.toSet()
  }

  override fun toString(): String {
    val maxY = onPixels.maxOf { it.y }
    val maxX = onPixels.maxOf { it.y }

    return (0..maxY).joinToString(separator = "\n") { y ->
      (0..maxX).joinToString(separator = "") { x ->
        when (Point(x, y)) {
          in seaMonsterPixels -> "\u001b[31m#\u001b[0m"
          in onPixels -> "#"
          else -> "."
        }
      }
    }
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day20/input.txt")
          .readText()
          .trim()

  val tiles = SatelliteTiles(input)
  val solution = tiles.solve()

  println(solution.cornerProduct())
  val image = solution.toImage()
  println(image.waterRoughness)
  println(image)
}