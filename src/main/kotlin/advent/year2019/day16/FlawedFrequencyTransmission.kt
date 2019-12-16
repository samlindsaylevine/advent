package advent.year2019.day16

import advent.utils.digits
import java.io.File

class FlawedFrequencyTransmission {

    fun pattern(index: Int): Sequence<Int> {
        val basePattern = listOf(0, 1, 0, -1)
        val targetRepeatCount = index + 1
        var patternIndex = 0
        var repeatsPerformed = 0

        val repeatedValues = generateSequence {
            if ((repeatsPerformed) == targetRepeatCount) {
                patternIndex = (patternIndex + 1) % basePattern.size
                repeatsPerformed = 0
            }

            repeatsPerformed++

            basePattern[patternIndex]
        }

        return repeatedValues.drop(1)
    }

    fun phase(input: List<Int>) = (0 until input.size)
            .map { index ->
                input.dotProduct(pattern(index)).onesDigit()
            }

    private fun List<Int>.dotProduct(other: Sequence<Int>): Int {
        return other.take(this.size)
                .zip(this.asSequence())
                .sumBy { it.first * it.second }
    }

    private fun Int.onesDigit() = Math.abs(this % 10)

    tailrec fun phases(input: List<Int>, numPhases: Int): List<Int> = if (numPhases <= 0) {
        input
    } else {
        phases(phase(input), numPhases - 1)
    }

    fun message(input: List<Int>): List<Int> {
        val realSignal = mutableListOf<Int>()
        repeat(10_000) { realSignal.addAll(input) }
        val offset = input.take(7).joinToString("").toInt()

        val result = phases(realSignal, 100)

        return result.drop(offset).take(8)
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day16/input.txt")
            .readText()
            .trim()

    val transmission = FlawedFrequencyTransmission()

    val firstPart = transmission.phases(input.digits(), numPhases = 100)

    println(firstPart.take(8).joinToString(""))
}