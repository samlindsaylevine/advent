package advent.year2019.day24

import advent.utils.Point
import kotlin.math.pow

/**
 * --- Day 24: Planet of Discord ---
 * You land on Eris, your last stop before reaching Santa.  As soon as you do, your sensors start picking up strange
 * life forms moving around: Eris is infested with bugs! With an over 24-hour roundtrip for messages between you and
 * Earth, you'll have to deal with this problem on your own.
 * Eris isn't a very large place; a scan of the entire area fits into a 5x5 grid (your puzzle input). The scan shows
 * bugs (#) and empty spaces (.).
 * Each minute, The bugs live and die based on the number of bugs in the four adjacent tiles:
 * 
 * A bug dies (becoming an empty space) unless there is exactly one bug adjacent to it.
 * An empty space becomes infested with a bug if exactly one or two bugs are adjacent to it.
 * 
 * Otherwise, a bug or empty space remains the same.  (Tiles on the edges of the grid have fewer than four adjacent
 * tiles; the missing tiles count as empty space.) This process happens in every location simultaneously; that is,
 * within the same minute, the number of adjacent bugs is counted for every tile first, and then the tiles are updated.
 * Here are the first few minutes of an example scenario:
 * Initial state:
 * ....#
 * #..#.
 * #..##
 * ..#..
 * #....
 * 
 * After 1 minute:
 * #..#.
 * ####.
 * ###.#
 * ##.##
 * .##..
 * 
 * After 2 minutes:
 * #####
 * ....#
 * ....#
 * ...#.
 * #.###
 * 
 * After 3 minutes:
 * #....
 * ####.
 * ...##
 * #.##.
 * .##.#
 * 
 * After 4 minutes:
 * ####.
 * ....#
 * ##..#
 * .....
 * ##...
 * 
 * To understand the nature of the bugs, watch for the first time a layout of bugs and empty spaces matches any
 * previous layout. In the example above, the first layout to appear twice is:
 * .....
 * .....
 * .....
 * #....
 * .#...
 * 
 * To calculate the biodiversity rating for this layout, consider each tile left-to-right in the top row, then
 * left-to-right in the second row, and so on. Each of these tiles is worth biodiversity points equal to increasing
 * powers of two: 1, 2, 4, 8, 16, 32, and so on.  Add up the biodiversity points for tiles with bugs; in this example,
 * the 16th tile (32768 points) and 22nd tile (2097152 points) have bugs, a total biodiversity rating of 2129920.
 * What is the biodiversity rating for the first layout that appears twice?
 * 
 * --- Part Two ---
 * After careful analysis, one thing is certain: you have no idea where all these bugs are coming from.
 * Then, you remember: Eris is an old Plutonian settlement! Clearly, the bugs are coming from recursively-folded space.
 * This 5x5 grid is only one level in an infinite number of recursion levels. The tile in the middle of the grid is
 * actually another 5x5 grid, the grid in your scan is contained as the middle tile of a larger 5x5 grid, and so on.
 * Two levels of grids look like this:
 *      |     |         |     |     
 *      |     |         |     |     
 *      |     |         |     |     
 * -----+-----+---------+-----+-----
 *      |     |         |     |     
 *      |     |         |     |     
 *      |     |         |     |     
 * -----+-----+---------+-----+-----
 *      |     | | | | | |     |     
 *      |     |-+-+-+-+-|     |     
 *      |     | | | | | |     |     
 *      |     |-+-+-+-+-|     |     
 *      |     | | |?| | |     |     
 *      |     |-+-+-+-+-|     |     
 *      |     | | | | | |     |     
 *      |     |-+-+-+-+-|     |     
 *      |     | | | | | |     |     
 * -----+-----+---------+-----+-----
 *      |     |         |     |     
 *      |     |         |     |     
 *      |     |         |     |     
 * -----+-----+---------+-----+-----
 *      |     |         |     |     
 *      |     |         |     |     
 *      |     |         |     |     
 * 
 * (To save space, some of the tiles are not drawn to scale.)  Remember, this is only a small part of the infinitely
 * recursive grid; there is a 5x5 grid that contains this diagram, and a 5x5 grid that contains that one, and so on. 
 * Also, the ? in the diagram contains another 5x5 grid, which itself contains another 5x5 grid, and so on.
 * The scan you took (your puzzle input) shows where the bugs are on a single level of this structure. The middle tile
 * of your scan is empty to accommodate the recursive grids within it. Initially, no other levels contain bugs.
 * Tiles still count as adjacent if they are directly up, down, left, or right of a given tile. Some tiles have
 * adjacent tiles at a recursion level above or below its own level. For example:
 *      |     |         |     |     
 *   1  |  2  |    3    |  4  |  5  
 *      |     |         |     |     
 * -----+-----+---------+-----+-----
 *      |     |         |     |     
 *   6  |  7  |    8    |  9  |  10 
 *      |     |         |     |     
 * -----+-----+---------+-----+-----
 *      |     |A|B|C|D|E|     |     
 *      |     |-+-+-+-+-|     |     
 *      |     |F|G|H|I|J|     |     
 *      |     |-+-+-+-+-|     |     
 *  11  | 12  |K|L|?|N|O|  14 |  15 
 *      |     |-+-+-+-+-|     |     
 *      |     |P|Q|R|S|T|     |     
 *      |     |-+-+-+-+-|     |     
 *      |     |U|V|W|X|Y|     |     
 * -----+-----+---------+-----+-----
 *      |     |         |     |     
 *  16  | 17  |    18   |  19 |  20 
 *      |     |         |     |     
 * -----+-----+---------+-----+-----
 *      |     |         |     |     
 *  21  | 22  |    23   |  24 |  25 
 *      |     |         |     |     
 * 
 * 
 * Tile 19 has four adjacent tiles: 14, 18, 20, and 24.
 * Tile G has four adjacent tiles: B, F, H, and L.
 * Tile D has four adjacent tiles: 8, C, E, and I.
 * Tile E has four adjacent tiles: 8, D, 14, and J.
 * Tile 14 has eight adjacent tiles: 9, E, J, O, T, Y, 15, and 19.
 * Tile N has eight adjacent tiles: I, O, S, and five tiles within the sub-grid marked ?.
 * 
 * The rules about bugs living and dying are the same as before.
 * For example, consider the same initial state as above:
 * ....#
 * #..#.
 * #.?##
 * ..#..
 * #....
 * 
 * The center tile is drawn as ? to indicate the next recursive grid. Call this level 0; the grid within this one is
 * level 1, and the grid that contains this one is level -1.  Then, after ten minutes, the grid at each level would
 * look like this:
 * Depth -5:
 * ..#..
 * .#.#.
 * ..?.#
 * .#.#.
 * ..#..
 * 
 * Depth -4:
 * ...#.
 * ...##
 * ..?..
 * ...##
 * ...#.
 * 
 * Depth -3:
 * #.#..
 * .#...
 * ..?..
 * .#...
 * #.#..
 * 
 * Depth -2:
 * .#.##
 * ....#
 * ..?.#
 * ...##
 * .###.
 * 
 * Depth -1:
 * #..##
 * ...##
 * ..?..
 * ...#.
 * .####
 * 
 * Depth 0:
 * .#...
 * .#.##
 * .#?..
 * .....
 * .....
 * 
 * Depth 1:
 * .##..
 * #..##
 * ..?.#
 * ##.##
 * #####
 * 
 * Depth 2:
 * ###..
 * ##.#.
 * #.?..
 * .#.##
 * #.#..
 * 
 * Depth 3:
 * ..###
 * .....
 * #.?..
 * #....
 * #...#
 * 
 * Depth 4:
 * .###.
 * #..#.
 * #.?..
 * ##.#.
 * .....
 * 
 * Depth 5:
 * ####.
 * #..#.
 * #.?#.
 * ####.
 * .....
 * 
 * In this example, after 10 minutes, a total of 99 bugs are present.
 * Starting with your scan, how many bugs are present after 200 minutes?
 * 
 */
