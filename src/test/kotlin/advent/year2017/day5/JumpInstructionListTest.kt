package advent.year2017.day5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JumpInstructionListTest {

    @Test
    fun `stepsToExitIncrementing -- 0, 3, 0, 1, -3 -- 5 steps`() {
        val instructions = JumpInstructionList(listOf(0, 3, 0, 1, -3))

        assertThat(instructions.stepsToExitIncrementing()).isEqualTo(5)
    }

    @Test
    fun `stepsToExitStranger -- 0, 3, 0, 1, -3 -- 10 steps`() {
        val instructions = JumpInstructionList(listOf(0, 3, 0, 1, -3))

        assertThat(instructions.stepsToExitStranger()).isEqualTo(10)
    }
}