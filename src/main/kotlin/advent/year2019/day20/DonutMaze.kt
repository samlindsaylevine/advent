package advent.year2019.day20

import advent.utils.EndState
import advent.utils.ShortestPathFinder
import advent.utils.Steps
import java.io.File
import kotlin.math.sign

/**
 * Coordinates are taken by convention to begin at 0,0, in the upper left of the maze block, and end at the lower right.
 *
 * --- Day 20: Donut Maze ---
 * You notice a strange pattern on the surface of Pluto and land nearby to get a closer look. Upon closer inspection,
 * you realize you've come across one of the famous space-warping mazes of the long-lost Pluto civilization!
 * Because there isn't much space on Pluto, the civilization that used to live here thrived by inventing a method for
 * folding spacetime.  Although the technology is no longer understood, mazes like this one provide a small glimpse
 * into the daily life of an ancient Pluto citizen.
 * This maze is shaped like a donut. Portals along the inner and outer edge of the donut can instantly teleport you
 * from one side to the other.  For example:
 *          A
 *          A
 *   #######.#########
 *   #######.........#
 *   #######.#######.#
 *   #######.#######.#
 *   #######.#######.#
 *   #####  B    ###.#
 * BC...##  C    ###.#
 *   ##.##       ###.#
 *   ##...DE  F  ###.#
 *   #####    G  ###.#
 *   #########.#####.#
 * DE..#######...###.#
 *   #.#########.###.#
 * FG..#########.....#
 *   ###########.#####
 *              Z
 *              Z
 *
 * This map of the maze shows solid walls (#) and open passages (.). Every maze on Pluto has a start (the open tile
 * next to AA) and an end (the open tile next to ZZ). Mazes on Pluto also have portals; this maze has three pairs of
 * portals: BC, DE, and FG. When on an open tile next to one of these labels, a single step can take you to the other
 * tile with the same label. (You can only walk on . tiles; labels and empty space are not traversable.)
 * One path through the maze doesn't require any portals.  Starting at AA, you could go down 1, right 8, down 12, left
 * 4, and down 1 to reach ZZ, a total of 26 steps.
 * However, there is a shorter path:  You could walk from AA to the inner BC portal (4 steps), warp to the outer BC
 * portal (1 step), walk to the inner DE (6 steps), warp to the outer DE (1 step), walk to the outer FG (4 steps), warp
 * to the inner FG (1 step), and finally walk to ZZ (6 steps). In total, this is only 23 steps.
 * Here is a larger example:
 *                    A
 *                    A
 *   #################.#############
 *   #.#...#...................#.#.#
 *   #.#.#.###.###.###.#########.#.#
 *   #.#.#.......#...#.....#.#.#...#
 *   #.#########.###.#####.#.#.###.#
 *   #.............#.#.....#.......#
 *   ###.###########.###.#####.#.#.#
 *   #.....#        A   C    #.#.#.#
 *   #######        S   P    #####.#
 *   #.#...#                 #......VT
 *   #.#.#.#                 #.#####
 *   #...#.#               YN....#.#
 *   #.###.#                 #####.#
 * DI....#.#                 #.....#
 *   #####.#                 #.###.#
 * ZZ......#               QG....#..AS
 *   ###.###                 #######
 * JO..#.#.#                 #.....#
 *   #.#.#.#                 ###.#.#
 *   #...#..DI             BU....#..LF
 *   #####.#                 #.#####
 * YN......#               VT..#....QG
 *   #.###.#                 #.###.#
 *   #.#...#                 #.....#
 *   ###.###    J L     J    #.#.###
 *   #.....#    O F     P    #.#...#
 *   #.###.#####.#.#####.#####.###.#
 *   #...#.#.#...#.....#.....#.#...#
 *   #.#####.###.###.#.#.#########.#
 *   #...#.#.....#...#.#.#.#.....#.#
 *   #.###.#####.###.###.#.#.#######
 *   #.#.........#...#.............#
 *   #########.###.###.#############
 *            B   J   C
 *            U   P   P
 *
 * Here, AA has no direct path to ZZ, but it does connect to AS and CP. By passing through AS, QG, BU, and JO, you can
 * reach ZZ in 58 steps.
 * In your maze, how many steps does it take to get from the open tile marked AA to the open tile marked ZZ?
 *
 * --- Part Two ---
 * Strangely, the exit isn't open when you reach it.  Then, you remember: the ancient Plutonians were famous for
 * building recursive spaces.
 * The marked connections in the maze aren't portals: they physically connect to a larger or smaller copy of the maze.
 * Specifically, the labeled tiles around the inside edge actually connect to a smaller copy of the same maze, and the
 * smaller copy's inner labeled tiles connect to yet a smaller copy, and so on.
 * When you enter the maze, you are at the outermost level; when at the outermost level, only the outer labels AA and
 * ZZ function (as the start and end, respectively); all other outer labeled tiles are effectively walls. At any other
 * level, AA and ZZ count as walls, but the other outer labeled tiles bring you one level outward.
 * Your goal is to find a path through the maze that brings you back to ZZ at the outermost level of the maze.
 * In the first example above, the shortest path is now the loop around the right side. If the starting level is 0,
 * then taking the previously-shortest path would pass through BC (to level 1), DE (to level 2), and FG (back to level
 * 1). Because this is not the outermost level, ZZ is a wall, and the only option is to go back around to BC, which
 * would only send you even deeper into the recursive maze.
 * In the second example above, there is no path that brings you to ZZ at the outermost level.
 * Here is a more interesting example:
 *              Z L X W       C
 *              Z P Q B       K
 *   ###########.#.#.#.#######.###############
 *   #...#.......#.#.......#.#.......#.#.#...#
 *   ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###
 *   #.#...#.#.#...#.#.#...#...#...#.#.......#
 *   #.###.#######.###.###.#.###.###.#.#######
 *   #...#.......#.#...#...#.............#...#
 *   #.#########.#######.#.#######.#######.###
 *   #...#.#    F       R I       Z    #.#.#.#
 *   #.###.#    D       E C       H    #.#.#.#
 *   #.#...#                           #...#.#
 *   #.###.#                           #.###.#
 *   #.#....OA                       WB..#.#..ZH
 *   #.###.#                           #.#.#.#
 * CJ......#                           #.....#
 *   #######                           #######
 *   #.#....CK                         #......IC
 *   #.###.#                           #.###.#
 *   #.....#                           #...#.#
 *   ###.###                           #.#.#.#
 * XF....#.#                         RF..#.#.#
 *   #####.#                           #######
 *   #......CJ                       NM..#...#
 *   ###.#.#                           #.###.#
 * RE....#.#                           #......RF
 *   ###.###        X   X       L      #.#.#.#
 *   #.....#        F   Q       P      #.#.#.#
 *   ###.###########.###.#######.#########.###
 *   #.....#...#.....#.......#...#.....#.#...#
 *   #####.#.###.#######.#######.###.###.#.#.#
 *   #.......#.......#.#.#.#.#...#...#...#.#.#
 *   #####.###.#####.#.#.#.#.###.###.#.###.###
 *   #.......#.....#.#...#...............#...#
 *   #############.#.#.###.###################
 *                A O F   N
 *                A A D   M
 *
 * One shortest path through the maze is the following:
 *
 * Walk from AA to XF (16 steps)
 * Recurse into level 1 through XF (1 step)
 * Walk from XF to CK (10 steps)
 * Recurse into level 2 through CK (1 step)
 * Walk from CK to ZH (14 steps)
 * Recurse into level 3 through ZH (1 step)
 * Walk from ZH to WB (10 steps)
 * Recurse into level 4 through WB (1 step)
 * Walk from WB to IC (10 steps)
 * Recurse into level 5 through IC (1 step)
 * Walk from IC to RF (10 steps)
 * Recurse into level 6 through RF (1 step)
 * Walk from RF to NM (8 steps)
 * Recurse into level 7 through NM (1 step)
 * Walk from NM to LP (12 steps)
 * Recurse into level 8 through LP (1 step)
 * Walk from LP to FD (24 steps)
 * Recurse into level 9 through FD (1 step)
 * Walk from FD to XQ (8 steps)
 * Recurse into level 10 through XQ (1 step)
 * Walk from XQ to WB (4 steps)
 * Return to level 9 through WB (1 step)
 * Walk from WB to ZH (10 steps)
 * Return to level 8 through ZH (1 step)
 * Walk from ZH to CK (14 steps)
 * Return to level 7 through CK (1 step)
 * Walk from CK to XF (10 steps)
 * Return to level 6 through XF (1 step)
 * Walk from XF to OA (14 steps)
 * Return to level 5 through OA (1 step)
 * Walk from OA to CJ (8 steps)
 * Return to level 4 through CJ (1 step)
 * Walk from CJ to RE (8 steps)
 * Return to level 3 through RE (1 step)
 * Walk from RE to IC (4 steps)
 * Recurse into level 4 through IC (1 step)
 * Walk from IC to RF (10 steps)
 * Recurse into level 5 through RF (1 step)
 * Walk from RF to NM (8 steps)
 * Recurse into level 6 through NM (1 step)
 * Walk from NM to LP (12 steps)
 * Recurse into level 7 through LP (1 step)
 * Walk from LP to FD (24 steps)
 * Recurse into level 8 through FD (1 step)
 * Walk from FD to XQ (8 steps)
 * Recurse into level 9 through XQ (1 step)
 * Walk from XQ to WB (4 steps)
 * Return to level 8 through WB (1 step)
 * Walk from WB to ZH (10 steps)
 * Return to level 7 through ZH (1 step)
 * Walk from ZH to CK (14 steps)
 * Return to level 6 through CK (1 step)
 * Walk from CK to XF (10 steps)
 * Return to level 5 through XF (1 step)
 * Walk from XF to OA (14 steps)
 * Return to level 4 through OA (1 step)
 * Walk from OA to CJ (8 steps)
 * Return to level 3 through CJ (1 step)
 * Walk from CJ to RE (8 steps)
 * Return to level 2 through RE (1 step)
 * Walk from RE to XQ (14 steps)
 * Return to level 1 through XQ (1 step)
 * Walk from XQ to FD (8 steps)
 * Return to level 0 through FD (1 step)
 * Walk from FD to ZZ (18 steps)
 *
 * This path takes a total of 396 steps to move from AA at the outermost layer to ZZ at the outermost layer.
 * In your maze, when accounting for recursion, how many steps does it take to get from the open tile marked AA to the
 * open tile marked ZZ, both at the outermost layer?
 *
 */
