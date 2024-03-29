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

// Helper for converting geographic maps (from Point to T) into a string.
// We put (minX, minY) at the upper left, and Y increases down, and X increases to the right.
fun <T> Map<Point, T>.visualized(transform: (T) -> String): String {
  val minX = keys.minOf { it.x }
  val maxX = keys.maxOf { it.x }
  val minY = keys.minOf { it.y }
  val maxY = keys.maxOf { it.y }

  return (minY..maxY).joinToString("\n") { y ->
    (minX..maxX).joinToString("") { x -> this[Point(x, y)]?.let(transform) ?: " " }
  }
}

fun Set<Point>.visualized(pointCharacter: Char): String {
  val map = this.groupBy { it }
  return map.visualized { pointCharacter.toString() }
}

/**
 * Defines a range of points along a line segment. The line segment may be horizontal, vertical, or (any degree of)
 * diagonal; if diagonal, only integer-coordinate-value points along that line segment are part of the range.
 *
 * For example, the range from (0,0) to (6,4) contains (0,0), (3,2), and (6,4).
 *
 * If start and end are the same point, the range contains only one point, not two (i.e., points are never repeated).
 */
class PointRange(
  val start: Point,
  val endInclusive: Point
) : Iterable<Point> {
  override fun iterator() = PointRangeIterator(start, endInclusive)

  override fun toString() = "$start .. $endInclusive"
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