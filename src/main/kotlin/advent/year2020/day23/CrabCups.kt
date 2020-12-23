package advent.year2020.day23

class CrabCups(cupsList: List<Int>) {
  constructor(input: String) : this(parseToList(input))

  companion object {
    private fun parseToList(input: String) = input.split("")
            .filter { it.isNotEmpty() }
            .map { it.toInt() }

    fun extended(input: String): CrabCups {
      val original = parseToList(input)
      val additional = (original.size + 1)..1_000_000
      return CrabCups(original + additional)
    }
  }

  private val numCups = cupsList.size

  // A random-access list for looking up our linked list nodes by value.
  // These linked list nodes will be hooked up in the init method, in the proper (circular) order.
  // We originally used a built-in LinkedList, but that was too slow, particularly since we had to do O(N) lookups
  // for a particular cup. This lets us do inserts, removes, and lookups by value all as O(1) operations.
  private val cups = (1..numCups).map(::Cup)

  /**
   * Get the node for the Cup with the provided number.
   */
  private fun getCup(cupNumber: Int) = cups[cupNumber - 1]

  private var currentCup = getCup(cupsList.first())

  init {
    // Hook up our linked list.
    cupsList.zipWithNext().forEach { (first, second) -> getCup(first).next = getCup(second) }
    getCup(cupsList.last()).next = getCup(cupsList.first())
  }

  fun next() {
    val pickedUp = Triple(pickUp(), pickUp(), pickUp())

    val destinationNumber = destinationCupLabel(pickedUp.values())
    val destination = getCup(destinationNumber)

    place(destination, pickedUp)

    currentCup = currentCup.next
  }

  fun next(moves: Int) = repeat(moves) { next() }

  fun labels(): String = generateSequence(getCup(1)) { it.next }
          .drop(1)
          .map { it.value }
          .takeWhile { it != 1 }
          .joinToString(separator = "") { it.toString() }

  fun labelProduct(): Long {
    val cupOne = getCup(1)

    return cupOne.next.value.toLong() * cupOne.next.next.value.toLong()
  }

  private fun pickUp() = currentCup.takeNext()

  private fun place(destination: Cup, pickedUp: Triple<Cup, Cup, Cup>) {
    destination.addNext(pickedUp.third)
    destination.addNext(pickedUp.second)
    destination.addNext(pickedUp.first)
  }

  private fun destinationCupLabel(pickedUp: Set<Int>): Int {
    val target = currentCup.value - 1
    val possibilities = sequenceOf(
            wrap(target),
            wrap(target - 1),
            wrap(target - 2),
            wrap(target - 3)
    )

    return possibilities.first { !pickedUp.contains(it) }
  }

  private fun wrap(number: Int) = if (number < 1) {
    numCups + number
  } else {
    number
  }
}

/**
 * Linked list nodes.
 */
private class Cup(val value: Int) {
  var next: Cup = this

  fun takeNext(): Cup {
    val twoDown = this.next.next
    val output = this.next
    this.next = twoDown

    return output
  }

  fun addNext(cup: Cup) {
    val previousNext = this.next
    this.next = cup
    cup.next = previousNext
  }
}

private fun Triple<Cup, Cup, Cup>.values() = setOf(this.first.value, this.second.value, this.third.value)

fun main() {
  val input = "389547612"
  val cups = CrabCups(input)

  cups.next(100)
  println(cups.labels())

  val extended = CrabCups.extended(input)
  extended.next(10_000_000)
  println(extended.labelProduct())
}