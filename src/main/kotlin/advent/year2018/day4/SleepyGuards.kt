package advent.year2018.day4

import com.google.common.collect.ArrayListMultimap
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SleepyGuards(val events: List<GuardEvent>) {

    private val guards: Set<SleepyGuard>

    constructor(input: String) : this(input.trim().split("\n")
            .map { GuardEvent.parse(it) }
            .sortedBy { it.timestamp })

    init {
        val ranges = ArrayListMultimap.create<Int, IntRange>()
        var currentGuard = 0
        var currentSleepStart = 0

        for (event in events) {
            when (event) {
                is NewGuard -> currentGuard = event.guardNumber
                is FallsAsleep -> currentSleepStart = event.timestamp.minute
                is WakesUp -> ranges.put(currentGuard, currentSleepStart until event.timestamp.minute)
            }
        }

        guards = ranges.asMap()
                .entries
                .map { entry -> SleepyGuard(entry.key, entry.value) }
                .toSet()
    }

    val sleepiestGuard = guards.maxBy { it.minutesAsleep }
    val mostFrequentlyAsleepOnSameMinute = guards.maxBy { it.timesAsleepOnSleepiestMinute }
}

class SleepyGuard(val number: Int,
                  private val sleepRanges: Collection<IntRange>) {
    val minutesAsleep = sleepRanges.sumBy { it.count() }
    private fun timesAsleepOn(minute: Int) = sleepRanges.count { it.contains(minute) }
    val sleepiestMinute = (0..59).maxBy(this::timesAsleepOn)!!
    val timesAsleepOnSleepiestMinute = timesAsleepOn(sleepiestMinute)
}

sealed class GuardEvent(val timestamp: LocalDateTime) {
    companion object {
        private val REGEX = "\\[(.*)] (.*)".toRegex()
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        private val GUARD_REGEX = "Guard #(\\d+) begins shift".toRegex()
        private const val FALLS_ASLEEP = "falls asleep"
        private const val WAKES_UP = "wakes up"

        fun parse(input: String): GuardEvent {
            val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable event $input")

            val timestamp = LocalDateTime.parse(match.groupValues[1], DATE_FORMAT)
            val description = match.groupValues[2]
            val guardMatch = GUARD_REGEX.matchEntire(description)

            return when {
                guardMatch != null -> NewGuard(timestamp, guardMatch.groupValues[1].toInt())
                description == FALLS_ASLEEP -> FallsAsleep(timestamp)
                description == WAKES_UP -> WakesUp(timestamp)
                else -> throw IllegalArgumentException("Unparseable description $description")
            }
        }
    }
}

class NewGuard(timestamp: LocalDateTime,
               val guardNumber: Int) : GuardEvent(timestamp)

class FallsAsleep(timestamp: LocalDateTime) : GuardEvent(timestamp)
class WakesUp(timestamp: LocalDateTime) : GuardEvent(timestamp)

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day4/input.txt")
            .readText()
            .trim()

    val guards = SleepyGuards(input)

    val sleepiestGuard = guards.sleepiestGuard ?: throw IllegalStateException("No guards")
    println(sleepiestGuard.number * sleepiestGuard.sleepiestMinute)

    val mostFrequentlyAsleep = guards.mostFrequentlyAsleepOnSameMinute ?: throw IllegalStateException("No guards")
    println(mostFrequentlyAsleep.number * mostFrequentlyAsleep.sleepiestMinute)
}