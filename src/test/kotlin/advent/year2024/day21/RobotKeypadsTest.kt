package advent.year2024.day21

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RobotKeypadsTest {

    @Test
    fun `length of shortest sequence -- 029A -- 68`() {
        val keypads = RobotKeypads("")

        val length = keypads.lengthOfShortestSequence("029A")

        assertThat(length).isEqualTo(68)
    }

    @Test
    fun `complexity sum -- reference input -- 126384`() {
        val input = """
            029A
            980A
            179A
            456A
            379A
        """.trimIndent()
        val keypads = RobotKeypads(input)

        val sum = keypads.complexitySum()

        assertThat(sum).isEqualTo(126384)
    }
}