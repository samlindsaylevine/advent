package advent.year2017.day8

import java.io.File

/**
 * --- Day 8: I Heard You Like Registers ---
 * You receive a signal directly from the CPU. Because of your recent assistance with jump instructions, it would like
 * you to compute the result of a series of unusual register instructions.
 * Each instruction consists of several parts: the register to modify, whether to increase or decrease that register's
 * value, the amount by which to increase or decrease it, and a condition. If the condition fails, skip the instruction
 * without modifying the register. The registers all start at 0. The instructions look like this:
 * b inc 5 if a > 1
 * a inc 1 if b < 5
 * c dec -10 if a >= 1
 * c inc -20 if c == 10
 * 
 * These instructions would be processed as follows:
 * 
 * Because a starts at 0, it is not greater than 1, and so b is not modified.
 * a is increased by 1 (to 1) because b is less than 5 (it is 0).
 * c is decreased by -10 (to 10) because a is now greater than or equal to 1 (it is 1).
 * c is increased by -20 (to -10) because c is equal to 10.
 * 
 * After this process, the largest value in any register is 1.
 * You might also encounter <= (less than or equal to) or != (not equal to). However, the CPU doesn't have the
 * bandwidth to tell you what all the registers are named, and leaves that to you to determine.
 * What is the largest value in any register after completing the instructions in your puzzle input?
 * 
 * --- Part Two ---
 * To be safe, the CPU also needs to know the highest value held in any register during this process so that it can
 * decide how much memory to allocate to these operations. For example, in the above instructions, the highest value
 * ever held was 10 (in register c after the third instruction was evaluated).
 * 
 */
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

    fun largestRegisterValue(): Int = Math.max(registers.values.maxOrNull() ?: 0, 0)

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

fun main() {
    val instructions = File("src/main/kotlin/advent/year2017/day8/input.txt")
            .useLines { RegisterInstructions(it) }

    println(instructions.largestRegisterValue())
    println(instructions.highestValueEverHeld())
}