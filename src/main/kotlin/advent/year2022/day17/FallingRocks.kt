package advent.year2022.day17

import advent.utils.Direction
import advent.utils.Point
import java.io.File

class FallingRocks(private val jetDirections: List<Direction>) {
  constructor(input: String) : this(input.toList().map {
    when (it) {
      '<' -> Direction.W
      '>' -> Direction.E
      else -> throw IllegalArgumentException("Unrecognized jet direction $it")
    }
  })

  fun chambers(): Sequence<FallingRockChamber> =
    jetDirections.asSequence()
      .repeatForever()
      .runningFold(FallingRockChamber()) { chamber, direction ->
        chamber.next(direction)
      }

  fun result(rocksLanded: Int) = chambers().first { it.rocksLanded == rocksLanded }
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
}