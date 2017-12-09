package advent.year2017.day8

import java.io.File

class RegisterInstructions(lines: Sequence<String>) {

    constructor(text: String) : this(text
            .split("\n")
            .asSequence())

    private val registers = mutableMapOf<String, Int>()
    private var highestValueEverHeld = 0

    init {
        lines.map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { RegisterInstruction(it) }
                .forEach { this.execute(it) }
    }

    private fun execute(instruction: RegisterInstruction) {
        val testValue = registers.computeIfAbsent(instruction.testRegisterName, { 0 })
        if (instruction.condition(testValue)) {
            val prevValue = registers.computeIfAbsent(instruction.registerName, { 0 })
            val newValue = prevValue + instruction.incrementAmount
            registers[instruction.registerName] = newValue

            highestValueEverHeld = Math.max(highestValueEverHeld, newValue)
        }
    }

    fun largestRegisterValue(): Int = Math.max(registers.values.max() ?: 0, 0)

    fun highestValueEverHeld(): Int = highestValueEverHeld

    private class RegisterInstruction(instruction: String) {
        val registerName: String
        val incrementAmount: Int
        val testRegisterName: String
        val condition: (Int) -> Boolean

        companion object {
            val REGEX = "(\\w+) (inc|dec) (-?\\d+) if (\\w+) (.*) (-?\\d+)".toRegex()
        }

        init {
            val match = REGEX.matchEntire(instruction) ?: throw IllegalArgumentException("Bad instruction $instruction")

            registerName = match.groupValues[1]
            val amountMultiplier = if (match.groupValues[2] == "dec") -1 else 1
            incrementAmount = amountMultiplier * match.groupValues[3].toInt()

            testRegisterName = match.groupValues[4]
            val operatorString = match.groupValues[5]
            val testAmount = match.groupValues[6].toInt()
            condition = when (operatorString) {
                ">" -> { n -> n > testAmount }
                "<" -> { n -> n < testAmount }
                ">=" -> { n -> n >= testAmount }
                "<=" -> { n -> n <= testAmount }
                "==" -> { n -> n == testAmount }
                "!=" -> { n -> n != testAmount }
                else -> throw IllegalArgumentException("Unrecognized operator $operatorString")
            }
        }
    }
}

fun main(args: Array<String>) {
    val instructions = File("src/main/kotlin/advent/year2017/day8/input.txt")
            .useLines { RegisterInstructions(it) }

    println(instructions.largestRegisterValue())
    println(instructions.highestValueEverHeld())
}