package advent.year2023.day19

import advent.year2020.day14.substringBetween
import java.io.File

/**
 * --- Day 19: Aplenty ---
 * The Elves of Gear Island are thankful for your help and send you on your way. They even have a hang glider that
 * someone stole from Desert Island; since you're already going that direction, it would help them a lot if you would
 * use it to get down there and return it to them.
 * As you reach the bottom of the relentless avalanche of machine parts, you discover that they're already forming a
 * formidable heap. Don't worry, though - a group of Elves is already here organizing the parts, and they have a system.
 * To start, each part is rated in each of four categories:
 *
 * x: Extremely cool looking
 * m: Musical (it makes a noise when you hit it)
 * a: Aerodynamic
 * s: Shiny
 *
 * Then, each part is sent through a series of workflows that will ultimately accept or reject the part. Each workflow
 * has a name and contains a list of rules; each rule specifies a condition and where to send the part if the condition
 * is true. The first rule that matches the part being considered is applied immediately, and the part moves on to the
 * destination described by the rule. (The last rule in each workflow has no condition and always applies if reached.)
 * Consider the workflow ex{x>10:one,m<20:two,a>30:R,A}. This workflow is named ex and contains four rules. If workflow
 * ex were considering a specific part, it would perform the following steps in order:
 *
 * Rule "x>10:one": If the part's x is more than 10, send the part to the workflow named one.
 * Rule "m<20:two": Otherwise, if the part's m is less than 20, send the part to the workflow named two.
 * Rule "a>30:R": Otherwise, if the part's a is more than 30, the part is immediately rejected (R).
 * Rule "A": Otherwise, because no other rules matched the part, the part is immediately accepted (A).
 *
 * If a part is sent to another workflow, it immediately switches to the start of that workflow instead and never
 * returns. If a part is accepted (sent to A) or rejected (sent to R), the part immediately stops any further
 * processing.
 * The system works, but it's not keeping up with the torrent of weird metal shapes. The Elves ask if you can help sort
 * a few parts and give you the list of workflows and some part ratings (your puzzle input). For example:
 * px{a<2006:qkq,m>2090:A,rfg}
 * pv{a>1716:R,A}
 * lnx{m>1548:A,A}
 * rfg{s<537:gd,x>2440:R,A}
 * qs{s>3448:A,lnx}
 * qkq{x<1416:A,crn}
 * crn{x>2662:A,R}
 * in{s<1351:px,qqz}
 * qqz{s>2770:qs,m<1801:hdj,R}
 * gd{a>3333:R,R}
 * hdj{m>838:A,pv}
 *
 * {x=787,m=2655,a=1222,s=2876}
 * {x=1679,m=44,a=2067,s=496}
 * {x=2036,m=264,a=79,s=2244}
 * {x=2461,m=1339,a=466,s=291}
 * {x=2127,m=1623,a=2188,s=1013}
 *
 * The workflows are listed first, followed by a blank line, then the ratings of the parts the Elves would like you to
 * sort. All parts begin in the workflow named in. In this example, the five listed parts go through the following
 * workflows:
 *
 * {x=787,m=2655,a=1222,s=2876}: in -> qqz -> qs -> lnx -> A
 * {x=1679,m=44,a=2067,s=496}: in -> px -> rfg -> gd -> R
 * {x=2036,m=264,a=79,s=2244}: in -> qqz -> hdj -> pv -> A
 * {x=2461,m=1339,a=466,s=291}: in -> px -> qkq -> crn -> R
 * {x=2127,m=1623,a=2188,s=1013}: in -> px -> rfg -> A
 *
 * Ultimately, three parts are accepted. Adding up the x, m, a, and s rating for each of the accepted parts gives 7540
 * for the part with x=787, 4623 for the part with x=2036, and 6951 for the part with x=2127. Adding all of the ratings
 * for all of the accepted parts gives the sum total of 19114.
 * Sort through all of the parts you've been given; what do you get if you add together all of the rating numbers for
 * all of the parts that ultimately get accepted?
 *
 * --- Part Two ---
 * Even with your help, the sorting process still isn't fast enough.
 * One of the Elves comes up with a new plan: rather than sort parts individually through all of these workflows, maybe
 * you can figure out in advance which combinations of ratings will be accepted or rejected.
 * Each of the four ratings (x, m, a, s) can have an integer value ranging from a minimum of 1 to a maximum of 4000. Of
 * all possible distinct combinations of ratings, your job is to figure out which ones will be accepted.
 * In the above example, there are 167409079868000 distinct combinations of ratings that will be accepted.
 * Consider only your list of workflows; the list of part ratings that the Elves wanted you to sort is no longer
 * relevant. How many distinct combinations of ratings will be accepted by the Elves' workflows?
 *
 */
class MachinePartSystem(workflows: List<Workflow>, val parts: List<Part>) {
  companion object {
    fun of(input: String): MachinePartSystem {
      val (workflowsSection, partsSection) = input.trim().split("\n\n")
      return MachinePartSystem(workflowsSection.lines().map(::Workflow), partsSection.lines().map(::Part))
    }
  }

  private val workflowsByName = workflows.associateBy { it.name }

  private fun test(part: Part) = test(part, "in")

  private tailrec fun test(part: Part, currentWorkflowName: String): PartConclusion {
    val workflow = workflowsByName[currentWorkflowName]
            ?: throw IllegalStateException("Can't find workflow $currentWorkflowName")
    // Contorted slightly to make this a tail call.
    val nextRuleName = when (val ruleConclusion = workflow.apply(part)) {
      is Conclude -> return ruleConclusion.conclusion
      is SendToWorkflow -> ruleConclusion.name
    }
    return test(part, nextRuleName)
  }

