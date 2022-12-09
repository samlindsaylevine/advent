package advent.year2022.day9

import advent.utils.Direction
import advent.utils.Point
import advent.utils.next
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign

class RopePath(
  val head: Point = Point(0, 0),
  val tail: Point = Point(0, 0),
  val tailVisited: Set<Point> = setOf(tail)
) {
  fun move(motions: List<String>) = motions.fold(this) { path, motion -> path.move(motion) }

  private fun move(motion: String): RopePath {
    val direction = motion.substringBefore(" ").toDirection()
    val amount = motion.substringAfter(" ").toInt()
    return this.next(amount) { it.move(direction) }
  }

  private fun move(direction: Direction): RopePath {
    val nextHead = head + direction.toPoint()
    val nextTail = nextTail(nextHead, tail)
    return RopePath(head = nextHead, tail = nextTail, tailVisited = this.tailVisited + nextTail)
  }

  private fun nextTail(head: Point, tail: Point): Point {
    val delta = head - tail
    val move = when {
      delta.x.absoluteValue <= 1 && delta.y.absoluteValue <= 1 -> Point(0, 0)
      delta.y == 0 -> Point(delta.x.sign, 0)
      delta.x == 0 -> Point(0, delta.y.sign)
      else -> Point(delta.x.sign, delta.y.sign)
    }
    return tail + move
  }

  private fun String.toDirection() = when (this) {
    "R" -> Direction.E
    "L" -> Direction.W
    "U" -> Direction.N
    "D" -> Direction.S
    else -> throw IllegalArgumentException("Unrecognized direction $this")
  }


}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day9/input.txt").readText().trim()
  val path = RopePath().move(input.lines())

  println(path.tailVisited.size)
}