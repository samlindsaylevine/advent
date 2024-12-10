package advent.year2024.day10

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TopographicMapTest {

    @Test
    fun `trailheadScore -- reference input -- 36`() {
        val input = """
            89010123
            78121874
            87430965
            96549874
            45678903
            32019012
            01329801
            10456732
        """.trimIndent()
        val map = TopographicMap(input)

        val score = map.trailheadScore()

        assertThat(score).isEqualTo(36)
    }

    @Test
    fun `trailheadRating -- reference input -- 81`() {
        val input = """
            89010123
            78121874
            87430965
            96549874
            45678903
            32019012
            01329801
            10456732
        """.trimIndent()
        val map = TopographicMap(input)

        val score = map.trailheadRating()

        assertThat(score).isEqualTo(81)
    }
}