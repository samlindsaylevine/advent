package advent.year2022.day16

import advent.utils.updated
import java.io.File
import java.lang.Integer.max

/**
 * --- Day 16: Proboscidea Volcanium ---
 * The sensors have led you to the origin of the distress signal: yet another handheld device, just like the one the
 * Elves gave you. However, you don't see any Elves around; instead, the device is surrounded by elephants! They must
 * have gotten lost in these tunnels, and one of the elephants apparently figured out how to turn on the distress
 * signal.
 * The ground rumbles again, much stronger this time. What kind of cave is this, exactly? You scan the cave with your
 * handheld device; it reports mostly igneous rock, some ash, pockets of pressurized gas, magma... this isn't just a
 * cave, it's a volcano!
 * You need to get the elephants out of here, quickly. Your device estimates that you have 30 minutes before the
 * volcano erupts, so you don't have time to go back out the way you came in.
 * You scan the cave for other options and discover a network of pipes and pressure-release valves. You aren't sure how
 * such a system got into a volcano, but you don't have time to complain; your device produces a report (your puzzle
 * input) of each valve's flow rate if it were opened (in pressure per minute) and the tunnels you could use to move
 * between the valves.
 * There's even a valve in the room you and the elephants are currently standing in labeled AA. You estimate it will
 * take you one minute to open a single valve and one minute to follow any tunnel from one valve to another. What is
 * the most pressure you could release?
 * For example, suppose you had the following scan output:
 * Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
 * Valve BB has flow rate=13; tunnels lead to valves CC, AA
 * Valve CC has flow rate=2; tunnels lead to valves DD, BB
 * Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
 * Valve EE has flow rate=3; tunnels lead to valves FF, DD
 * Valve FF has flow rate=0; tunnels lead to valves EE, GG
 * Valve GG has flow rate=0; tunnels lead to valves FF, HH
 * Valve HH has flow rate=22; tunnel leads to valve GG
 * Valve II has flow rate=0; tunnels lead to valves AA, JJ
 * Valve JJ has flow rate=21; tunnel leads to valve II
 *
 * All of the valves begin closed. You start at valve AA, but it must be damaged or jammed or something: its flow rate
 * is 0, so there's no point in opening it. However, you could spend one minute moving to valve BB and another minute
 * opening it; doing so would release pressure during the remaining 28 minutes at a flow rate of 13, a total eventual
 * pressure release of 28 * 13 = 364. Then, you could spend your third minute moving to valve CC and your fourth minute
 * opening it, providing an additional 26 minutes of eventual pressure release at a flow rate of 2, or 52 total
 * pressure released by valve CC.
 * Making your way through the tunnels like this, you could probably open many or all of the valves by the time 30
 * minutes have elapsed. However, you need to release as much pressure as possible, so you'll need to be methodical.
 * Instead, consider this approach:
 * == Minute 1 ==
 * No valves are open.
 * You move to valve DD.
 *
 * == Minute 2 ==
 * No valves are open.
 * You open valve DD.
 *
 * == Minute 3 ==
 * Valve DD is open, releasing 20 pressure.
 * You move to valve CC.
 *
 * == Minute 4 ==
 * Valve DD is open, releasing 20 pressure.
 * You move to valve BB.
 *
 * == Minute 5 ==
 * Valve DD is open, releasing 20 pressure.
 * You open valve BB.
 *
 * == Minute 6 ==
 * Valves BB and DD are open, releasing 33 pressure.
 * You move to valve AA.
 *
 * == Minute 7 ==
 * Valves BB and DD are open, releasing 33 pressure.
 * You move to valve II.
 *
 * == Minute 8 ==
 * Valves BB and DD are open, releasing 33 pressure.
 * You move to valve JJ.
 *
 * == Minute 9 ==
 * Valves BB and DD are open, releasing 33 pressure.
 * You open valve JJ.
 *
 * == Minute 10 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve II.
 *
 * == Minute 11 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve AA.
 *
 * == Minute 12 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve DD.
 *
 * == Minute 13 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve EE.
 *
 * == Minute 14 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve FF.
 *
 * == Minute 15 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve GG.
 *
 * == Minute 16 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve HH.
 *
 * == Minute 17 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You open valve HH.
 *
 * == Minute 18 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You move to valve GG.
 *
 * == Minute 19 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You move to valve FF.
 *
 * == Minute 20 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You move to valve EE.
 *
 * == Minute 21 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You open valve EE.
 *
 * == Minute 22 ==
 * Valves BB, DD, EE, HH, and JJ are open, releasing 79 pressure.
 * You move to valve DD.
 *
 * == Minute 23 ==
 * Valves BB, DD, EE, HH, and JJ are open, releasing 79 pressure.
 * You move to valve CC.
 *
 * == Minute 24 ==
 * Valves BB, DD, EE, HH, and JJ are open, releasing 79 pressure.
 * You open valve CC.
 *
 * == Minute 25 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 26 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 27 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 28 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 29 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 30 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * This approach lets you release the most pressure possible in 30 minutes with this valve layout, 1651.
 * Work out the steps to release the most pressure in 30 minutes. What is the most pressure you can release?
 *
 * --- Part Two ---
 * You're worried that even with an optimal approach, the pressure released won't be enough. What if you got one of the
 * elephants to help you?
 * It would take you 4 minutes to teach an elephant how to open the right valves in the right order, leaving you with
 * only 26 minutes to actually execute your plan. Would having two of you working together be better, even if it means
 * having less time? (Assume that you teach the elephant before opening any valves yourself, giving you both the same
 * full 26 minutes.)
 * In the example above, you could teach the elephant to help you as follows:
 * == Minute 1 ==
 * No valves are open.
 * You move to valve II.
 * The elephant moves to valve DD.
 *
 * == Minute 2 ==
 * No valves are open.
 * You move to valve JJ.
 * The elephant opens valve DD.
 *
 * == Minute 3 ==
 * Valve DD is open, releasing 20 pressure.
 * You open valve JJ.
 * The elephant moves to valve EE.
 *
 * == Minute 4 ==
 * Valves DD and JJ are open, releasing 41 pressure.
 * You move to valve II.
 * The elephant moves to valve FF.
 *
 * == Minute 5 ==
 * Valves DD and JJ are open, releasing 41 pressure.
 * You move to valve AA.
 * The elephant moves to valve GG.
 *
 * == Minute 6 ==
 * Valves DD and JJ are open, releasing 41 pressure.
 * You move to valve BB.
 * The elephant moves to valve HH.
 *
 * == Minute 7 ==
 * Valves DD and JJ are open, releasing 41 pressure.
 * You open valve BB.
 * The elephant opens valve HH.
 *
 * == Minute 8 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You move to valve CC.
 * The elephant moves to valve GG.
 *
 * == Minute 9 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You open valve CC.
 * The elephant moves to valve FF.
 *
 * == Minute 10 ==
 * Valves BB, CC, DD, HH, and JJ are open, releasing 78 pressure.
 * The elephant moves to valve EE.
 *
 * == Minute 11 ==
 * Valves BB, CC, DD, HH, and JJ are open, releasing 78 pressure.
 * The elephant opens valve EE.
 *
 * (At this point, all valves are open.)
 *
 * == Minute 12 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * ...
 *
 * == Minute 20 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * ...
 *
 * == Minute 26 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * With the elephant helping, after 26 minutes, the best you could do would release a total of 1707 pressure.
 * With you and an elephant working together for 26 minutes, what is the most pressure you could release?
 *
 */
