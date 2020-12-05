package advent.year2020.day5

import java.io.File

class BinaryBoardingPass(val text: String) {

  val row: Int = text.take(7).fold(0..127) { range, char ->
    when (char) {
      'F' -> range.lowerHalf()
      'B' -> range.upperHalf()
      else -> throw IllegalArgumentException("Bad character $char")
    }
  }.uniqueValue()

  val column: Int = text.takeLast(3).fold(0..7) { range, char ->
    when (char) {
      'L' -> range.lowerHalf()
      'R' -> range.upperHalf()
      else -> throw IllegalArgumentException("Bad character $char")
    }
  }.uniqueValue()

  val seatId = row * 8 + column
}

fun IntRange.lowerHalf() = this.first..((this.first + this.last - 1) / 2)
fun IntRange.upperHalf() = ((this.first + this.last + 1) / 2)..this.last
fun IntRange.uniqueValue(): Int = if (this.first == this.last) this.first else
  throw IllegalStateException("$this has more than one value!")

fun List<BinaryBoardingPass>.missingSeat(): Int {
  val min = this.minOf { it.seatId }
  val max = this.maxOf { it.seatId }

  val seatIds = this.map { it.seatId }.toSet()

  return (min..max).first { !seatIds.contains(it) }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day5/input.txt")
          .readText()
          .trim()

  val passes = input.lines().map(::BinaryBoardingPass)

  println(passes.maxOf { it.seatId })
  println(passes.missingSeat())
}