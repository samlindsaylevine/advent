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
            .map { index -> input.dotProduct(pattern(index)).onesDigit() }

    private fun List<Int>.dotProduct(other: Sequence<Int>): Int {
        return other.take(this.size)
                .zip(this.asSequence())
                .sumBy { (it.first * it.second) % 10 }
    }

    private fun Int.onesDigit() = Math.abs(this % 10)

    tailrec fun phases(input: List<Int>, numPhases: Int): List<Int> = if (numPhases <= 0) {
        input
    } else {
        phases(phase(input), numPhases - 1)
    }

    /**
     * This is too slow to be practical for either the test inputs or the problem input.
     *
     * Left here as a curiosity.
     */
    @Suppress("unused")
    fun naiveMessage(input: List<Int>): List<Int> {
        val realSignal = mutableListOf<Int>()
        repeat(10_000) { realSignal.addAll(input) }
        val offset = input.take(7).joinToString("").toInt()

        val result = phases(realSignal, 100)

        return result.drop(offset).take(8)
    }

    /**
     * One Weird Trick FFTs Hate! You Won't Believe The Final Digit!
     *
     * When looking at what happens when we run phases on our input, looking for relationships, we saw this in the
     * final digits:
     * 7105310340
     * 4766187740
     * 0693768140
     * 4489693540
     * 2846712940
     * 3139365340
     * 7430182740
     * 6952213140
     * 3783198540
     * 8580767940
     * 4613360340
     * Based on a little inspection, and based on our patternDiffs, we can see that each digit is the sum of the one
     * above it, and the one to the right of it (%10, of course).
     *
     * This pattern does NOT hold across the entire number, but it does for everything at least halfway through. So if
     * our offset is at least halfway through, we can use this expedient recurrence relation instead of doing a full
     * calculation.
     *
     * Hey, all the sample test values have an offset that is more than halfway through, and so does our
     * problem input...
     */
    fun message(input: List<Int>): List<Int> {

        val offset = input.take(7).joinToString("").toInt()

        if (offset < input.size * 10_000 / 2) {
            throw IllegalArgumentException("The offset is less than halfway through the list; " +
                    "our weird hack won't work")
        }

        val relevantInput = (offset until input.size * 10_000).map { input[it % input.size] }

        val result = recur(relevantInput, 100)
        return result.take(8)
    }

    fun recur(digits: List<Int>,
              numPhases: Int): List<Int> = if (numPhases == 0) {
        digits
    } else {
        val next = MutableList(digits.size) { 0 }
        next[next.size - 1] = digits[next.size - 1]
        for (i in next.size - 2 downTo 0) {
            next[i] = (digits[i] + next[i + 1]) % 10
        }

        recur(next, numPhases - 1)
    }
}

/**
 * Looking for a way to optimize the second half of the problem, I wondered if I could come up with a recurrence
 * relation to calculate digits based on the previous digit instead of having to do the entire calculation for each
 * digit. In particular, I wondered what it looked like if I found the difference between the "pattern" digits that
 * are right next to each other, since that might help me establish a relation between digits of the resulting
 * output.
 *
 * For i = 20, this has an output
 *
 * [1, -1, -2, 0, 1, 1, 0, 0, 1, -1, -2, 0, 1, 1, 0, 0, 1, -1, -2, 0]
 * [0, 1, 0, -1, -1, -1, -1, 0, 1, 2, 2, 0, 0, -1, -2, -1, -1, 1, 1, 0]
 * [0, 0, 1, 0, 0, -1, -1, 0, -1, -1, -1, 1, 1, 1, 2, 1, 1, 0, 0, -1]
 * [0, 0, 0, 1, 0, 0, 0, -1, -1, 0, 0, -1, -1, -1, 0, 1, 1, 1, 1, 1]
 * [0, 0, 0, 0, 1, 0, 0, 0, 0, -1, -1, 0, 0, 0, -1, -1, -1, 0, 0, 1]
 * [0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, -1, -1, -1]
 * [0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0]
 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0]
 *
 * Interesting. We can see some patterns in there, but particularly we can note that in the second half of the digits,
 * we have what looks like potentially a very simple recurrence relation.
 */
@Suppress("unused")
fun patternDiffs(i: Int) = (0 until i).map { FlawedFrequencyTransmission().pattern(it).take(i).toList() }
        .zipWithNext()
        .map { it.second.zip(it.first).map { digits -> digits.second - digits.first } }

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day16/input.txt")
            .readText()
            .trim()

    val transmission = FlawedFrequencyTransmission()

    val firstPart = transmission.phases(input.digits(), numPhases = 100)
            .take(8)
            .joinToString("")
    println(firstPart)

    val secondPart = transmission.message(input.digits())
            .joinToString("")
    println(secondPart)
}