  fun acceptedPartSum(): Int = parts.filter { this.test(it) == PartConclusion.ACCEPT }.sumOf { it.sum() }

  fun countAllAccepted(): Long {
    val allowedValues = 1..4000
    val allParts = PartRange(mapOf(
            "x" to allowedValues,
            "m" to allowedValues,
            "a" to allowedValues,
            "s" to allowedValues
    ))
    return count("in", allParts)
  }

  private fun count(workflowName: String, partRange: PartRange): Long {
    val workflow = workflowsByName[workflowName]
            ?: throw IllegalStateException("Can't find workflow $workflowName")
    return count(partRange, workflow.rules)
  }

  private fun count(partRange: PartRange, rules: List<Rule>): Long {
    if (partRange.isEmpty()) return 0L
    val rule = rules.first()
    val (matching, notMatching) = rule.test.partition(partRange)
    val matchingCount = when (rule.result) {
      is Conclude -> if (rule.result.conclusion == PartConclusion.ACCEPT) matching.size() else 0
      is SendToWorkflow -> count(rule.result.name, matching)
    }
    val notMatchingCount = count(notMatching, rules.drop(1))
    // Since we have split the ranges, they are now disjoint and we can sum the count between them.
    return matchingCount + notMatchingCount
  }
}

data class Workflow(val name: String, val rules: List<Rule>) {
  constructor(input: String) : this(input.substringBefore("{"),
          input.substringBetween("{", "}")
                  .split(",")
                  .map(Rule::of))

  fun apply(part: Part): RuleConclusion = rules.asSequence()
          .map { it.apply(part) }
          .filterIsInstance<RuleConclusion>()
          .first()
}

data class Rule(val test: Test, val result: RuleConclusion) {
  companion object {
    fun of(input: String): Rule {
      return if (input.contains(":")) {
        val (conditionString, conclusionString) = input.split(":")
        Rule(Condition.of(conditionString), RuleConclusion.of(conclusionString))
      } else {
        Rule(Always, RuleConclusion.of(input))
      }
    }
  }

  fun apply(part: Part) = if (test.apply(part)) result else NextRule
}

sealed interface Test {
  fun apply(part: Part): Boolean

  // Returns the pair (the part of the input that matches this test, the part that does not match).
  fun partition(partRange: PartRange): Pair<PartRange, PartRange>
}

data class Condition(val category: String, val greater: Boolean, val amount: Int) : Test {
  companion object {
    fun of(input: String): Condition {
      val (category, amountString) = "[<>]".toRegex().split(input)
      return Condition(category, input.contains(">"), amountString.toInt())
    }
  }

  override fun apply(part: Part): Boolean {
    val partValue = part.ratings[category] ?: throw IllegalStateException("Part $part is missing $category")
    return if (greater) (partValue > amount) else (partValue < amount)
  }

  /**
   * Returns the pair (matches, doesn't match).
   */
  fun partition(range: IntRange): Pair<IntRange, IntRange> = when {
    greater && amount < range.first -> range to IntRange.EMPTY
    greater && amount >= range.last -> IntRange.EMPTY to range
    greater -> (amount + 1..range.last) to (range.first..amount)
    !greater && amount > range.last -> range to IntRange.EMPTY
    !greater && amount <= range.first -> IntRange.EMPTY to range
    else -> (range.first until amount) to (amount..range.last)
  }

  override fun partition(partRange: PartRange): Pair<PartRange, PartRange> {
    val intRange = partRange.ranges[category]
            ?: throw IllegalArgumentException("Part range $partRange missing $category")
    val (newMatchingRange, newNonMatchingRange) = partition(intRange)
    return partRange.replace(category, newMatchingRange) to partRange.replace(category, newNonMatchingRange)
  }
}

object Always : Test {
  override fun apply(part: Part) = true
  override fun partition(partRange: PartRange) = partRange to partRange.empty()
}

sealed interface RuleResult
object NextRule : RuleResult
sealed interface RuleConclusion : RuleResult {
  companion object {
    fun of(input: String) = when (input) {
      "R" -> Conclude(PartConclusion.REJECT)
      "A" -> Conclude(PartConclusion.ACCEPT)
      else -> SendToWorkflow(input)
    }
  }
}

data class SendToWorkflow(val name: String) : RuleConclusion
data class Conclude(val conclusion: PartConclusion) : RuleConclusion
enum class PartConclusion { REJECT, ACCEPT }

data class Part(val ratings: Map<String, Int>) {
  constructor(input: String) : this(input.substringBetween("{", "}")
          .split(",")
          .associate { it.substringBefore("=") to it.substringAfter("=").toInt() }
  )

  fun sum() = ratings.values.sum()
}

/**
 * Represents a possibility space of parts, with a range of values of each category.
 */
data class PartRange(val ranges: Map<String, IntRange>) {
  fun isEmpty() = ranges.values.any { it.isEmpty() }
  fun size(): Long = if (isEmpty()) 0L else ranges.values.fold(1L) { acc, range -> acc * (range.last - range.first + 1) }
  fun replace(category: String, newIntRange: IntRange): PartRange {
    val newRanges = ranges.toMutableMap()
    newRanges[category] = newIntRange
    return PartRange(newRanges)
  }

  fun empty() = PartRange(ranges.mapValues { (_, _) -> IntRange.EMPTY })
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day19/input.txt").readText().trim()
  val system = MachinePartSystem.of(input)

  println(system.acceptedPartSum())
  println(system.countAllAccepted())
}
