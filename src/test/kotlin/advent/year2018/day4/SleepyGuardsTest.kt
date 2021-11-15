package advent.year2018.day4

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SleepyGuardsTest {

    private val referenceInput = """
        [1518-11-01 00:00] Guard #10 begins shift
        [1518-11-01 00:05] falls asleep
        [1518-11-01 00:25] wakes up
        [1518-11-01 00:30] falls asleep
        [1518-11-01 00:55] wakes up
        [1518-11-01 23:58] Guard #99 begins shift
        [1518-11-02 00:40] falls asleep
        [1518-11-02 00:50] wakes up
        [1518-11-03 00:05] Guard #10 begins shift
        [1518-11-03 00:24] falls asleep
        [1518-11-03 00:29] wakes up
        [1518-11-04 00:02] Guard #99 begins shift
        [1518-11-04 00:36] falls asleep
        [1518-11-04 00:46] wakes up
        [1518-11-05 00:03] Guard #99 begins shift
        [1518-11-05 00:45] falls asleep
        [1518-11-05 00:55] wakes up
    """.trimIndent()

    @Test
    fun `sleepiestGuard -- reference input -- guard 10`() {
        val guards = SleepyGuards(referenceInput)

        val sleepiest = guards.sleepiestGuard

        assertThat(sleepiest?.number).isEqualTo(10)
    }

    @Test
    fun `minutesAsleep -- sleepiest guard from reference input -- asleep for 50 minutes`() {
        val guards = SleepyGuards(referenceInput)

        val sleepiest = guards.sleepiestGuard

        assertThat(sleepiest?.minutesAsleep).isEqualTo(50)
    }

    @Test
    fun `sleepiestMinute -- sleepiest guard from reference input -- sleepiest on minute 24`() {
        val guards = SleepyGuards(referenceInput)

        val sleepiest = guards.sleepiestGuard

        assertThat(sleepiest?.sleepiestMinute).isEqualTo(24)
    }

    @Test
    fun `mostFrequentlyAsleepOnSameMinute -- reference input -- guard 99`() {
        val guards = SleepyGuards(referenceInput)

        val mostFrequentlyAsleep = guards.mostFrequentlyAsleepOnSameMinute

        assertThat(mostFrequentlyAsleep?.number).isEqualTo(99)
    }

    @Test
    fun `sleepiestMinute -- reference input, guard 99 -- minute 45`() {
        val guards = SleepyGuards(referenceInput)

        val mostFrequentlyAsleep = guards.mostFrequentlyAsleepOnSameMinute

        assertThat(mostFrequentlyAsleep?.sleepiestMinute).isEqualTo(45)
    }
}