class DonutMaze private constructor(
  private val openSpaces: Set<MazePoint>,
  teleporterSpaces: Set<TeleporterSpace>
) {

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
    private fun Map<MazePoint, Char>.teleporterSpaces(
      externalRange: MazePointRange,
      labelDirection: MazePoint
    ): List<TeleporterSpace> =
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
  private val teleporterSpacesByLabel: Map<String, List<MazePoint>> =
    teleporterSpaces.groupBy({ it.label }, { it.point })

  private val start = teleporterSpacesByLabel["AA"]?.firstOrNull() ?: throw IllegalStateException("No AA space")
  private val end = teleporterSpacesByLabel["ZZ"]?.firstOrNull() ?: throw java.lang.IllegalStateException("No ZZ space")

  private val lowerRightCorner = MazePoint(
    openSpaces.maxOf { it.x },
    openSpaces.maxOf { it.y }
  )

  /**
   * From part 1, with teleportation.
   */
  fun stepsToComplete(): Int {
    val paths = ShortestPathFinder().find(
      start = start,
      end = EndState(end),
      nextSteps = Steps(this::adjacent)
    )

    return paths.first().totalCost
  }

  fun recursiveStepsToComplete(): Int {
    val paths = ShortestPathFinder().find(
      start = RecursiveTraversal(point = start, depth = 0),
      end = EndState(RecursiveTraversal(point = end, depth = 0)),
      nextSteps = Steps(this::adjacentWithRecursion),
      reportEvery = 100
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

  private fun adjacentWithRecursion(traversal: RecursiveTraversal): Set<RecursiveTraversal> {
    val normalSteps = traversal.point.adjacent()
      .filter(openSpaces::contains)
      .map { RecursiveTraversal(it, traversal.depth) }

    val teleportLabel = teleporterLabelsBySpace[traversal.point]
    val depthDelta = if (isOnOuterBorder(traversal.point)) -1 else 1

    val recurseSteps: List<RecursiveTraversal> = when {
      traversal.depth == 0 && depthDelta == -1 -> emptyList()
      teleportLabel == null -> emptyList()
      teleportLabel == "AA" -> emptyList()
      teleportLabel == "ZZ" -> emptyList()
      else -> (teleporterSpacesByLabel[teleportLabel]?.filter { it != traversal.point } ?: emptyList())
        .map { RecursiveTraversal(it, traversal.depth + depthDelta) }
    }

    return (normalSteps + recurseSteps).toSet()
  }

  private fun isOnOuterBorder(point: MazePoint): Boolean =
    point.x == 0 || point.y == 0 || point.x == lowerRightCorner.x || point.y == lowerRightCorner.y

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

    fun adjacent() = listOf(
      this + MazePoint(-1, 0),
      this + MazePoint(0, 1),
      this + MazePoint(1, 0),
      this + MazePoint(0, -1)
    )
  }

  /**
   * This is super overkill but it's fun to figure out how to implement a custom Range that lets us use the ".."
   * operator.
   */
  private class MazePointRange(
    override val start: MazePoint,
    override val endInclusive: MazePoint
  ) : ClosedRange<MazePoint>, Iterable<MazePoint> {
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

  private data class RecursiveTraversal(val point: MazePoint, val depth: Int)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2019/day20/input.txt")
    .readText()

  val maze = DonutMaze.parse(input)

  println(maze.stepsToComplete())

  // This takes a long time. Obviously we should do a similar thing to what we did in day 18, and transform from actual
  // steps into distances between nodes, and have edges with weights, so we can blast through this faster.
  // But, I just let it run for 20 minutes while I was cooking black pepper eggplant and tofu, and got the answer.
  println(maze.recursiveStepsToComplete())
}