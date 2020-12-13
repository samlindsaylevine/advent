package advent.year2020.day13

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

fun main() {
  val timestamp = 1000186
  val busesString = "17,x,x,x,x,x,x,x,x,x,x,37,x,x,x,x,x,907,x,x,x,x,x,x,x,x,x,x,x,19,x,x,x,x,x,x,x,x,x,x,23,x,x,x,x,x,29,x,653,x,x,x,x,x,x,x,x,x,41,x,x,13"

  val buses = ShuttleBuses(busesString)
  println(buses.earliestProduct(timestamp))

}