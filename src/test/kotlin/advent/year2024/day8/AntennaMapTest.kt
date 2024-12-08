package advent.year2024.day8

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AntennaMapTest {

    @Test
    fun `antinode count -- reference input -- 14`() {
        val input = """
            ............
            ........0...
            .....0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............
        """.trimIndent()
        val map = AntennaMap.of(input)

        val count = map.antinodes().size

        assertThat(count).isEqualTo(14)
    }

    @Test
    fun `antinode count, with harmonics -- reference input -- 34`() {
        val input = """
            ............
            ........0...
            .....0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............
        """.trimIndent()
        val map = AntennaMap.of(input)

        val count = map.antinodes(harmonics = true).size

        assertThat(count).isEqualTo(34)
    }
}