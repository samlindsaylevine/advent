package advent.year2023.day6

import advent.utils.findAllNumbers
import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

class BoatRaces(val races: List<BoatRace>) {
    companion object {
        fun of(input: String): BoatRaces =
                input.lines().first().findAllNumbers().zip(input.lines().drop(1).first().findAllNumbers())
                        .map { BoatRace(it.first.toLong(), it.second.toLong()) }
                        .let(::BoatRaces)
    }

    fun numberOfWaysToBeatTheRecords() = races.map { it.recordBeatingOptions() }
            .filter { !it.isEmpty() }
            .map { it.size() }
            .reduce(Long::times)
}

data class BoatRace(val duration: Long, val recordDistance: Long) {
    companion object {
        fun of(input: String): BoatRace {
            val lines = input.lines()
            return BoatRace(lines.first().withoutNonDigits().toLong(),
                    lines.drop(1).first().withoutNonDigits().toLong())
        }

        private fun String.withoutNonDigits() = this.replace("\\D".toRegex(), "")
    }

    fun recordBeatingOptions(): LongRange {
        // If we wait for N milliseconds, we have a speed of N, and a duration remaining of (duration - N) to move,
        // so we achieve a distance of (duration * N) - N^2.
        // This is a quadratic equation in N; we will beat the distance when (calling duration 'D', and record 'R')
        // D * N - N^2 > R
        // or
        // 0 > N^2 - D * N + R
        // We'll solve this quadratic equation to find the zeroes, and then the values of N between them will be the
        // winning ones.
        // Of course, you recall that the solutions to the quadratic equation are (-b +/- sqrt(b^2-4ac))/2a.
        // For us, that means they are (D +/- sqrt(D^2 - 4*R))/2.
        val sqrtPart = sqrt((duration.toDouble() * duration.toDouble() - 4 * recordDistance))
        val min = (duration - sqrtPart) / 2
        val max = (duration + sqrtPart) / 2
        // Remember, we have to *beat* the record, so if either min or max are exact integers, we need the next one.
        val lowestWinner: Long = if (min == ceil(min)) (min.toLong() + 1) else ceil(min).toLong()
        val highestWinner: Long = if (max == floor(max)) (max.toLong() - 1) else floor(max).toLong()
        return lowestWinner..highestWinner
    }
}

fun LongRange.size(): Long = if (this.isEmpty()) 0 else (this.last - this.first + 1)

fun main() {
    val input = File("src/main/kotlin/advent/year2023/day6/input.txt").readText().trim()
    val races = BoatRaces.of(input)
    println(races.numberOfWaysToBeatTheRecords())
    val singleRace = BoatRace.of(input)
    println(singleRace.recordBeatingOptions().size())
}