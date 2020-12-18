package advent.year2020.day17

import advent.utils.Point3D
import advent.year2018.day18.advance

abstract class ConwayGame<P>(val active: Set<P>) {
  /**
   * Return a bounding box 1 larger in each dimension than the provided points.
   */
  abstract fun expandedBoundingBox(points: Set<P>): Set<P>

  abstract fun neighbors(point: P): List<P>

  protected fun nextSet(): Set<P> {
    if (active.isEmpty()) return active

    return expandedBoundingBox(active).filter(::isActiveInNext).toSet()
  }

  private fun isActiveInNext(point: P): Boolean {
    val neighborCount = neighbors(point).count { it in active }
    return (point in active && (neighborCount == 2 || neighborCount == 3)) ||
            (point !in active && neighborCount == 3)
  }
}

private fun <P> parse(input: String,
                      pointFromInput: (Int, Int) -> P): Set<P> = input.lines()
        .flatMapIndexed { y, line ->
          line.mapIndexedNotNull { x, char -> if (char == '#') pointFromInput(x, y) else null }
        }.toSet()

// Finds a range that goes one beyond anything in this set of points.
private fun <P> Set<P>.range(coordinate: (P) -> Int) =
        this.minOf { coordinate(it) - 1 }..this.maxOf { coordinate(it) + 1 }

class ConwayCubes(active: Set<Point3D>) : ConwayGame<Point3D>(active) {
  constructor(input: String) : this(parse(input) { x, y -> Point3D(x, y, 0) })

  override fun expandedBoundingBox(points: Set<Point3D>) =
          active.range(Point3D::x).flatMap { x ->
            active.range(Point3D::y).flatMap { y ->
              active.range(Point3D::z).map { z -> Point3D(x, y, z) }
            }
          }.toSet()

  override fun neighbors(point: Point3D) = point.allNeighbors

  fun next() = ConwayCubes(nextSet())
}

class ConwayHypercubes(active: Set<Point4D>) : ConwayGame<Point4D>(active) {
  constructor(input: String) : this(parse(input) { x, y -> Point4D(x, y, 0, 0) })

  override fun expandedBoundingBox(points: Set<Point4D>) =
          active.range(Point4D::x).flatMap { x ->
            active.range(Point4D::y).flatMap { y ->
              active.range(Point4D::z).flatMap { z ->
                active.range(Point4D::w).map { w -> Point4D(x, y, z, w) }
              }
            }
          }.toSet()

  override fun neighbors(point: Point4D) = point.allNeighbors

  fun next() = ConwayHypercubes(nextSet())
}

data class Point4D(val x: Int, val y: Int, val z: Int, val w: Int) {
  /**
   * Including neighbors that are "diagonal".
   */
  val allNeighbors by lazy {
    ((x - 1)..(x + 1)).flatMap { x ->
      ((y - 1)..(y + 1)).flatMap { y ->
        ((z - 1)..(z + 1)).flatMap { z ->
          ((w - 1)..(w + 1)).map { w -> Point4D(x, y, z, w) }
        }
      }
    }.filter { it != this }
  }
}

fun main() {
  val input = """
    ######.#
    #.###.#.
    ###.....
    #.####..
    ##.#.###
    .######.
    ###.####
    ######.#
  """.trimIndent()

  val cubes = ConwayCubes(input)
  val finalCubes = advance(6, cubes, ConwayCubes::next)
  println(finalCubes.active.size)

  val hypercubes = ConwayHypercubes(input)
  val finalHypercubes = advance(6, hypercubes, ConwayHypercubes::next)
  println(finalHypercubes.active.size)
}