class VolcanoValves(private val valvesByName: Map<String, VolcanoValve>) {
  companion object {
    private const val TIME_TO_ERUPT = 30
  }

  constructor(input: String) : this(input.lines()
    .map(VolcanoValve::parse)
    .associateBy { it.name }
  )

  private val nonZeroValveNames = valvesByName.values
    .filter { it.flowRate > 0 }
    .map { it.name }
    .toSet()

  // Legal next moves.
  private fun next(state: ValveExplorationState, workerNumber: Int): NextVolcanoState {
    // Either ran out of time; or all valves are open and nothing else to do but wait.
    if (state.valvesOpen == nonZeroValveNames || state.timeElapsed == TIME_TO_ERUPT) {
      return CompletedVolcanoWalk(state.flowEarned)
    }

    val atLastWorker = (workerNumber == state.currentValves.size - 1)
    val timeIncrement = if (atLastWorker) 1 else 0

    val currentValveName = state.currentValves[workerNumber]
    val currentValve = valvesByName[currentValveName]
      ?: throw IllegalStateException("At non-existent valve $currentValveName")

    val walks = currentValve.adjacent.map { newValve ->
      ValveExplorationState(
        state.timeElapsed + timeIncrement,
        state.currentValves.updated(workerNumber, newValve),
        state.flowEarned,
        state.valvesOpen
      )
    }.toSet()

    val nextStates = if (currentValveName in state.valvesOpen || currentValve.flowRate == 0) {
      walks
    } else {
      val newFlowEarned = currentValve.flowRate * (TIME_TO_ERUPT - state.timeElapsed - 1)
      val withValveOpened = ValveExplorationState(
        state.timeElapsed + timeIncrement,
        state.currentValves,
        state.flowEarned + newFlowEarned,
        state.valvesOpen + currentValveName
      )
      walks + withValveOpened
    }

    return StillWalking(nextStates)
  }

