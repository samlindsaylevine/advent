package advent.year2020.day24

import java.io.File

/**
 * --- Day 24: Lobby Layout ---
 * Your raft makes it to the tropical island; it turns out that the small crab was an excellent navigator. You make
 * your way to the resort.
 * As you enter the lobby, you discover a small problem: the floor is being renovated. You can't even reach the
 * check-in desk until they've finished installing the new tile floor.
 * The tiles are all hexagonal; they need to be arranged in a hex grid with a very specific color pattern. Not in the
 * mood to wait, you offer to help figure out the pattern.
 * The tiles are all white on one side and black on the other. They start with the white side facing up. The lobby is
 * large enough to fit whatever pattern might need to appear there.
 * A member of the renovation crew gives you a list of the tiles that need to be flipped over (your puzzle input). Each
 * line in the list identifies a single tile that needs to be flipped by giving a series of steps starting from a
 * reference tile in the very center of the room. (Every line starts from the same reference tile.)
 * Because the tiles are hexagonal, every tile has six neighbors: east, southeast, southwest, west, northwest, and
 * northeast. These directions are given in your list, respectively, as e, se, sw, w, nw, and ne. A tile is identified
 * by a series of these directions with no delimiters; for example, esenee identifies the tile you land on if you start
 * at the reference tile and then move one tile east, one tile southeast, one tile northeast, and one tile east.
 * Each time a tile is identified, it flips from white to black or from black to white. Tiles might be flipped more
 * than once. For example, a line like esew flips a tile immediately adjacent to the reference tile, and a line like
 * nwwswee flips the reference tile itself.
 * Here is a larger example:
 * sesenwnenenewseeswwswswwnenewsewsw
 * neeenesenwnwwswnenewnwwsewnenwseswesw
 * seswneswswsenwwnwse
 * nwnwneseeswswnenewneswwnewseswneseene
 * swweswneswnenwsewnwneneseenw
 * eesenwseswswnenwswnwnwsewwnwsene
 * sewnenenenesenwsewnenwwwse
 * wenwwweseeeweswwwnwwe
 * wsweesenenewnwwnwsenewsenwwsesesenwne
 * neeswseenwwswnwswswnw
 * nenwswwsewswnenenewsenwsenwnesesenew
 * enewnwewneswsewnwswenweswnenwsenwsw
 * sweneswneswneneenwnewenewwneswswnese
 * swwesenesewenwneswnwwneseswwne
 * enesenwswwswneneswsenwnewswseenwsese
 * wnwnesenesenenwwnenwsewesewsesesew
 * nenewswnwewswnenesenwnesewesw
 * eneswnwswnwsenenwnwnwwseeswneewsenese
 * neswnwewnwnwseenwseesewsenwsweewe
 * wseweeenwnesenwwwswnew
 * 
 * In the above example, 10 tiles are flipped once (to black), and 5 more are flipped twice (to black, then back to
 * white). After all of these instructions have been followed, a total of 10 tiles are black.
 * Go through the renovation crew's list and determine which tiles they need to flip. After all of the instructions
 * have been followed, how many tiles are left with the black side up?
 * 
 * --- Part Two ---
 * The tile floor in the lobby is meant to be a living art exhibit. Every day, the tiles are all flipped according to
 * the following rules:
 * 
 * Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to white.
 * Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.
 * 
 * Here, tiles immediately adjacent means the six tiles directly touching the tile in question.
 * The rules are applied simultaneously to every tile; put another way, it is first determined which tiles need to be
 * flipped, then they are all flipped at the same time.
 * In the above example, the number of black tiles that are facing up after the given number of days has passed is as
 * follows:
 * Day 1: 15
 * Day 2: 12
 * Day 3: 25
 * Day 4: 14
 * Day 5: 23
 * Day 6: 28
 * Day 7: 41
 * Day 8: 37
 * Day 9: 49
 * Day 10: 37
 * 
 * Day 20: 132
 * Day 30: 259
 * Day 40: 406
 * Day 50: 566
 * Day 60: 788
 * Day 70: 1106
 * Day 80: 1373
 * Day 90: 1844
 * Day 100: 2208
 * 
 * After executing this process a total of 100 times, there would be 2208 black tiles facing up.
 * How many tiles will be black after 100 days?
 * 
 */
class HexTileFloor(val tiles: Set<HexLocation>) {
  constructor(input: String) : this(input.lines()
          .map(HexLocation::parse)
          .groupingBy { it }
          .eachCount()
          .filter { it.value % 2 == 1 }
          .map { it.key }
          .toSet())

  fun next(): HexTileFloor {
    val minE = tiles.minOf { it.eAxis }
    val maxE = tiles.maxOf { it.eAxis }
    val minNw = tiles.minOf { it.nwAxis }
    val maxNw = tiles.maxOf { it.nwAxis }

    val boundingBox = ((minE - 1)..(maxE + 1)).flatMap { e ->
      ((minNw - 1)..(maxNw + 1)).map { nw ->
        HexLocation(eAxis = e, nwAxis = nw)
      }
    }

    return boundingBox
            .filter {
              val neighborCount = it.neighbors.count(tiles::contains)
              (it in tiles && (neighborCount == 1 || neighborCount == 2))
                      || (it !in tiles && neighborCount == 2)
            }
            .toSet()
            .let(::HexTileFloor)
  }

  fun next(steps: Int) = nextRecursive(this, steps)

  private tailrec fun nextRecursive(floor: HexTileFloor,
                                    steps: Int): HexTileFloor = when {
    steps < 0 -> throw IllegalArgumentException("""Attempting to advance $steps steps:
      The Moving Finger writes; and, having writ,
      Moves on: nor all thy Piety nor Wit
      Shall lure it back to cancel half a Line,
      Nor all thy Tears wash out a Word of it.
    """.trimMargin())
    steps == 0 -> floor
    else -> nextRecursive(floor.next(), steps - 1)
  }

}

/**
 * We can represent a hex location using a vector basis of the E direction and the NW direction.
 *
 * In this case, a step in the NE direction is represented as (+1, +1) N and SE.
 */
data class HexLocation(val eAxis: Int, val nwAxis: Int) {
  companion object {
    fun parse(input: String) = input.toHexDirections()
            .fold(HexLocation(0, 0), HexLocation::plus)
  }

  operator fun plus(direction: HexDirection) =
          HexLocation(this.eAxis + direction.eAxis, this.nwAxis + direction.nwAxis)

  val neighbors by lazy {
    HexDirection.values().map { this + it }
  }
}

enum class HexDirection(val eAxis: Int, val nwAxis: Int) {
  e(1, 0),
  se(0, -1),
  sw(-1, -1),
  w(-1, 0),
  nw(0, 1),
  ne(1, 1);
}

private tailrec fun String.toHexDirections(partialOutput: List<HexDirection> = emptyList()): List<HexDirection> {
  if (this.isEmpty()) return partialOutput

  val nextDirection = HexDirection.values().firstOrNull { this.startsWith(it.name) }
          ?: throw IllegalArgumentException("Unparseable direction string $this")

  return this.substringAfter(nextDirection.name).toHexDirections(partialOutput + nextDirection)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day24/input.txt")
          .readText()
          .trim()

  val floor = HexTileFloor(input)
  println(floor.tiles.size)
  println(floor.next(100).tiles.size)
}