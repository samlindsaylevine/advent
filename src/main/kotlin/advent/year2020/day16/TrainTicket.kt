package advent.year2020.day16

import java.io.File

/**
 * --- Day 16: Ticket Translation ---
 * As you're walking to yet another connecting flight, you realize that one of the legs of your re-routed trip coming
 * up is on a high-speed train. However, the train ticket you were given is in a language you don't understand. You
 * should probably figure out what it says before you get to the train station after the next flight.
 * Unfortunately, you can't actually read the words on the ticket. You can, however, read the numbers, and so you
 * figure out the fields these tickets must have and the valid ranges for values in those fields.
 * You collect the rules for ticket fields, the numbers on your ticket, and the numbers on other nearby tickets for the
 * same train service (via the airport security cameras) together into a single document you can reference (your puzzle
 * input).
 * The rules for ticket fields specify a list of fields that exist somewhere on the ticket and the valid ranges of
 * values for each field. For example, a rule like class: 1-3 or 5-7 means that one of the fields in every ticket is
 * named class and can be any value in the ranges 1-3 or 5-7 (inclusive, such that 3 and 5 are both valid in this
 * field, but 4 is not).
 * Each ticket is represented by a single line of comma-separated values. The values are the numbers on the ticket in
 * the order they appear; every ticket has the same format. For example, consider this ticket:
 * .--------------------------------------------------------.
 * | ????: 101    ?????: 102   ??????????: 103     ???: 104 |
 * |                                                        |
 * | ??: 301  ??: 302             ???????: 303      ??????? |
 * | ??: 401  ??: 402           ???? ????: 403    ????????? |
 * '--------------------------------------------------------'
 * 
 * Here, ? represents text in a language you don't understand. This ticket might be represented as
 * 101,102,103,104,301,302,303,401,402,403; of course, the actual train tickets you're looking at are much more
 * complicated. In any case, you've extracted just the numbers in such a way that the first number is always the same
 * specific field, the second number is always a different specific field, and so on - you just don't know what each
 * position actually means!
 * Start by determining which tickets are completely invalid; these are tickets that contain values which aren't valid
 * for any field. Ignore your ticket for now.
 * For example, suppose you have the following notes:
 * class: 1-3 or 5-7
 * row: 6-11 or 33-44
 * seat: 13-40 or 45-50
 * 
 * your ticket:
 * 7,1,14
 * 
 * nearby tickets:
 * 7,3,47
 * 40,4,50
 * 55,2,20
 * 38,6,12
 * 
 * It doesn't matter which position corresponds to which field; you can identify invalid nearby tickets by considering
 * only whether tickets contain values that are not valid for any field. In this example, the values on the first
 * nearby ticket are all valid for at least one field. This is not true of the other three nearby tickets: the values
 * 4, 55, and 12 are are not valid for any field. Adding together all of the invalid values produces your ticket
 * scanning error rate: 4 + 55 + 12 = 71.
 * Consider the validity of the nearby tickets you scanned. What is your ticket scanning error rate?
 * 
 * --- Part Two ---
 * Now that you've identified which tickets contain invalid values, discard those tickets entirely. Use the remaining
 * valid tickets to determine which field is which.
 * Using the valid ranges for each field, determine what order the fields appear on the tickets. The order is
 * consistent between all tickets: if seat is the third field, it is the third field on every ticket, including your
 * ticket.
 * For example, suppose you have the following notes:
 * class: 0-1 or 4-19
 * row: 0-5 or 8-19
 * seat: 0-13 or 16-19
 * 
 * your ticket:
 * 11,12,13
 * 
 * nearby tickets:
 * 3,9,18
 * 15,1,5
 * 5,14,9
 * 
 * Based on the nearby tickets in the above example, the first position must be row, the second position must be class,
 * and the third position must be seat; you can conclude that in your ticket, class is 12, row is 11, and seat is 13.
 * Once you work out which field is which, look for the six fields on your ticket that start with the word departure.
 * What do you get if you multiply those six values together?
 * 
 */
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