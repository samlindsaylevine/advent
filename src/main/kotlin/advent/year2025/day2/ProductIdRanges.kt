package advent.year2025.day2

import advent.meta.readInput

class ProductIdRanges(val ranges: List<ProductIdRange>) {
  constructor(input: String) : this(input.trim().split(",").map(::ProductIdRange))

  fun invalidIdSum() = ranges.sumOf { it.invalidIdSum() }
}

class ProductIdRange(val first: Long, val second: Long) {
  constructor(input: String) : this(input.substringBefore("-").toLong(), input.substringAfter("-").toLong())

  fun invalidIdSum() = (first..second).filter { it.isInvalidId() }.sum()
}

fun Long.isInvalidId(): Boolean {
  val str = this.toString()
  if (str.length % 2 != 0) return false
  return str.take(str.length / 2) == str.drop(str.length / 2)
}

fun main() {
  val ranges = ProductIdRanges(readInput())

  println(ranges.invalidIdSum())
}