package advent.utils

import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
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
}

operator fun Int.times(point: Point) = Point(this * point.x, this * point.y)