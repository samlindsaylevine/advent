package advent.year2017.day8

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RegisterInstructionsTest {

    private val referenceInput = """b inc 5 if a > 1
a inc 1 if b < 5
c dec -10 if a >= 1
c inc -20 if c == 10"""

    @Test
    fun `largestRegisterValue -- reference input -- is 1`() {
        val input = RegisterInstructions(referenceInput)

        val largestValue = input.largestRegisterValue()

        assertThat(largestValue).isEqualTo(1)
    }

    @Test
    fun `highestValueEverHeld -- reference input -- is 10`() {
        val input = RegisterInstructions(referenceInput)

        val largestValue = input.highestValueEverHeld()

        assertThat(largestValue).isEqualTo(10)
    }
}