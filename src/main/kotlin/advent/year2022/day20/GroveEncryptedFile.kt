package advent.year2022.day20

import advent.utils.next
import java.io.File

class GroveEncryptedFile private constructor(
  private val size: Int,
  private var firstNode: GroveNode
) {
  companion object {
    fun parse(input: String): GroveEncryptedFile = from(input.lines().map { it.toInt() })

    fun from(numbers: List<Int>): GroveEncryptedFile {
      val nodesList = numbers.map { GroveNode(value = it, previous = null, next = null) }
      nodesList.zipWithNext().forEach { (first, second) ->
        first.next = second
        second.previous = first
      }
      nodesList.last().next = nodesList.first()
      nodesList.first().previous = nodesList.last()
      return GroveEncryptedFile(nodesList.size, nodesList.first())
    }

    fun from(vararg numbers: Int) = from(numbers.toList())
  }

  private fun nodesToMove(): List<GroveNode> = listOf(firstNode) + generateSequence(firstNode) { it.next() }
    .drop(1)
    .takeWhile { it != firstNode }
    .toList()

  fun numbers(): List<Int> = nodesToMove().map { it.value }

  fun mix() = nodesToMove().forEach(this::move)

  private fun move(node: GroveNode) {
    val steps = Math.floorMod(node.value, this.size - 1)

    if (steps == 0) return

    val newPrevious = node.next(steps, GroveNode::next)
    val originalPrevious = node.previous()
    val originalNext = node.next()
    originalPrevious.next = originalNext
    originalNext.previous = originalPrevious

    val newNext = newPrevious.next()
    newPrevious.next = node
    node.previous = newPrevious
    newNext.previous = node
    node.next = newNext

    when {
      node == firstNode && steps > 0 -> firstNode = originalNext
      newNext == firstNode && node.value > 0 -> firstNode = node
    }
  }

  fun groveCoordinates(): Int {
    val zeroNode = nodesToMove().first { it.value == 0 }
    return (zeroNode.next(1000) { it.next() }.value +
        zeroNode.next(2000) { it.next() }.value +
        zeroNode.next(3000) { it.next() }.value)
  }
}

private class GroveNode(
  val value: Int,
  var next: GroveNode?,
  var previous: GroveNode?
) {
  fun next() = next ?: throw IllegalStateException("Missing next link for node $value")
  fun previous() = previous ?: throw IllegalStateException("Missing previous link for node $value")

  override fun toString() = "GroveNode[value=$value]"
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day20/input.txt").readText().trim()

  val file = GroveEncryptedFile.parse(input)

  file.mix()
  println(file.groveCoordinates())
}
