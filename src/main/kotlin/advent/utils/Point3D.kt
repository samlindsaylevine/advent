package advent.utils

import kotlin.math.abs

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

  operator fun plus(other: Point3D) = Point3D(this.x + other.x, this.y + other.y, this.z + other.z)
  operator fun minus(other: Point3D) = Point3D(this.x - other.x, this.y - other.y, this.z - other.z)

  /**
   * Manhattan distance.
   */
  fun distanceFrom(other: Point3D) = abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
}