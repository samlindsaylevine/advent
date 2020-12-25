package advent.year2020.day24

import java.io.File

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