class SimpleEris(val initialState: SimpleErisState) {
  fun firstToAppearTwice(): SimpleErisState {
    val observed = mutableSetOf<SimpleErisState>()
    var current = initialState
    while (!observed.contains(current)) {
      observed.add(current)
      current = current.next()
    }
    return current
  }
}

data class SimpleErisState(val bugs: Set<Point>) {
  companion object {
    fun parsePoints(input: String): Set<Point> {
      val lines = input.lines()

      val bugs = allPoints.filter { lines[it.y][it.x] == '#' }

      return bugs.toSet()
    }

    fun parse(input: String) = SimpleErisState(parsePoints(input))

    val allPoints = (0..4).flatMap { y ->
      (0..4).map { x -> Point(x, y) }
    }

    val allPointsExceptMiddle = allPoints.filter { !(it.x == 2 && it.y == 2) }.toSet()
  }

  fun next(): SimpleErisState {
    val newBugs = allPoints.filter {
      (bugs.contains(it) && it.neighborBugCount() == 1) ||
              (!bugs.contains(it) && (it.neighborBugCount() == 1 || it.neighborBugCount() == 2))
    }
    return SimpleErisState(newBugs.toSet())
  }

  private fun Point.neighborBugCount() = this.adjacentNeighbors.count { bugs.contains(it) }

