package advent.year2018.day7

import java.io.File
import java.lang.IllegalStateException

class SleighInstructions(private val requirementsByStep: Map<String, Set<String>>) {

    constructor(input: String) : this(input.trim().split("\n")
            .map { SleighInstruction.parse(it) }
            .groupBy { it.step }
            .mapValues { entry -> entry.value.map { it.requirement }.toSet() })

    private val allSteps = requirementsByStep.values.fold(requirementsByStep.keys) { steps, requirements -> steps + requirements }

    fun stepsInOrder(): List<String> {
        if (allSteps.isEmpty()) return emptyList()

        val availableSteps = allSteps.filter { requirementsByStep[it]?.isEmpty() ?: true }
        val nextStep = availableSteps.min() ?: throw IllegalStateException("Unreachable steps $allSteps")

        return listOf(nextStep) + this.without(nextStep).stepsInOrder()
    }

    fun without(step: String): SleighInstructions {
        val newMap = requirementsByStep.mapValues { entry -> entry.value - step }
        return SleighInstructions(newMap - step)
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

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day7/input.txt")
            .readText()
            .trim()

    val instructions = SleighInstructions(input)
    val steps = instructions.stepsInOrder()

    println(steps.joinToString(separator = ""))
}