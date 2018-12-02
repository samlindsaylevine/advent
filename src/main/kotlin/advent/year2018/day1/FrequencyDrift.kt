package advent.year2018.day1

import com.google.common.collect.Iterators
import java.io.File

class FrequencyDrift(deltas: List<Int>) {

    companion object {
        private fun parseDelta(input: String): Int = when {
            input.startsWith("+") -> input.substring(1).toInt()
            else -> input.toInt()
        }
    }

    constructor(input: String) : this(input.trim().lines().map { FrequencyDrift.parseDelta(it) })

    val result = deltas.sum()

    val firstFrequencyReachedTwice by lazy {
        val observedFrequencies = mutableSetOf<Int>()

        var currentFrequency = 0
        val loopDeltas = Iterators.cycle(deltas)

        while (!(currentFrequency in observedFrequencies)) {
            observedFrequencies.add(currentFrequency)
            currentFrequency += loopDeltas.next()
        }

        currentFrequency
    }
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day1/input.txt")
            .readText()
            .trim()

    val drift = FrequencyDrift(input)

    println(drift.result)
    println(drift.firstFrequencyReachedTwice)
}