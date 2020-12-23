package advent.year2020.day23

import java.util.*

class CrabCups(val cups: LinkedList<Int>,
               var currentIndex: Int = 0) {
  constructor(input: String) : this(input.split("")
          .filter { it.isNotEmpty() }
          .map { it.toInt() }
          .let(::LinkedList))

  private val minCup = cups.minOrNull() ?: throw IllegalStateException("Can't play cups with no cups")
  private val maxCup = cups.maxOrNull() ?: throw IllegalStateException("Can't play cups with no cups")

  override fun toString() = cups.mapIndexed { i, cup -> if (i == currentIndex) "($cup)" else "$cup" }
          .joinToString(" ")

  fun next() {
    val pickedUp = Triple(pickUp(), pickUp(), pickUp())

    place(pickedUp, destinationIndex(pickedUp))

    currentIndex = (currentIndex + 1) % cups.size
  }

  fun next(moves: Int) = repeat(moves) { next() }

  fun labels(): String {
    val oneIndex = cups.indexOf(1)

    val startingFromOne = cups.drop(oneIndex + 1) + cups.take(oneIndex)

    return startingFromOne.joinToString("")
  }

  private fun pickUp(): Int = if (currentIndex == cups.size - 1) {
    currentIndex--
    cups.removeFirst()
  } else {
    cups.removeAt(currentIndex + 1)
  }

  private fun place(pickedUp: Triple<Int, Int, Int>, index: Int) {
    if (index <= currentIndex) currentIndex = (currentIndex + 3)

    cups.add(index, pickedUp.third)
    cups.add(index, pickedUp.second)
    cups.add(index, pickedUp.first)
  }

  private fun destinationIndex(pickedUp: Triple<Int, Int, Int>) = cups.indexOf(destinationCupLabel(pickedUp.toSet())) + 1

  private fun destinationCupLabel(pickedUp: Set<Int>): Int {
    val target = cups[currentIndex] - 1
    val possibilities = sequenceOf(
            wrap(target),
            wrap(target - 1),
            wrap(target - 2),
            wrap(target - 3)
    )

    return possibilities.first { !pickedUp.contains(it) }
  }

  private fun wrap(number: Int) = if (number < minCup) {
    maxCup - (minCup - number - 1)
  } else {
    number
  }
}

private fun Triple<Int, Int, Int>.toSet() = setOf(this.first, this.second, this.third)

fun main() {
  val cups = CrabCups("389547612")

  cups.next(100)
  println(cups.labels())
}