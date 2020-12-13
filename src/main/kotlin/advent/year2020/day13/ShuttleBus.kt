package advent.year2020.day13

import advent.utils.ModularConstraint
import advent.utils.chineseRemainderSolution
import advent.utils.multiplicativeInverse

class ShuttleBus(val id: Int) {
  fun earliestArrivalAfter(timestamp: Int) = ((timestamp / id) + 1) * id
}

class ShuttleBuses(val buses: List<ShuttleBus>) {
  constructor(input: String) : this(input
          .split(",")
          .filter { it != "x" }
          .map { ShuttleBus(it.toInt()) })

  fun earliestProduct(timestamp: Int) = buses.minByOrNull { it.earliestArrivalAfter(timestamp) }
          ?.let { (it.earliestArrivalAfter(timestamp) - timestamp) * it.id }
}

class DepartureTimes(val busToOffset: Map<Long, Long>) {
  constructor(input: String) : this(
          input.split(",")
                  .mapIndexedNotNull { index, busId ->
                    if (busId == "x") null else (busId.toLong() to index.toLong())
                  }
                  .toMap())

  /**
   * This is a number theory problem. We want to satisfy the simultaneous set of equations
   *
   * x + offset_n === 0 mod busId_n
   *
   * The Chinese Remainder Theorem states that there is a unique solution in mod (product of all busIds), assuming that
   * the busIds are relatively coprime... and in both our input, and all the examples, all of the busIds are prime.
   *
   * We can solve the family of equations by recursively solving the first pair of equations, and turning it into a
   * single equation in the modulo that is the product of the individual ones.
   */
  fun earliest() = chineseRemainderSolution(busToOffset.entries
          .map {
            val output = ModularConstraint(Math.floorMod(-it.value, it.key), it.key)
            println(output)
            output
          })
}

fun main() {
  val timestamp = 1000186
  val busesString = "17,x,x,x,x,x,x,x,x,x,x,37,x,x,x,x,x,907,x,x,x,x,x,x,x,x,x,x,x,19,x,x,x,x,x,x,x,x,x,x,23,x,x,x,x,x,29,x,653,x,x,x,x,x,x,x,x,x,41,x,x,13"

  val buses = ShuttleBuses(busesString)
  println(buses.earliestProduct(timestamp))

  val departures = DepartureTimes(busesString)
  println(departures.earliest())
}