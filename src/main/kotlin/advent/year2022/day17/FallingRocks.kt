package advent.year2022.day17

import advent.utils.Direction
import advent.utils.Point
import advent.utils.findLinearRecurrence
import java.io.File

/**
 * --- Day 17: Pyroclastic Flow ---
 * Your handheld device has located an alternative exit from the cave for you and the elephants.  The ground is
 * rumbling almost continuously now, but the strange valves bought you some time. It's definitely getting warmer in
 * here, though.
 * The tunnels eventually open into a very tall, narrow chamber. Large, oddly-shaped rocks are falling into the chamber
 * from above, presumably due to all the rumbling. If you can't work out where the rocks will fall next, you might be
 * crushed!
 * The five types of rocks have the following peculiar shapes, where # is rock and . is empty space:
 * ####
 *
 * .#.
 * ###
 * .#.
 *
 * ..#
 * ..#
 * ###
 *
 * #
 * #
 * #
 * #
 *
 * ##
 * ##
 *
 * The rocks fall in the order shown above: first the - shape, then the + shape, and so on. Once the end of the list is
 * reached, the same order repeats: the - shape falls first, sixth, 11th, 16th, etc.
 * The rocks don't spin, but they do get pushed around by jets of hot gas coming out of the walls themselves. A quick
 * scan reveals the effect the jets of hot gas will have on the rocks as they fall (your puzzle input).
 * For example, suppose this was the jet pattern in your cave:
 * >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
 *
 * In jet patterns, < means a push to the left, while > means a push to the right. The pattern above means that the
 * jets will push a falling rock right, then right, then right, then left, then left, then right, and so on. If the end
 * of the list is reached, it repeats.
 * The tall, vertical chamber is exactly seven units wide. Each rock appears so that its left edge is two units away
 * from the left wall and its bottom edge is three units above the highest rock in the room (or the floor, if there
 * isn't one).
 * After a rock appears, it alternates between being pushed by a jet of hot gas one unit (in the direction indicated by
 * the next symbol in the jet pattern) and then falling one unit down. If any movement would cause any part of the rock
 * to move into the walls, floor, or a stopped rock, the movement instead does not occur. If a downward movement would
 * have caused a falling rock to move into the floor or an already-fallen rock, the falling rock stops where it is
 * (having landed on something) and a new rock immediately begins falling.
 * Drawing falling rocks with @ and stopped rocks with #, the jet pattern in the example above manifests as follows:
 * The first rock begins falling:
 * |..@@@@.|
 * |.......|
 * |.......|
 * |.......|
 * +-------+
 *
 * Jet of gas pushes rock right:
 * |...@@@@|
 * |.......|
 * |.......|
 * |.......|
 * +-------+
 *
 * Rock falls 1 unit:
 * |...@@@@|
 * |.......|
 * |.......|
 * +-------+
 *
 * Jet of gas pushes rock right, but nothing happens:
 * |...@@@@|
 * |.......|
 * |.......|
 * +-------+
 *
 * Rock falls 1 unit:
 * |...@@@@|
 * |.......|
 * +-------+
 *
 * Jet of gas pushes rock right, but nothing happens:
 * |...@@@@|
 * |.......|
 * +-------+
 *
 * Rock falls 1 unit:
 * |...@@@@|
 * +-------+
 *
 * Jet of gas pushes rock left:
 * |..@@@@.|
 * +-------+
 *
 * Rock falls 1 unit, causing it to come to rest:
 * |..####.|
 * +-------+
 *
 * A new rock begins falling:
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |.......|
 * |.......|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Jet of gas pushes rock left:
 * |..@....|
 * |.@@@...|
 * |..@....|
 * |.......|
 * |.......|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Rock falls 1 unit:
 * |..@....|
 * |.@@@...|
 * |..@....|
 * |.......|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Jet of gas pushes rock right:
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |.......|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Rock falls 1 unit:
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Jet of gas pushes rock left:
 * |..@....|
 * |.@@@...|
 * |..@....|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Rock falls 1 unit:
 * |..@....|
 * |.@@@...|
 * |..@....|
 * |..####.|
 * +-------+
 *
 * Jet of gas pushes rock right:
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |..####.|
 * +-------+
 *
 * Rock falls 1 unit, causing it to come to rest:
 * |...#...|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * A new rock begins falling:
 * |....@..|
 * |....@..|
 * |..@@@..|
 * |.......|
 * |.......|
 * |.......|
 * |...#...|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * The moment each of the next few rocks begins falling, you would see this:
 * |..@....|
 * |..@....|
 * |..@....|
 * |..@....|
 * |.......|
 * |.......|
 * |.......|
 * |..#....|
 * |..#....|
 * |####...|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@@...|
 * |..@@...|
 * |.......|
 * |.......|
 * |.......|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@@@@.|
 * |.......|
 * |.......|
 * |.......|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |.......|
 * |.......|
 * |.......|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |....@..|
 * |....@..|
 * |..@@@..|
 * |.......|
 * |.......|
 * |.......|
 * |..#....|
 * |.###...|
 * |..#....|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@....|
 * |..@....|
 * |..@....|
 * |..@....|
 * |.......|
 * |.......|
 * |.......|
 * |.....#.|
 * |.....#.|
 * |..####.|
 * |.###...|
 * |..#....|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@@...|
 * |..@@...|
 * |.......|
 * |.......|
 * |.......|
 * |....#..|
 * |....#..|
 * |....##.|
 * |....##.|
 * |..####.|
 * |.###...|
 * |..#....|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@@@@.|
 * |.......|
 * |.......|
 * |.......|
 * |....#..|
 * |....#..|
 * |....##.|
 * |##..##.|
 * |######.|
 * |.###...|
 * |..#....|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * To prove to the elephants your simulation is accurate, they want to know how tall the tower will get after 2022
 * rocks have stopped (but before the 2023rd rock begins falling). In this example, the tower of rocks will be 3068
 * units tall.
 * How many units tall will the tower of rocks be after 2022 rocks have stopped falling?
 *
 * --- Part Two ---
 * The elephants are not impressed by your simulation. They demand to know how tall the tower will be after
 * 1000000000000 rocks have stopped! Only then will they feel confident enough to proceed through the cave.
 * In the example above, the tower would be 1514285714288 units tall!
 * How tall will the tower be after 1000000000000 rocks have stopped?
 *
 */