  fun maxPressureReleased(withElephant: Boolean = false): Int {
    val initialPositions = if (withElephant) listOf("AA", "AA") else listOf("AA")
    val initialTime = if (withElephant) 4 else 0
    val initialState = ValveExplorationState(
      currentValves = initialPositions,
      timeElapsed = initialTime,
      flowEarned = 0,
      valvesOpen = emptySet()
    )
    val numWorkers = initialPositions.size
    return maxPressure(
      setOf(initialState),
      emptyMap(),
      workersMoved = 0,
      numWorkers = numWorkers,
      mostTotalPressure = 0
    )
  }

  private tailrec fun maxPressure(
    currentStates: Set<ValveExplorationState>,
    visited: Map<ValveExplorationKey, Int>,
    workersMoved: Int,
    numWorkers: Int,
    mostTotalPressure: Int
  ): Int {
    if (currentStates.isEmpty()) return mostTotalPressure

    val next = currentStates.map { next(it, workersMoved) }
    val completed = next.filterIsInstance<CompletedVolcanoWalk>()
    val maxOfCompleted = completed.maxOfOrNull { it.totalPressure } ?: 0
    val nextMostTotalPressure = max(maxOfCompleted, mostTotalPressure)

    val stillGoing = next.filterIsInstance<StillWalking>()
    val nextStates = stillGoing.flatMap { it.states }

    // Drop any new states that we have already visited exactly (same positions & valves) and haven't improved on the
    // total flow.
    val (alreadyVisited, notYetVisited) = nextStates.partition { it.uniqueKey in visited.keys }

    val improvements = alreadyVisited.filter {
      val existingRecord = visited[it.uniqueKey] ?: 0
      it.flowEarned > existingRecord
    }

    // Collapse the next states - any that have the same current state and valves open should be de-duplicated,
    // preserving only the one with the highest flowEarned.
    // Worker positions (me & the elephant) are interchangeable.
    val collapsed = (improvements + notYetVisited)
      .groupBy { it.uniqueKey }
      .map { it.value.maxBy { state -> state.flowEarned } }
      .toSet()

    val nextVisited = visited + collapsed.associate { it.uniqueKey to it.flowEarned }

    // Weird and ugly impatient hack - only take the 10,000 best states. This is super not pretty and not really
    // guaranteed to work in the general case!
    val toUse = collapsed.sortedByDescending { it.flowEarned }.take(10000).toSet()

    return maxPressure(toUse, nextVisited, (workersMoved + 1) % numWorkers, numWorkers, nextMostTotalPressure)
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

typealias ValveExplorationKey = Pair<Set<String>, Set<String>>

data class ValveExplorationState(
  val timeElapsed: Int,
  // One element if just me; two if an elephant helper exists.
  val currentValves: List<String>,
  val flowEarned: Int,
  val valvesOpen: Set<String>
) {
  val uniqueKey: ValveExplorationKey by lazy { currentValves.toSet() to valvesOpen }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2022/day16/input.txt").readText().trim()
  val valves = VolcanoValves(input)

  println(valves.maxPressureReleased())
  println(valves.maxPressureReleased(withElephant = true))
}