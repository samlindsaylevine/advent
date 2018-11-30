package advent.year2017.day25

import java.io.File
import java.lang.IllegalStateException

class TuringMachine(var state: String) {
    var tapeIndex = 0
    private val slotsWithValueOne = mutableSetOf<Int>()

    fun currentValue() = if (slotsWithValueOne.contains(tapeIndex)) 1 else 0
    fun setValue(newValue: Int) = when (newValue) {
        1 -> slotsWithValueOne.add(tapeIndex)
        0 -> slotsWithValueOne.remove(tapeIndex)
        else -> throw IllegalArgumentException("Tape only stores 0 or 1, not $newValue")
    }

    fun checksum() = slotsWithValueOne.size
}

data class TuringMachineProgram constructor(private val initialState: String,
                                            private val stepsBeforeChecksum: Int,
                                            private val stateInstructions: Map<String, StateInstruction>) {


    private fun executeStep(machine: TuringMachine) {
        val stateInstructions = (stateInstructions[machine.state]
                ?: throw IllegalStateException("No instruction for state ${machine.state}"))
        val instruction = stateInstructions.valueInstructions[machine.currentValue()]
                ?: throw IllegalStateException("No instruction for value ${machine.currentValue()}")

        instruction.execute(machine)
    }

    fun execute(): TuringMachine {
        val machine = TuringMachine(initialState)

        (1..stepsBeforeChecksum).forEach { _ -> this.executeStep(machine) }

        return machine
    }
}

data class StateInstruction(val valueInstructions: Map<Int, Instruction>)

data class Instruction(private val newValue: Int,
                       private val indexDelta: Int,
                       private val newState: String) {
    fun execute(machine: TuringMachine) {
        machine.setValue(newValue)
        machine.tapeIndex += indexDelta
        machine.state = newState
    }
}

class TuringProgramParser {

    fun parse(input: String): TuringMachineProgram {
        val sections = input.trim().split("\n\\s*\n".toRegex())
        val initialState = findInitialState(sections.first())
        val stepsBeforeChecksum = findStepCount(sections.first())

        val stateInstructions = sections.subList(1, sections.size)
                .map(this::parseStateInstruction)
                .toMap()

        return TuringMachineProgram(initialState, stepsBeforeChecksum, stateInstructions)
    }

    private fun findInitialState(block: String) = block.findRegexMatch("Begin in state (.*).")
    private fun findStepCount(block: String): Int =
            block.findRegexMatch("Perform a diagnostic checksum after (.*) steps.").toInt()

    private fun parseStateInstruction(block: String): Pair<String, StateInstruction> {
        val state = block.findRegexMatch("In state (.*):")

        val valueInstructions = block.split("If").asSequence()
                .drop(1)
                .map(this::parseValueInstruction).toList()
                .toMap()

        return Pair(state, StateInstruction(valueInstructions))
    }

    private fun parseValueInstruction(block: String): Pair<Int, Instruction> {
        val value = block.findRegexMatch("current value is (.*):").toInt()
        val newValue = block.findRegexMatch("Write the value (.*).").toInt()
        val indexDirection = block.findRegexMatch("Move one slot to the (.*).")
        val indexDelta = if (indexDirection == "right") 1 else -1
        val newState = block.findRegexMatch("Continue with state (.*).")

        return Pair(value, Instruction(newValue, indexDelta, newState))
    }

    private fun String.findRegexMatch(regex: String): String {
        val match = regex.toRegex().find(this)
                ?: throw java.lang.IllegalArgumentException("No initial state in $this")
        return match.groupValues[1]
    }

}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day25/input.txt")
            .readText()
            .trim()

    val program = TuringProgramParser().parse(input)

    println(program.execute().checksum())
}