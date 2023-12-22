package advent.utils

import kotlin.math.abs
import kotlin.math.sign

data class Point3D(val x: Int, val y: Int, val z: Int) {
  fun distanceTo(other: Point3D) = abs(this.x - other.x) +
          abs(this.y - other.y) +
          abs(this.z - other.z)

  /**
   * Including neighbors that are "diagonal".
   */
  val allNeighbors by lazy {
    ((x - 1)..(x + 1)).flatMap { x ->
      ((y - 1)..(y + 1)).flatMap { y ->
        ((z - 1)..(z + 1)).map { z -> Point3D(x, y, z) }
      }
    }.filter { it != this }
  }

  val orthogonallyAdjacent by lazy {
    setOf(
            Point3D(x - 1, y, z),
            Point3D(x + 1, y, z),
            Point3D(x, y - 1, z),
            Point3D(x, y + 1, z),
            Point3D(x, y, z - 1),
            Point3D(x, y, z + 1)
    )
  }

  operator fun plus(other: Point3D) = Point3D(this.x + other.x, this.y + other.y, this.z + other.z)
  operator fun minus(other: Point3D) = Point3D(this.x - other.x, this.y - other.y, this.z - other.z)

  /**
   * Manhattan distance.
   */
  fun distanceFrom(other: Point3D) = abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
}

operator fun Int.times(point: Point3D) = Point3D(this * point.x, this * point.y, this * point.z)

/**
 * Like PointRange; currently only handles orthogonally offset starts and ends.
 */
data class PointRange3D(val start: Point3D, val endInclusive: Point3D) : Iterable<Point3D> {
  override fun iterator() = Point3DRangeIterator(start, endInclusive)
}

class Point3DRangeIterator(start: Point3D, private val end: Point3D) : Iterator<Point3D> {
  private val step = (end - start).let { delta ->
    Point3D(delta.x.sign, delta.y.sign, delta.z.sign)
  }
  private var next: Point3D? = start
  override fun hasNext() = next != null
  override fun next(): Point3D {
    val output = next ?: throw NoSuchElementException("Walked past end of iterator")
    next = if (output == end) null else output + step
    return output
  }
}