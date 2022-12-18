package advent.year2022.day18

import advent.utils.Point3D
import java.io.File

/**
 * --- Day 18: Boiling Boulders ---
 * You and the elephants finally reach fresh air. You've emerged near the base of a large volcano that seems to be
 * actively erupting! Fortunately, the lava seems to be flowing away from you and toward the ocean.
 * Bits of lava are still being ejected toward you, so you're sheltering in the cavern exit a little longer. Outside
 * the cave, you can see the lava landing in a pond and hear it loudly hissing as it solidifies.
 * Depending on the specific compounds in the lava and speed at which it cools, it might be forming obsidian! The
 * cooling rate should be based on the surface area of the lava droplets, so you take a quick scan of a droplet as it
 * flies past you (your puzzle input).
 * Because of how quickly the lava is moving, the scan isn't very good; its resolution is quite low and, as a result,
 * it approximates the shape of the lava droplet with 1x1x1 cubes on a 3D grid, each given as its x,y,z position.
 * To approximate the surface area, count the number of sides of each cube that are not immediately connected to
 * another cube. So, if your scan were only two adjacent cubes like 1,1,1 and 2,1,1, each cube would have a single side
 * covered and five sides exposed, a total surface area of 10 sides.
 * Here's a larger example:
 * 2,2,2
 * 1,2,2
 * 3,2,2
 * 2,1,2
 * 2,3,2
 * 2,2,1
 * 2,2,3
 * 2,2,4
 * 2,2,6
 * 1,2,5
 * 3,2,5
 * 2,1,5
 * 2,3,5
 *
 * In the above example, after counting up all the sides that aren't connected to another cube, the total surface area
 * is 64.
 * What is the surface area of your scanned lava droplet?
 *
 * --- Part Two ---
 * Something seems off about your calculation. The cooling rate depends on exterior surface area, but your calculation
 * also included the surface area of air pockets trapped in the lava droplet.
 * Instead, consider only cube sides that could be reached by the water and steam as the lava droplet tumbles into the
 * pond. The steam will expand to reach as much as possible, completely displacing any air on the outside of the lava
 * droplet but never expanding diagonally.
 * In the larger example above, exactly one cube of air is trapped within the lava droplet (at 2,2,5), so the exterior
 * surface area of the lava droplet is 58.
 * What is the exterior surface area of your scanned lava droplet?
 *
 */
class LavaDroplets(val points: Set<Point3D>) {
  constructor(input: String) : this(input.lines().map { line ->
    val (x, y, z) = line.split(",")
    Point3D(x.toInt(), y.toInt(), z.toInt())
  }.toSet())

  fun surfaceArea() = points.surfaceArea()
  fun externalSurfaceArea() = filledIn().surfaceArea()

  private fun Set<Point3D>.surfaceArea() =
    this.sumOf { it.orthogonallyAdjacent.count { neighbor -> neighbor !in this } }

  /**
   * Returns those points not reachable from infinity, either because they are droplets or air pockets.
   */
  private fun filledIn(): Set<Point3D> {
    val minX = points.minOf { it.x }
    val maxX = points.maxOf { it.x }
    val minY = points.minOf { it.y }
    val maxY = points.maxOf { it.y }
    val minZ = points.minOf { it.z }
    val maxZ = points.maxOf { it.z }

    val boundingBox = (minX - 1..maxX + 1).flatMap { x ->
      (minY - 1..maxY + 1).flatMap { y ->
        (minZ - 1..maxZ + 1).map { z -> Point3D(x, y, z) }
      }
    }.toSet()

    val exteriorAir = explore(boundingBox.first()) { point ->
      point.orthogonallyAdjacent.filter { it in boundingBox && it !in points }.toSet()
    }

    return boundingBox - exteriorAir
  }

  private fun <T> explore(start: T, next: (T) -> Set<T>): Set<T> =
    explore(setOf(start), emptySet(), next)

  private tailrec fun <T> explore(
    current: Set<T>,
    visited: Set<T>,
    next: (T) -> Set<T>
  ): Set<T> {
    if (current.isEmpty()) return visited

    val nextStates = current.flatMap { next(it) }
      .filter { it !in visited }
      .toSet()

    return explore(nextStates, visited + current, next)
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day18/input.txt").readText().trim()

  val droplets = LavaDroplets(input)
  println(droplets.surfaceArea())
  println(droplets.externalSurfaceArea())
}