class FallingRocks(private val jetDirections: List<Direction>) {
  constructor(input: String) : this(input.toList().map {
    when (it) {
      '<' -> Direction.W
      '>' -> Direction.E
      else -> throw IllegalArgumentException("Unrecognized jet direction $it")
    }
  })

  private fun chambers(): Sequence<FallingRockChamber> =
    jetDirections.asSequence()
      .repeatForever()
      .runningFold(FallingRockChamber()) { chamber, direction ->
        chamber.next(direction)
      }

  fun result(rocksLanded: Int) = chambers().first { it.rocksLanded == rocksLanded }

  // Returns a sequence of pairs (number of rocks dropped, height after rock dropped).
  private fun heightsByRock(): Sequence<Int> = sequenceOf(0) +
      chambers()
        .zipWithNext()
        .filter { it.second.rocksLanded > it.first.rocksLanded }
        .map { it.second.height() }

  fun calculateHeight(rocksLanded: Long) = heightsByRock()
    .findLinearRecurrence(minPeriod = 10)
    ?.get(rocksLanded)
}

private fun <T> Sequence<T>.repeatForever() = sequence { while (true) yieldAll(this@repeatForever) }

class FallingRockChamber(
  private val rockPoints: Set<Point> = emptySet(),
  private val currentRockShapeIndex: Int = 0,
  // The bottom left of the rock.
  // 0,0 is the bottom left empty space of the chamber, up against the left wall
  // and the floor.
  private val currentRockLocation: Point = Point(2, 3),
  val rocksLanded: Int = 0
) {
  private fun isAvailable(point: Point) = point.x in 0 until 7 &&
      point.y >= 0 &&
      point !in rockPoints

  fun height() = rockPoints.height()

  private fun Set<Point>.height() = (this.maxOfOrNull { it.y } ?: -1) + 1

  fun next(jetDirection: Direction): FallingRockChamber {
    val currentRockShape = FallingRockShape.values()[currentRockShapeIndex]
    val sidewaysPoints = currentRockShape.points.map { it + currentRockLocation + jetDirection.toPoint() }

    val rockLocationAfterSideways = if (sidewaysPoints.all(this::isAvailable)) {
      currentRockLocation + jetDirection.toPoint()
    } else {
      currentRockLocation
    }

    val downwardsPoints = currentRockShape.points.map { it + rockLocationAfterSideways + Point(0, -1) }
    return if (downwardsPoints.all(this::isAvailable)) {
      FallingRockChamber(rockPoints, currentRockShapeIndex, rockLocationAfterSideways + Point(0, -1), rocksLanded)
    } else {
      val currentRockRestingPoints = currentRockShape.points.map { it + rockLocationAfterSideways }.toSet()
      val newRockPoints = rockPoints + currentRockRestingPoints
      FallingRockChamber(
        rockPoints + newRockPoints,
        (currentRockShapeIndex + 1) % FallingRockShape.values().size,
        Point(2, newRockPoints.height() + 3),
        rocksLanded + 1
      )
    }
  }
}

// In order of falling.
enum class FallingRockShape(
  // Defined such that the bottom left is (0,0).
  val points: Set<Point>
) {
  MINUS(setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))),
  PLUS(setOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2))),
  ANGLE(setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2))),
  I(setOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3))),
  SQUARE(setOf(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1)))
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day17/input.txt").readText().trim()

  val rocks = FallingRocks(input)

  val result = rocks.result(2022)
  println(result.height())

  // This takes a couple minutes to find the linear recurrence
  // LinearRecurrence(firstIndex=390, period=1725, deltaPerCycle=2685)
  println(rocks.calculateHeight(1000000000000))
}