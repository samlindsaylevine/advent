package advent.year2024.day11

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BlinkingStonesTest {

    @Test
    fun `blink -- 125 17 -- yields 253000 1 7`() {
        val input = "125 17"
        val stones = BlinkingStones(input)

        val blunk = stones.blink(1)

        val expected = mapOf(253000L to 1L, 1L to 1L, 7L to 1L)
        assertThat(blunk.countsByNumber).isEqualTo(expected)
    }

    @Test
    fun `stoneCount -- blinked 6 times -- 22 stones`() {
        val input = "125 17"
        val stones = BlinkingStones(input)

        val count = stones.blink(6).stoneCount()

        assertThat(count).isEqualTo(22)
    }

    @Test
    fun `stoneCount -- blinked 25 times -- 55312 stones`() {
        val input = "125 17"
        val stones = BlinkingStones(input)

        val count = stones.blink(25).stoneCount()

        assertThat(count).isEqualTo(55312)
    }
}