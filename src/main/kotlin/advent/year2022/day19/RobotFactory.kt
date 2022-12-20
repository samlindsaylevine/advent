package advent.year2022.day19

import advent.utils.merge
import java.io.File
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

class RobotFactory(val blueprints: List<RobotFactoryBlueprint>) {
  constructor(input: String) : this(input.lines().map(RobotFactoryBlueprint::parse))
}

class RobotFactoryBlueprint(
  val id: Int,
  private val robotCosts: Map<String, Resources>
) {
  companion object {
    fun parse(input: String): RobotFactoryBlueprint {
      val id = input.substringBefore(":").substringAfterLast(" ").toInt()
      val costs = input.substringAfter(": ").split(". ").associate(::parseRobot)
      return RobotFactoryBlueprint(id, costs)
    }

    private fun parseRobot(input: String): Pair<String, Resources> {
      val regex = "Each (.*) robot costs (.*?)\\.?".toRegex()
      val match = regex.matchEntire(input.trim()) ?: throw IllegalArgumentException("Unparseable robot $input")
      val (robotType, costString) = match.destructured
      val costs = costString.split(" and ").associate { costPhrase ->
        costPhrase.substringAfter(" ") to costPhrase.substringBefore(" ").toInt()
      }
      return robotType to costs
    }
  }

  fun qualityLevel() = this.id * this.maxProducible("geode", 24)

  fun maxProducible(type: String = "geode", time: Int = 24): Int {
    val possibilities = PriorityQueue(
      100,
      // Depth-first search - look at ones near the end of their time first.
      Comparator.comparing(FactoryState::timeElapsed).reversed()
    ).apply {
      add(FactoryState())
    }
    var bestResult = 0

    // We don't need to build more of any (non-target-type) robot than it would take to build a robot every turn.
    val maxRequired = robotCosts.keys.associateWith { resource -> robotCosts.maxOf { it.value[resource] ?: 0 } }

    fun maxRequired(robotType: String) = if (robotType == type) Int.MAX_VALUE else maxRequired[robotType] ?: 0

    while (possibilities.isNotEmpty()) {
      val next = possibilities.poll()

      val endResultIfNothingMoreIsBuilt = next.amountAt(type, time)
      if (endResultIfNothingMoreIsBuilt > bestResult) bestResult = endResultIfNothingMoreIsBuilt

      // Prune this if it can't possibly beat our current best result.
      val timeRemaining = time - next.timeElapsed
      // A (possibly lowerable) upper bound - if we build a new robot of the target type every one of the remaining
      // turns.
      val maxPossibleEndResult = endResultIfNothingMoreIsBuilt + timeRemaining * (timeRemaining - 1) / 2
      val canPossiblyBeatCurrentBest = maxPossibleEndResult > bestResult

      val nextStates = if (canPossiblyBeatCurrentBest) {
        robotCosts.mapNotNull { (type, resources) ->
          val timeToBuild = next.timeToBuild(resources)
          when {
            next.robotCount(type) >= maxRequired(type) -> null
            timeToBuild != null && timeToBuild + next.timeElapsed < time -> next.purchase(type, resources, timeToBuild)
            else -> null
          }
        }
      } else emptyList()

      possibilities.addAll(nextStates)
    }

    return bestResult
  }

  // Time it takes to build one of these robots, including turns that we spend saving up building nothing.
  // Null if the robot type is not buildable at all; i.e., we do not have production of all of its inputs.
  private fun FactoryState.timeToBuild(costToBuild: Resources): Int? {
    if (!robots.keys.containsAll(costToBuild.keys)) return null
    val needToSaveUp = costToBuild.mapValues { (type, amount) -> amount - this.resources(type) }
    val turnsToSaveUp = needToSaveUp.maxOf { (type, amount) ->
      // There's probably a more type-safe way to do this.
      val perTurn = robots[type] ?: throw IllegalStateException("Should have already returned null")
      ceil(amount.toDouble() / perTurn.toDouble()).toInt()
    }.let { max(it, 0) }
    // 1 turn to actually build the robot.
    return turnsToSaveUp + 1
  }

  private fun FactoryState.purchase(robotType: String, cost: Resources, time: Int) = FactoryState(
    this.robots + mapOf(robotType to 1),
    this.resources + (-1 * cost) + (time * this.robots),
    this.timeElapsed + time
  )
}

typealias Resources = Map<String, Int>

private operator fun Resources.plus(other: Resources) = this.merge(other) { a, b -> a + b }
private operator fun Int.times(resources: Resources) = resources.mapValues { (_, amount) -> this * amount }

data class FactoryState(
  val robots: Map<String, Int> = mapOf("ore" to 1),
  val resources: Resources = emptyMap(),
  val timeElapsed: Int = 0
) {
  fun robotCount(type: String) = robots[type] ?: 0
  fun resources(type: String) = resources[type] ?: 0

  fun amountAt(type: String, time: Int): Int = resources(type) + (time - timeElapsed) * (robots[type] ?: 0)
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day19/input.txt").readText().trim()

  val factory = RobotFactory(input)

  println(factory.blueprints.sumOf { it.qualityLevel() })

  val elephantEatenFactory = RobotFactory(factory.blueprints.take(3))

  println(elephantEatenFactory.blueprints
    .map { it.maxProducible(time = 32) }
    .reduce(Int::times))
}
