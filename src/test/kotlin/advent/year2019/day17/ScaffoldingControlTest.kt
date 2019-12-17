package advent.year2019.day17

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScaffoldingControlTest {

    @Test
    fun `alignmentParameterSum -- reference input -- 76`() {
        val input = """
            ..#..........
            ..#..........
            #######...###
            #.#...#...#.#
            #############
            ..#...#...#..
            ..#####...^..
        """.trimIndent()
        val scaffolding = Scaffolding.parse(input)

        val sum = scaffolding.alignmentParameterSum()

        assertThat(sum).isEqualTo(76)
    }

    @Test
    fun `directions -- reference input -- reference direction list`() {
        val input = """
            #######...#####
            #.....#...#...#
            #.....#...#...#
            ......#...#...#
            ......#...###.#
            ......#.....#.#
            ^########...#.#
            ......#.#...#.#
            ......#########
            ........#...#..
            ....#########..
            ....#...#......
            ....#...#......
            ....#...#......
            ....#####......
        """.trimIndent()
        val scaffolding = Scaffolding.parse(input)

        val directions = scaffolding.directions()

        assertThat(directions).isEqualTo(listOf(
                "R", "8", "R", "8", "R", "4", "R", "4", "R", "8", "L", "6", "L", "2", "R", "4", "R", "4", "R", "8",
                "R", "8", "R", "8", "L", "6", "L", "2"))
    }
}