package advent.year2019.day7

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AmplifierControlsTest {

    @Test
    fun `optimalSettings -- first reference example -- reference values`() {
        val program = listOf(3, 15, 3, 16, 1002, 16, 10, 16, 1, 16, 15, 15, 4, 15, 99, 0, 0)
        val amplifierControls = AmplifierControls(program = program)

        val optimal = amplifierControls.optimalSettings()

        assertThat(optimal.settings).containsExactly(4, 3, 2, 1, 0)
        assertThat(optimal.thrusterSignal).isEqualTo(43210)
    }

    @Test
    fun `optimalSettings -- second reference example -- reference values`() {
        val program = listOf(3, 23, 3, 24, 1002, 24, 10, 24, 1002, 23, -1, 23,
                101, 5, 23, 23, 1, 24, 23, 23, 4, 23, 99, 0, 0)
        val amplifierControls = AmplifierControls(program = program)

        val optimal = amplifierControls.optimalSettings()

        assertThat(optimal.settings).containsExactly(0, 1, 2, 3, 4)
        assertThat(optimal.thrusterSignal).isEqualTo(54321)
    }

    @Test
    fun `optimalSettings -- third reference example -- reference values`() {
        val program = listOf(3, 31, 3, 32, 1002, 32, 10, 32, 1001, 31, -2, 31, 1007, 31, 0, 33,
                1002, 33, 7, 33, 1, 33, 31, 31, 1, 32, 31, 31, 4, 31, 99, 0, 0, 0)
        val amplifierControls = AmplifierControls(program = program)

        val optimal = amplifierControls.optimalSettings()

        assertThat(optimal.settings).containsExactly(1, 0, 4, 3, 2)
        assertThat(optimal.thrusterSignal).isEqualTo(65210)
    }
}