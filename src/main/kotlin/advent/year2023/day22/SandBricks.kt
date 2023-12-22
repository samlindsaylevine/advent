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
  }

  private fun dropDistance(brick: SandBrick, minZ: Int, occupied: Set<Point3D>): Int =
          (1 until minZ).takeWhile { z ->
            brick.points().droppedBy(z).all { it !in occupied }
          }.lastOrNull() ?: 0

  fun removable(): Collection<SandBrick> {
    val pointsToBricks = bricks.flatMap { brick -> brick.points().map { it to brick } }
            .toMap()

    fun adjacent(brick: SandBrick, amountBelow: Int): Set<SandBrick> =
            brick.points().droppedBy(amountBelow)
                    .mapNotNull { pointsToBricks[it] }
                    .filter { it != brick }
                    .toSet()

    fun above(brick: SandBrick) = adjacent(brick, -1)
    fun below(brick: SandBrick) = adjacent(brick, 1)

    return bricks.filter { brick -> above(brick).all { below(it).size > 1 } }
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
}