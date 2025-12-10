package advent.year2025.day10

import advent.meta.readInput
import advent.utils.CollapseOnCurrentState
import advent.utils.EndState
import advent.utils.ShortestPathFinder
import advent.utils.Steps
import advent.year2020.day14.substringBetween

class FactoryMachine(
  val targetIndicatorLights: List<Boolean>,
  val buttons: List<List<Int>>,
  val joltageRequirements: List<Int>
) {
  companion object {
    fun of(input: String): FactoryMachine {
      val indicatorLightString = input.substringBetween("[", "]")
      val targetIndicatorLights = indicatorLightString.map { it == '#' }

      val buttons: List<List<Int>> = input.substringAfter(" ").substringBeforeLast(" ")
        .split(" ")
        .map { buttonString ->
          buttonString.substringBetween("(", ")")
            .split(",")
            .map { it.toInt() }
        }

      val joltageRequirements = input.substringBetween("{", "}")
        .split(",")
        .map(String::toInt)

      return FactoryMachine(targetIndicatorLights, buttons, joltageRequirements)
    }
  }

  private fun nextLightStates(currentState: List<Boolean>): Set<List<Boolean>> =
    buttons.map { applyButton(currentState, it) }.toSet()

  private fun applyButton(lightState: List<Boolean>, button: List<Int>) =
    lightState.withIndex().map { (i, value) -> if (i in button) !value else value }

  fun minimumButtonPresses(): Int {
    val initialState = List(targetIndicatorLights.size) { false }

    val shortestPath = ShortestPathFinder().find(
      start = initialState,
      end = EndState(targetIndicatorLights),
      nextSteps = Steps(::nextLightStates),
      collapse = CollapseOnCurrentState()
    )

    return shortestPath.first().totalCost
  }
}

fun main() {
  val machines = readInput().trim().lines().map(FactoryMachine::of)

  println(machines.sumOf(FactoryMachine::minimumButtonPresses))
}