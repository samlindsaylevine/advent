package advent.year2018.day7

import java.io.File
import java.util.Collections.nCopies

class SleighInstructions(private val requirementsByStep: Map<String, Set<String>>,
                         private val workerTaskDurations: List<Pair<String, Int>?>,
                         private val baseStepDuration: Int,
                         private val timeElapsed: Int) {

    constructor(input: String, numWorkers: Int = 1, baseStepDuration: Int = 0) : this(input.trim().split("\n")
            .map { SleighInstruction.parse(it) }
            .groupBy { it.step }
            .mapValues { entry -> entry.value.map { it.requirement }.toSet() },
            nCopies(numWorkers, null as Pair<String, Int>?),
            baseStepDuration,
            0)

    private val allSteps = requirementsByStep.values.fold(requirementsByStep.keys) { steps, requirements -> steps + requirements }

    fun stepsInOrder(): List<String> {
        if (allSteps.isEmpty()) return emptyList()

        val nextStep = availableSteps().minOrNull() ?: throw IllegalStateException("Unreachable steps $allSteps")

        return listOf(nextStep) + this.without(nextStep).stepsInOrder()
    }

    private fun availableSteps() = allSteps.filter { requirementsByStep[it]?.isEmpty() ?: true }
            .filter { step -> !workerTaskDurations.any { it?.first == step } }

    private fun availableWorkers() = (0 until workerTaskDurations.size).filter { workerTaskDurations[it] == null }

    fun without(step: String): SleighInstructions {
        val newMap = requirementsByStep.mapValues { entry -> entry.value - step } - step
        val newWorkerTasks = workerTaskDurations.map { if (it?.first == step) null else it }
        return SleighInstructions(newMap, newWorkerTasks, baseStepDuration, timeElapsed)
    }

    fun timeToComplete(): Int {
        if (allSteps.isEmpty()) return timeElapsed

        return workingUntilNextTaskCompleted().timeToComplete()
    }

    private fun workingUntilNextTaskCompleted(): SleighInstructions {
        if (!availableSteps().isEmpty() && !availableWorkers().isEmpty()) {
            return this.working(availableWorkers().first(), availableSteps().first()).workingUntilNextTaskCompleted()
        }

        val (nextToComplete, workTime) = workerTaskDurations.minByOrNull { it?.second ?: Integer.MAX_VALUE }
                ?: throw IllegalStateException("No tasks underway and none available")

        val without = this.without(nextToComplete)

        return SleighInstructions(without.requirementsByStep,
                without.workerTaskDurations.map { it?.let { Pair(it.first, it.second - workTime) } },
                baseStepDuration,
                without.timeElapsed + workTime)
    }

    private fun working(workerIndex: Int, step: String): SleighInstructions {
        val newTaskDurations = workerTaskDurations.toMutableList()
        newTaskDurations[workerIndex] = Pair(step, baseStepDuration + additionalTimeForStep(step))

        return SleighInstructions(this.requirementsByStep,
                newTaskDurations,
                baseStepDuration,
                timeElapsed)
    }

    private fun additionalTimeForStep(step: String): Int {
        val chars = step.toCharArray()
        if (chars.size != 1) throw IllegalArgumentException("Step names must be one character")
        return chars[0].toInt() - 'A'.toInt() + 1
    }

    private data class SleighInstruction(val requirement: String, val step: String) {
        companion object {
            private val REGEX = "Step (.+) must be finished before step (.+) can begin.".toRegex()
            fun parse(line: String): SleighInstruction {
                val match = REGEX.matchEntire(line) ?: throw IllegalArgumentException("Unparseable instruction $line")

                return SleighInstruction(match.groupValues[1], match.groupValues[2])
            }
        }
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day7/input.txt")
            .readText()
            .trim()

    val instructions = SleighInstructions(input, numWorkers = 5, baseStepDuration = 60)
    val steps = instructions.stepsInOrder()

    println(steps.joinToString(separator = ""))

    println(instructions.timeToComplete())
}