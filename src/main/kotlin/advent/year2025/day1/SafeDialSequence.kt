package advent.year2025.day1

import advent.meta.readInput

class SafeDialSequence(val steps: List<Int>) {
    private val dialSize = 100
    private val initialDialValue = 50

    constructor(input: String) : this(input.trim().lines().map(String::dialStepValue))

    fun positions(): Sequence<Int> = steps.asSequence()
        .runningFold(initialDialValue) { position, stepValue -> Math.floorMod(position + stepValue, dialSize) }

    fun password() = positions().count { it == 0 }
}

private fun String.dialStepValue(): Int {
    val direction = this.take(1)
    val amount = this.drop(1).toInt()

    return when (direction) {
        "L" -> -amount
        "R" -> amount
        else -> throw IllegalArgumentException("Unrecognized direction $direction")
    }
}

fun main() {
    val sequence = SafeDialSequence(readInput())

    println(sequence.password())
}
