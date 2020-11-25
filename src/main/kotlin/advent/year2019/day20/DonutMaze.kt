package advent.year2019.day20

import advent.utils.EndState
import advent.utils.ShortestPathFinder
import advent.utils.Steps
import java.io.File
import kotlin.math.sign

/**
 * Coordinates are taken by convention to begin at 0,0, in the upper left of the maze block, and end at the lower right.
 */
class DonutMaze private constructor(private val openSpaces: Set<MazePoint>,
                                    teleporterSpaces: Set<TeleporterSpace>) {

  companion object {
    fun parse(input: String): DonutMaze {
      val chars = input.lines().flatMapIndexed { y, line ->
        line.toCharArray().mapIndexed { x, char -> MazePoint(x, y) to char }
      }.toMap()

      val upperLeft = chars.filter { it.value == '#' }
              .map { it.key }
              .minOrNull()
              ?: throw IllegalStateException("No walls in maze input")

      // Offset to put everything into the conventional coordinate system.
      val spaces: Map<MazePoint, Char> = chars.mapKeys { it.key - upperLeft }

      val lowerRight = spaces.filter { it.value == '#' }
              .map { it.key }
              .maxOrNull()
              ?: throw IllegalStateException("No walls in maze input")

      // The corners of the space inside the maze (i.e., not walls or open space).
      val innerUpperLeft = spaces.filter { it.value != '#' && it.value != '.' }
              .filter { it.key.withinBoundingBox(MazePoint(0, 0), lowerRight) }
              .map { it.key }
              .minOrNull()
              ?: throw IllegalStateException("No inside to the donut")

      val innerLowerRight = spaces.filter { it.value != '#' && it.value != '.' }
              .filter { it.key.withinBoundingBox(MazePoint(0, 0), lowerRight) }
              .map { it.key }
              .maxOrNull()
              ?: throw IllegalStateException("No inside to the donut")

      val openSpaces = spaces.filter { it.value == '.' }.map { it.key }.toSet()

      val labelRangesAndDirections = listOf(
              // Outside top
              MazePoint(0, -1)..MazePoint(lowerRight.x, -1) to MazePoint(0, -1),
              // Outside right
              MazePoint(lowerRight.x + 1, 0)..MazePoint(lowerRight.x + 1, lowerRight.y) to MazePoint(1, 0),
              // Outside bottom
              MazePoint(0, lowerRight.y + 1)..MazePoint(lowerRight.x, lowerRight.y + 1) to MazePoint(0, 1),
              // Outside left
              MazePoint(-1, 0)..MazePoint(-1, lowerRight.y) to MazePoint(-1, 0),

              // Inside top
              innerUpperLeft..MazePoint(innerLowerRight.x, innerUpperLeft.y) to MazePoint(0, 1),
              // Inside right
              MazePoint(innerLowerRight.x, innerUpperLeft.y)..innerLowerRight to MazePoint(-1, 0),
              // Inside bottom
              MazePoint(innerUpperLeft.x, innerLowerRight.y)..innerLowerRight to MazePoint(0, -1),
              // Inside left
              innerUpperLeft..MazePoint(innerUpperLeft.x, innerLowerRight.y) to MazePoint(1, 0)
      )

      val teleporterSpaces = labelRangesAndDirections.flatMap { spaces.teleporterSpaces(it.first, it.second) }
              .toSet()

      return DonutMaze(openSpaces, teleporterSpaces)
    }

    /**
     * Reads the labelled teleporter spaces in the provided range (which should be just outside the maze) and returns
     * them.
     *
     * @param externalRange The first row of spaces inside or outside the maze to look through for labels.
     * @param labelDirection A vector (which should be of magnitude 1) that indicates which direction the additional
     * character of the label lies in.
     */
    private fun Map<MazePoint, Char>.teleporterSpaces(externalRange: MazePointRange,
                                                      labelDirection: MazePoint): List<TeleporterSpace> =
            externalRange.filter { this[it] != null && this[it] != ' ' }
                    .map { labelSpace ->
                      val otherLabelSpace = labelSpace + labelDirection
                      val firstLabelSpace = minOf(labelSpace, otherLabelSpace)
                      val secondLabelSpace = maxOf(labelSpace, otherLabelSpace)
                      val label = "${this[firstLabelSpace]}${this[secondLabelSpace]}"

                      val teleporterSpace = labelSpace - labelDirection
                      TeleporterSpace(teleporterSpace, label)
                    }
  }

  private val teleporterLabelsBySpace: Map<MazePoint, String> = teleporterSpaces.associate { it.point to it.label }
  private val teleporterSpacesByLabel: Map<String, List<MazePoint>> = teleporterSpaces.groupBy({ it.label }, { it.point })

  fun stepsToComplete(): Int {
    val start = teleporterSpacesByLabel["AA"]?.firstOrNull() ?: throw IllegalStateException("No AA space")
    val end = teleporterSpacesByLabel["ZZ"]?.firstOrNull() ?: throw java.lang.IllegalStateException("No ZZ space")

    val paths = ShortestPathFinder().find(
            start = start,
            end = EndState(end),
            nextSteps = Steps(this::adjacent)
    )

    return paths.first().totalCost
  }

  private fun adjacent(point: MazePoint): Set<MazePoint> {
    val normalSteps = point.adjacent().filter(openSpaces::contains)
    val teleportSteps = when (val teleportLabel = teleporterLabelsBySpace[point]) {
      null -> emptyList()
      else -> teleporterSpacesByLabel[teleportLabel]?.filter { it != point } ?: emptyList()
    }

    return (normalSteps + teleportSteps).toSet()
  }

  /**
   * 2D point that is [Comparable] to find the bounds of the maze - the ordering is in English page reading order
   * (i.e., top to bottom, and left to right within a line).
   */
  private data class MazePoint(val x: Int, val y: Int) : Comparable<MazePoint> {
    override fun compareTo(other: MazePoint) = when {
      this.y < other.y -> -1
      this.y > other.y -> 1
      else -> this.x - other.x
    }

    fun withinBoundingBox(upperLeft: MazePoint, lowerRight: MazePoint) =
            this.x >= upperLeft.x &&
                    this.x <= lowerRight.x &&
                    this.y >= upperLeft.y &&
                    this.y <= lowerRight.y

    operator fun plus(other: MazePoint) = MazePoint(this.x + other.x, this.y + other.y)
    operator fun minus(other: MazePoint) = MazePoint(this.x - other.x, this.y - other.y)

    infix operator fun rangeTo(other: MazePoint) = MazePointRange(this, other)

    fun adjacent() = listOf(this + MazePoint(-1, 0),
            this + MazePoint(0, 1),
            this + MazePoint(1, 0),
            this + MazePoint(0, -1))
  }

  /**
   * This is super overkill but it's fun to figure out how to implement a custom Range that lets us use the ".."
   * operator.
   */
  private class MazePointRange(override val start: MazePoint,
                               override val endInclusive: MazePoint) : ClosedRange<MazePoint>, Iterable<MazePoint> {
    override fun iterator() = MazePointRangeIterator(start, endInclusive)
  }

  /**
   * The brains of the [MazePointRange].
   */
  private class MazePointRangeIterator(private val start: MazePoint, private val end: MazePoint) : Iterator<MazePoint> {
    private var next: MazePoint? = start

    override fun hasNext() = next != null

    override fun next(): MazePoint {
      val output = next ?: throw NoSuchElementException("Walked past end of iterator")

      next = when {
        output == end -> null
        output.x == end.x -> MazePoint(output.x, output.y + (end.y - output.y).sign)
        output.y == end.y -> MazePoint(output.x + (end.x - output.x).sign, output.y)
        else -> throw IllegalStateException("Start $start and end $end aren't in a straight line")
      }

      return output
    }
  }

  private data class TeleporterSpace(val point: MazePoint, val label: String)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2019/day20/input.txt")
          .readText()

  val maze = DonutMaze.parse(input)

  println(maze.stepsToComplete())
}