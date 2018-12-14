package advent.year2018.day7

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SleighInstructionsTest {

    private val referenceInput = """
        Step C must be finished before step A can begin.
        Step C must be finished before step F can begin.
        Step A must be finished before step B can begin.
        Step A must be finished before step D can begin.
        Step B must be finished before step E can begin.
        Step D must be finished before step E can begin.
        Step F must be finished before step E can begin.
    """.trimIndent()

    @Test
    fun `stepsInOrder -- reference input -- CABDFE`() {
        val instructions = SleighInstructions(referenceInput)

        val steps = instructions.stepsInOrder()

        assertThat(steps).containsExactly("C", "A", "B", "D", "F", "E")
    }

    @Test
    fun `timeToComplete -- reference input, 2 workers, 0 base duration -- 15 seconds`() {
        val instructions = SleighInstructions(referenceInput, numWorkers = 2, baseStepDuration = 0)

        val time = instructions.timeToComplete()

        assertThat(time).isEqualTo(15)
    }
}