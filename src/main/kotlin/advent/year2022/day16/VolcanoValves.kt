package advent.year2022.day16

import java.io.File
import java.lang.Integer.max

class VolcanoValves(val valvesByName: Map<String, VolcanoValve>) {
  companion object {
    private const val TIME_TO_ERUPT = 30
  }

  constructor(input: String) : this(input.lines()
    .map(VolcanoValve::parse)
    .associateBy { it.name }
  )

  // Legal next moves.
  private fun next(state: ValveExplorationState): NextVolcanoState {
    // Either ran out of time; or all valves are open and nothing else to do but wait.
    if (state.valvesOpen == valvesByName.keys || state.timeElapsed == TIME_TO_ERUPT) {
      return CompletedVolcanoWalk(state.flowEarned)
    }

    val currentValve = valvesByName[state.currentValve]
      ?: throw IllegalStateException("At non-existent valve ${state.currentValve}")

    val walks = currentValve.adjacent.map { newValve ->
      ValveExplorationState(state.timeElapsed + 1, newValve, state.flowEarned, state.valvesOpen)
    }.toSet()

    val nextStates = if (state.currentValve in state.valvesOpen || currentValve.flowRate == 0) {
      walks
    } else {
      val newFlowEarned = currentValve.flowRate * (TIME_TO_ERUPT - state.timeElapsed - 1)
      val withValveOpened = ValveExplorationState(
        state.timeElapsed + 1,
        state.currentValve,
        state.flowEarned + newFlowEarned,
        state.valvesOpen + state.currentValve
      )
      walks + withValveOpened
    }

    return StillWalking(nextStates)
  }

  fun maxPressureReleased(): Int = maxPressure(setOf(ValveExplorationState()), 0)

  private tailrec fun maxPressure(currentStates: Set<ValveExplorationState>, mostTotalPressure: Int): Int {
    if (currentStates.isEmpty()) return mostTotalPressure

    val next = currentStates.map { next(it) }
    val completed = next.filterIsInstance<CompletedVolcanoWalk>()
    val maxOfCompleted = completed.maxOfOrNull { it.totalPressure } ?: 0
    val nextMostTotalPressure = max(maxOfCompleted, mostTotalPressure)

    val stillGoing = next.filterIsInstance<StillWalking>()
    val nextStates = stillGoing.flatMap { it.states }

    // Collapse the next states - any that have the same current state and valves open should be de-duplicated,
    // preserving only the one with the highest flowEarned.
    val collapsed = nextStates.groupBy { it.currentValve to it.valvesOpen }
      .map { it.value.maxBy { state -> state.flowEarned } }
      .toSet()

    return maxPressure(collapsed, nextMostTotalPressure)
  }
}

private sealed class NextVolcanoState
private data class CompletedVolcanoWalk(val totalPressure: Int) : NextVolcanoState()
private data class StillWalking(val states: Set<ValveExplorationState>) : NextVolcanoState()

data class VolcanoValve(
  val name: String,
  val flowRate: Int,
  val adjacent: List<String>
) {
  companion object {
    fun parse(input: String): VolcanoValve {
      val regex = "Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)".toRegex()
      val match = regex.matchEntire(input) ?: throw IllegalArgumentException("Can't parse $input")
      val (name, flowRateString, adjacentString) = match.destructured
      return VolcanoValve(name, flowRateString.toInt(), adjacentString.split(", "))
    }
  }
}

data class ValveExplorationState(
  val timeElapsed: Int = 0,
  val currentValve: String = "AA",
  val flowEarned: Int = 0,
  val valvesOpen: Set<String> = emptySet()
)

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day16/input.txt").readText().trim()
  val valves = VolcanoValves(input)

  println(valves.maxPressureReleased())
}