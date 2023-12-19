package advent.year2023.day19

import advent.year2020.day14.substringBetween
import java.io.File

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
}

object Always : Test {
  override fun apply(part: Part) = true
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

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day19/input.txt").readText().trim()
  val system = MachinePartSystem.of(input)

  println(system.acceptedPartSum())
}
