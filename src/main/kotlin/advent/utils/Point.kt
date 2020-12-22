package advent.utils

import kotlin.math.abs
import kotlin.math.sign

data class Point(val x: Int, val y: Int) : Comparable<Point> {
  operator fun plus(other: Point) = Point(this.x + other.x, this.y + other.y)
  operator fun minus(other: Point) = Point(this.x - other.x, this.y - other.y)
  operator fun div(divisor: Int) = Point(this.x / divisor, this.y / divisor)

  /**
   * Manhattan distance.
   */
  fun distanceFrom(other: Point) = abs(this.x - other.x) + abs(this.y - other.y)

  val adjacentNeighbors by lazy {
    sequenceOf(Point(-1, 0), Point(0, 1), Point(1, 0), Point(0, -1))
            .map { it + this }
            .toSet()
  }

  val eightNeighbors by lazy {
    (this.x - 1..this.x + 1).flatMap { x ->
      (this.y - 1..this.y + 1).map { y -> Point(x, y) }
    }.toSet() - this
  }

  override fun compareTo(other: Point) = this.distanceFrom(Point(0, 0))
          .compareTo(other.distanceFrom(Point(0, 0)))

  infix operator fun rangeTo(other: Point) = PointRange(this, other)
}

operator fun Int.times(point: Point) = Point(this * point.x, this * point.y)

/**
 * Defines a range of points along a horizontal or vertical line.
 */
class PointRange(private val start: Point,
                 private val endInclusive: Point) : Iterable<Point> {
  init {
    require(start.x == endInclusive.x || start.y == endInclusive.y) {
      "Start $start and end $endInclusive must be horizontally or vertically aligned"
    }
  }

  override fun iterator() = PointRangeIterator(start, endInclusive)
}

/**
 * The brains of the [PointRange].
 */
class PointRangeIterator(private val start: Point, private val end: Point) : Iterator<Point> {
  private var next: Point? = start

  override fun hasNext() = next != null

  override fun next(): Point {
    val output = next ?: throw NoSuchElementException("Walked past end of iterator")

    next = when {
      output == end -> null
      output.x == end.x -> Point(output.x, output.y + (end.y - output.y).sign)
      output.y == end.y -> Point(output.x + (end.x - output.x).sign, output.y)
      else -> throw IllegalStateException("Start $start and end $end aren't in a straight line")
    }

    return output
  }
}