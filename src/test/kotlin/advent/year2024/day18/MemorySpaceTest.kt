package advent.year2024.day18

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MemorySpaceTest {

    private val input = """
            5,4
            4,2
            4,5
            3,0
            2,1
            6,3
            2,4
            1,5
            0,6
            3,3
            2,6
            5,1
            1,2
            5,5
            2,5
            6,5
            1,4
            0,4
            6,4
            1,1
            6,1
            1,0
            0,5
            1,6
            2,0
    """.trimIndent()


    @Test
    fun `draw -- reference example, at time 12 -- reference value`() {
        val space = MemorySpace(input, 6)

        val result = space.draw(12)

        val expected = """
            ...#...
            ..#..#.
            ....#..
            ...#..#
            ..#..#.
            .#..#..
            #.#....
        """.trimIndent()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `minimum steps -- reference example, 12 bytes fallen --  22`() {
        val space = MemorySpace(input, 6)

        val steps = space.minimumStepsAtTime(12)

        assertThat(steps).isEqualTo(22)
    }

    @Test
    fun `byteThatBlocksExit -- reference example -- 6,1`() {
        val space = MemorySpace(input, 6)

        val blocker = space.firstByteThatBlocksExit()

        assertThat(blocker).isEqualTo(Point(6, 1))
    }
}