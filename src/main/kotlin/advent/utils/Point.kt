package advent.utils

import advent.year2019.day10.gcd
import kotlin.math.abs
import kotlin.math.absoluteValue

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
 * Defines a range of points along a horizontal, vertical, or diagonal line.
 */
class PointRange(
  val start: Point,
  val endInclusive: Point
) : Iterable<Point> {
  override fun iterator() = PointRangeIterator(start, endInclusive)
}

/**
 * The brains of the [PointRange].
 */
class PointRangeIterator(start: Point, private val end: Point) : Iterator<Point> {
  private val step = (end - start).let { delta ->
    val gcd = gcd(delta.x.absoluteValue, delta.y.absoluteValue)
    if (gcd == 0) Point(0, 0) else delta / gcd
  }
  private var next: Point? = start

  override fun hasNext() = next != null

  override fun next(): Point {
    val output = next ?: throw NoSuchElementException("Walked past end of iterator")
    next = if (output == end) null else output + step
    return output
  }
}