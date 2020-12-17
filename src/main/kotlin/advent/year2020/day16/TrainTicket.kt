package advent.year2020.day16

import java.io.File

class TrainTicket(val values: List<Int>) {
  constructor(input: String) : this(input.split(",").map { it.toInt() })

  fun invalidValues(fields: List<TrainTicketField>) = values.filter { value -> fields.none { it.isValid(value) } }

  fun passes(rule: TrainTicketField, index: Int) = rule.isValid(values[index])
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

class TrainTicketDocument(private val rules: List<TrainTicketField>,
                          private val myTicket: TrainTicket,
                          private val nearbyTickets: List<TrainTicket>) {
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

  val validNearbyTickets by lazy {
    nearbyTickets.filter { it.invalidValues(rules).isEmpty() }
  }

  private val ruleValidityCache = mutableMapOf<Int, List<TrainTicketField>>()

  private fun validRulesAtPosition(position: Int) = ruleValidityCache.getOrPut(position) {
    rules.filter { rule -> validNearbyTickets.all { it.passes(rule, position) } }
  }

  val correctRuleOrdering: List<TrainTicketField> by lazy {
    findOrdering(emptyMap(), rules.toSet()) ?: throw IllegalStateException("No legal arrangement of rules")
  }

  private fun findOrdering(partialOrdering: Map<Int, TrainTicketField>,
                           remainingRules: Set<TrainTicketField>): List<TrainTicketField>? {
    if (remainingRules.isEmpty()) return rules.indices
            .map { partialOrdering[it] ?: throw IllegalStateException("Missing rule $it") }

    val possibilities = rules.indices
            .filter { it !in partialOrdering }
            .associateWith { (validRulesAtPosition(it) intersect remainingRules) }

    // As a speed up optimization, consider as our next index the one with the fewest options.
    val next = possibilities.minByOrNull { it.value.size }
            ?: return null

    return next.value.asSequence()
            .map { possibleNext ->
              findOrdering(partialOrdering + (next.key to possibleNext),
                      remainingRules - possibleNext)
            }
            .filterNotNull()
            .firstOrNull()
  }

  val myTicketAsMap by lazy {
    correctRuleOrdering.mapIndexed { index, rule -> rule.name to myTicket.values[index] }
            .toMap()
  }

  val productOfDepartureFields by lazy {
    myTicketAsMap.entries.filter { it.key.startsWith("departure") }
            .map { it.value.toLong() }
            .reduce(Long::times)
  }
}

private fun String.toIntRange(delimiter: String = "-") =
        this.substringBefore(delimiter).toInt()..this.substringAfter(delimiter).toInt()

fun main() {
  val input = File("src/main/kotlin/advent/year2020/day16/input.txt")
          .readText()
          .trim()

  val document = TrainTicketDocument.parse(input)

  println(document.errorRate())
  println(document.productOfDepartureFields)
}