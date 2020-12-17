package advent.year2020.day16

import java.io.File

class TrainTicket(val values: List<Int>) {
  constructor(input: String) : this(input.split(",").map { it.toInt() })

  fun invalidValues(fields: List<TrainTicketField>) = values.filter { value -> fields.none { it.isValid(value) } }
}

class TrainTicketField(val name: String, val ranges: List<IntRange>) {
  constructor(input: String) : this(
          input.substringBefore(":"),
          input.substringAfter(": ")
                  .split(" or ")
                  .map { it.toIntRange() }
  )

  fun isValid(value: Int) = ranges.any { it.contains(value) }
}

class TrainTicketDocument(val rules: List<TrainTicketField>,
                          val myTicket: TrainTicket,
                          val nearbyTickets: List<TrainTicket>) {
  companion object {
    fun parse(input: String): TrainTicketDocument {
      val sections = input.split("\n\n")
      val rules = sections[0].lines().map(::TrainTicketField)
      val myTicket = sections[1].lines().last().let(::TrainTicket)
      val nearbyTickets = sections[2].lines().drop(1).map(::TrainTicket)

      return TrainTicketDocument(rules, myTicket, nearbyTickets)
    }
  }

  fun errorRate() = nearbyTickets.flatMap { it.invalidValues(rules) }
          .sum()
}

private fun String.toIntRange(delimiter: String = "-") =
        this.substringBefore(delimiter).toInt()..this.substringAfter(delimiter).toInt()

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day16/input.txt")
          .readText()
          .trim()

  val document = TrainTicketDocument.parse(input)

  println(document.errorRate())
}