  fun biodiversity() = allPoints.mapIndexed { i, point -> if (bugs.contains(point)) 2.0.pow(i).toLong() else 0 }.sum()
}

data class RecursiveErisState(val floors: Map<Int, SimpleErisState>) {

  constructor(floor: SimpleErisState) : this(mapOf(0 to floor))

  fun next(): RecursiveErisState {
    val minFloor = floors.keys.minOrNull() ?: 0
    val maxFloor = floors.keys.maxOrNull() ?: 0

    val newFloors = mutableMapOf<Int, SimpleErisState>()
    floors.forEach { (num, _) -> newFloors[num] = nextFloor(num) }
    nextFloor(minFloor - 1).takeIf { it.bugs.isNotEmpty() }?.let { newFloors[minFloor - 1] = it }
    nextFloor(maxFloor + 1).takeIf { it.bugs.isNotEmpty() }?.let { newFloors[maxFloor + 1] = it }

    return RecursiveErisState(newFloors)
  }

  fun bugCount() = floors.values.sumBy { it.bugs.count() }

  fun advanced(steps: Int) = (1..steps).fold(this) { state, _ -> state.next() }

  private fun nextFloor(floorNum: Int): SimpleErisState {
    val newBugs = SimpleErisState.allPointsExceptMiddle.filter {
      val neighborCount = neighborBugCount(floorNum, it)

      (hasBug(floorNum, it) && neighborCount == 1) ||
              (!hasBug(floorNum, it) && (neighborCount == 1 || neighborCount == 2))
    }

    return SimpleErisState(newBugs.toSet())
  }

  private fun neighbors(floorNum: Int, point: Point): Set<Pair<Int, Point>> {
    val output = mutableSetOf<Pair<Int, Point>>()

    val validNeighbors = point.adjacentNeighbors.intersect(SimpleErisState.allPointsExceptMiddle)
    output.addAll(validNeighbors.map { floorNum to it })

    // Connections upwards.
    if (point.x == 0) {
      output.add(floorNum - 1 to Point(1, 2))
    }
    if (point.y == 0) {
      output.add(floorNum - 1 to Point(2, 1))
    }
    if (point.x == 4) {
      output.add(floorNum - 1 to Point(3, 2))
    }
    if (point.y == 4) {
      output.add(floorNum - 1 to Point(2, 3))
    }

    // Connections downwards.
    if (point == Point(2, 1)) {
      output.add(floorNum + 1 to Point(0, 0))
      output.add(floorNum + 1 to Point(1, 0))
      output.add(floorNum + 1 to Point(2, 0))
      output.add(floorNum + 1 to Point(3, 0))
      output.add(floorNum + 1 to Point(4, 0))
    }
    if (point == Point(1, 2)) {
      output.add(floorNum + 1 to Point(0, 0))
      output.add(floorNum + 1 to Point(0, 1))
      output.add(floorNum + 1 to Point(0, 2))
      output.add(floorNum + 1 to Point(0, 3))
      output.add(floorNum + 1 to Point(0, 4))
    }
    if (point == Point(2, 3)) {
      output.add(floorNum + 1 to Point(0, 4))
      output.add(floorNum + 1 to Point(1, 4))
      output.add(floorNum + 1 to Point(2, 4))
      output.add(floorNum + 1 to Point(3, 4))
      output.add(floorNum + 1 to Point(4, 4))
    }
    if (point == Point(3, 2)) {
      output.add(floorNum + 1 to Point(4, 0))
      output.add(floorNum + 1 to Point(4, 1))
      output.add(floorNum + 1 to Point(4, 2))
      output.add(floorNum + 1 to Point(4, 3))
      output.add(floorNum + 1 to Point(4, 4))
    }

    return output
  }

  private fun neighborBugCount(floorNum: Int, point: Point) = neighbors(floorNum, point)
          .count { hasBug(it.first, it.second) }

  private fun hasBug(floorNum: Int, point: Point) = floors[floorNum]?.bugs?.contains(point) ?: false
}

fun main() {
  val input = """
    #####
    .....
    ....#
    #####
    .###.
  """.trimIndent()

  val initialState = SimpleErisState.parse(input)

  println(SimpleEris(initialState).firstToAppearTwice().biodiversity())

  println(RecursiveErisState(initialState).advanced(200).bugCount())
}