package advent.year2020.day17

import advent.utils.Point3D
import advent.year2018.day18.advance

class ConwayCubes(val active: Set<Point3D>) {
  constructor(input: String) : this(input.lines()
          .flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, char -> if (char == '#') Point3D(x, y, 0) else null }
          }.toSet())

  fun next(): ConwayCubes {
    if (active.isEmpty()) return this

    val xRange = active.range(Point3D::x)
    val yRange = active.range(Point3D::y)
    val zRange = active.range(Point3D::z)

    val nextSet = xRange.flatMap { x ->
      yRange.flatMap { y ->
        zRange.map { z -> Point3D(x, y, z) }
                .filter(::isActiveInNext)
      }
    }.toSet()

    return ConwayCubes(nextSet)
  }

  private fun isActiveInNext(point: Point3D): Boolean {
    val neighborCount = point.allNeighbors.count { it in active }
    return (point in active && (neighborCount == 2 || neighborCount == 3)) ||
            (point !in active && neighborCount == 3)
  }

  // Finds a range that goes one beyond anything in this set of points.
  private fun Set<Point3D>.range(coordinate: (Point3D) -> Int) =
          this.minOf { coordinate(it) - 1 }..this.maxOf { coordinate(it) + 1 }
}

fun main() {
  val cubes = ConwayCubes("""
    ######.#
    #.###.#.
    ###.....
    #.####..
    ##.#.###
    .######.
    ###.####
    ######.#
  """.trimIndent())

  val finalState = advance(6, cubes, ConwayCubes::next)
  println(finalState.active.size)
}