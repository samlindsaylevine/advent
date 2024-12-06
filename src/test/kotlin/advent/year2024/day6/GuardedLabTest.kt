package advent.year2024.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GuardedLabTest {

    @Test
    fun `distinctGuardPositions -- reference input -- 41`() {
        val input = """
            ....#.....
            .........#
            ..........
            ..#.......
            .......#..
            ..........
            .#..^.....
            ........#.
            #.........
            ......#...
        """.trimIndent()
        val lab = GuardedLab.of(input)

        val count = lab.distinctGuardPositions().size

        assertThat(count).isEqualTo(41)
    }

    @Test
    fun `loopCausingPositions -- reference input -- 6`() {
        val input = """
            ....#.....
            .........#
            ..........
            ..#.......
            .......#..
            ..........
            .#..^.....
            ........#.
            #.........
            ......#...
        """.trimIndent()
        val lab = GuardedLab.of(input)

        val count = lab.loopCausingPositions()

        assertThat(count).isEqualTo(6)
    }
}