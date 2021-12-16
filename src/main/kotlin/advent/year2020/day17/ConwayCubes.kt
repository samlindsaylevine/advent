package advent.year2020.day17

import advent.utils.Point3D
import advent.year2018.day18.advance

/**
 * --- Day 17: Conway Cubes ---
 * As your flight slowly drifts through the sky, the Elves at the Mythical Information Bureau at the North Pole contact
 * you. They'd like some help debugging a malfunctioning experimental energy source aboard one of their super-secret
 * imaging satellites.
 * The experimental energy source is based on cutting-edge technology: a set of Conway Cubes contained in a pocket
 * dimension! When you hear it's having problems, you can't help but agree to take a look.
 * The pocket dimension contains an infinite 3-dimensional grid. At every integer 3-dimensional coordinate (x,y,z),
 * there exists a single cube which is either active or inactive.
 * In the initial state of the pocket dimension, almost all cubes start inactive. The only exception to this is a small
 * flat region of cubes (your puzzle input); the cubes in this region start in the specified active (#) or inactive (.)
 * state.
 * The energy source then proceeds to boot up by executing six cycles.
 * Each cube only ever considers its neighbors: any of the 26 other cubes where any of their coordinates differ by at
 * most 1. For example, given the cube at x=1,y=2,z=3, its neighbors include the cube at x=2,y=2,z=2, the cube at
 * x=0,y=2,z=3, and so on.
 * During a cycle, all cubes simultaneously change their state according to the following rules:
 * 
 * If a cube is active and exactly 2 or 3 of its neighbors are also active, the cube remains active. Otherwise, the
 * cube becomes inactive.
 * If a cube is inactive but exactly 3 of its neighbors are active, the cube becomes active. Otherwise, the cube
 * remains inactive.
 * 
 * The engineers responsible for this experimental energy source would like you to simulate the pocket dimension and
 * determine what the configuration of cubes should be at the end of the six-cycle boot process.
 * For example, consider the following initial state:
 * .#.
 * ..#
 * ###
 * 
 * Even though the pocket dimension is 3-dimensional, this initial state represents a small 2-dimensional slice of it.
 * (In particular, this initial state defines a 3x3x1 region of the 3-dimensional space.)
 * Simulating a few cycles from this initial state produces the following configurations, where the result of each
 * cycle is shown layer-by-layer at each given z coordinate (and the frame of view follows the active cells in each
 * cycle):
 * Before any cycles:
 * 
 * z=0
 * .#.
 * ..#
 * ###
 * 
 * 
 * After 1 cycle:
 * 
 * z=-1
 * #..
 * ..#
 * .#.
 * 
 * z=0
 * #.#
 * .##
 * .#.
 * 
 * z=1
 * #..
 * ..#
 * .#.
 * 
 * 
 * After 2 cycles:
 * 
 * z=-2
 * .....
 * .....
 * ..#..
 * .....
 * .....
 * 
 * z=-1
 * ..#..
 * .#..#
 * ....#
 * .#...
 * .....
 * 
 * z=0
 * ##...
 * ##...
 * #....
 * ....#
 * .###.
 * 
 * z=1
 * ..#..
 * .#..#
 * ....#
 * .#...
 * .....
 * 
 * z=2
 * .....
 * .....
 * ..#..
 * .....
 * .....
 * 
 * 
 * After 3 cycles:
 * 
 * z=-2
 * .......
 * .......
 * ..##...
 * ..###..
 * .......
 * .......
 * .......
 * 
 * z=-1
 * ..#....
 * ...#...
 * #......
 * .....##
 * .#...#.
 * ..#.#..
 * ...#...
 * 
 * z=0
 * ...#...
 * .......
 * #......
 * .......
 * .....##
 * .##.#..
 * ...#...
 * 
 * z=1
 * ..#....
 * ...#...
 * #......
 * .....##
 * .#...#.
 * ..#.#..
 * ...#...
 * 
 * z=2
 * .......
 * .......
 * ..##...
 * ..###..
 * .......
 * .......
 * .......
 * 
 * After the full six-cycle boot process completes, 112 cubes are left in the active state.
 * Starting with your given initial configuration, simulate six cycles. How many cubes are left in the active state
 * after the sixth cycle?
 * 
 * --- Part Two ---
 * For some reason, your simulated results don't match what the experimental energy source engineers expected.
 * Apparently, the pocket dimension actually has four spatial dimensions, not three.
 * The pocket dimension contains an infinite 4-dimensional grid. At every integer 4-dimensional coordinate (x,y,z,w),
 * there exists a single cube (really, a hypercube) which is still either active or inactive.
 * Each cube only ever considers its neighbors: any of the 80 other cubes where any of their coordinates differ by at
 * most 1. For example, given the cube at x=1,y=2,z=3,w=4, its neighbors include the cube at x=2,y=2,z=3,w=3, the cube
 * at x=0,y=2,z=3,w=4, and so on.
 * The initial state of the pocket dimension still consists of a small flat region of cubes. Furthermore, the same
 * rules for cycle updating still apply: during each cycle, consider the number of active neighbors of each cube.
 * For example, consider the same initial state as in the example above. Even though the pocket dimension is
 * 4-dimensional, this initial state represents a small 2-dimensional slice of it. (In particular, this initial state
 * defines a 3x3x1x1 region of the 4-dimensional space.)
 * Simulating a few cycles from this initial state produces the following configurations, where the result of each
 * cycle is shown layer-by-layer at each given z and w coordinate:
 * Before any cycles:
 * 
 * z=0, w=0
 * .#.
 * ..#
 * ###
 * 
 * 
 * After 1 cycle:
 * 
 * z=-1, w=-1
 * #..
 * ..#
 * .#.
 * 
 * z=0, w=-1
 * #..
 * ..#
 * .#.
 * 
 * z=1, w=-1
 * #..
 * ..#
 * .#.
 * 
 * z=-1, w=0
 * #..
 * ..#
 * .#.
 * 
 * z=0, w=0
 * #.#
 * .##
 * .#.
 * 
 * z=1, w=0
 * #..
 * ..#
 * .#.
 * 
 * z=-1, w=1
 * #..
 * ..#
 * .#.
 * 
 * z=0, w=1
 * #..
 * ..#
 * .#.
 * 
 * z=1, w=1
 * #..
 * ..#
 * .#.
 * 
 * 
 * After 2 cycles:
 * 
 * z=-2, w=-2
 * .....
 * .....
 * ..#..
 * .....
 * .....
 * 
 * z=-1, w=-2
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=0, w=-2
 * ###..
 * ##.##
 * #...#
 * .#..#
 * .###.
 * 
 * z=1, w=-2
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=2, w=-2
 * .....
 * .....
 * ..#..
 * .....
 * .....
 * 
 * z=-2, w=-1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=-1, w=-1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=0, w=-1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=1, w=-1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=2, w=-1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=-2, w=0
 * ###..
 * ##.##
 * #...#
 * .#..#
 * .###.
 * 
 * z=-1, w=0
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=0, w=0
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=1, w=0
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=2, w=0
 * ###..
 * ##.##
 * #...#
 * .#..#
 * .###.
 * 
 * z=-2, w=1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=-1, w=1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=0, w=1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=1, w=1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=2, w=1
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=-2, w=2
 * .....
 * .....
 * ..#..
 * .....
 * .....
 * 
 * z=-1, w=2
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=0, w=2
 * ###..
 * ##.##
 * #...#
 * .#..#
 * .###.
 * 
 * z=1, w=2
 * .....
 * .....
 * .....
 * .....
 * .....
 * 
 * z=2, w=2
 * .....
 * .....
 * ..#..
 * .....
 * .....
 * 
 * After the full six-cycle boot process completes, 848 cubes are left in the active state.
 * Starting with your given initial configuration, simulate six cycles in a 4-dimensional space. How many cubes are
 * left in the active state after the sixth cycle?
 * 
 */
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