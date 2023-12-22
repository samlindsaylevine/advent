package advent.year2023.day22

import advent.utils.Point3D
import advent.utils.PointRange3D
import advent.utils.times
import java.io.File

class SandBricks(val bricks: List<SandBrick>) {
  constructor(input: String) : this(input.trim().lines().map(::SandBrick))

  fun settle() {
    val bricksByMinZ = bricks
            .map { brick -> brick to brick.points().minOf { it.z } }
            .sortedBy { (_, minZ) -> minZ }
    val occupied = bricks.flatMap { it.points() }.toMutableSet()
    bricksByMinZ.forEach { (brick, minZ) ->
      occupied.removeAll(brick.points())
      val amountToDrop = dropDistance(brick, minZ, occupied)
      brick.droppedBy = brick.droppedBy + amountToDrop
      occupied.addAll(brick.points())
    }
    pointsToBricks = calculatePointsToBricks()
  }

  private fun dropDistance(brick: SandBrick, minZ: Int, occupied: Set<Point3D>): Int =
          (1 until minZ).takeWhile { z ->
            brick.points().droppedBy(z).all { it !in occupied }
          }.lastOrNull() ?: 0

  private var pointsToBricks = calculatePointsToBricks()
  private fun calculatePointsToBricks() = bricks.flatMap { brick -> brick.points().map { it to brick } }
          .toMap()

  private fun adjacent(brick: SandBrick, amountBelow: Int): Set<SandBrick> =
          brick.points().droppedBy(amountBelow)
                  .mapNotNull { pointsToBricks[it] }
                  .filter { it != brick }
                  .toSet()

  private fun above(brick: SandBrick) = adjacent(brick, -1)
  private fun below(brick: SandBrick) = adjacent(brick, 1)

  fun removable(): Collection<SandBrick> = bricks.filter { brick -> above(brick).all { below(it).size > 1 } }

  fun cascadeCount(): Int {
    val graph = BrickGraph(bricks.associateWith { brick -> above(brick) })
    return bricks.sumOf { graph.countStrandedWithout(it) }
  }
}

// The directed graph, with the edges pointing from a brick to those that are directly above and resting on it.
data class BrickGraph(val brickToAbove: Map<SandBrick, Set<SandBrick>>) {
  private val grounded = brickToAbove.keys.filter { it.points().any { point -> point.z == 1 } }
  val size = brickToAbove.keys.size

  // The number of bricks that will fall without this one is all of them; except the bricks that are still reachable
  // from the ground without it; and without this brick itself.
  fun countStrandedWithout(brick: SandBrick): Int = size - countReachableWithout(brick) - 1

  /**
   * Counts how many bricks are reachable from the ground with this one brick missing.
   */
  private fun countReachableWithout(missingBrick: SandBrick): Int {
    // Do a traversal from the ground.
    val reached = mutableSetOf<SandBrick>()
    val pending = ArrayDeque<SandBrick>()
    pending.addAll(grounded.filter { it != missingBrick })
    while (pending.isNotEmpty()) {
      val next = pending.removeFirst()
      if (next !in reached) {
        reached.add(next)
        val aboveNext = brickToAbove[next] ?: emptySet()
        pending.addAll(aboveNext.filter { it != missingBrick })
      }
    }
    return reached.size
  }
}

data class SandBrick(val initial: PointRange3D, var droppedBy: Int = 0) {
  constructor(input: String) : this(PointRange3D(input.substringBefore("~").toPoint3D(),
          input.substringAfter("~").toPoint3D()))

  fun points() = initial.droppedBy(droppedBy).toSet()
}

private fun String.toPoint3D(): Point3D {
  val (x, y, z) = this.split(",")
  return Point3D(x.toInt(), y.toInt(), z.toInt())
}


private fun Iterable<Point3D>.droppedBy(amount: Int) = this.map { it - amount * Point3D(0, 0, 1) }

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day22/input.txt").readText().trim()
  val bricks = SandBricks(input)

  bricks.settle()
  println(bricks.removable().size)
  println(bricks.cascadeCount())
}