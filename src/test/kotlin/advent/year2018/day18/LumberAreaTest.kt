package advent.year2018.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LumberAreaTest {

    @Test
    fun `advanced -- reference input -- reference result`() {
        val input = """
            .#.#...|#.
            .....#|##|
            .|..|...#.
            ..|#.....#
            #.#|||#|#|
            ...#.||...
            .|....|...
            ||...#|.#|
            |.||||..|.
            ...#.|..|.
        """.trimIndent()
        val area = LumberArea.parse(input)

        val actual = area.advanced(times = 10)

        val expected = """
            .||##.....
            ||###.....
            ||##......
            |##.....##
            |##.....##
            |##....##|
            ||##.####|
            ||#####|||
            ||||#|||||
            ||||||||||
        """.trimIndent()
        assertThat(actual.toString()).isEqualTo(expected)
        assertThat(actual.resourceValue()).isEqualTo(1147)
    }
}