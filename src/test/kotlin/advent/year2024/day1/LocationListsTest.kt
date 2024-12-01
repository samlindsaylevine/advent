package advent.year2024.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocationListsTest {

    @Test
    fun `total distance -- reference input -- 11`() {
        val input = """
            3   4
            4   3
            2   5
            1   3
            3   9
            3   3
        """.trimIndent()
        val lists = LocationLists.of(input)

        val distance = lists.totalDistance()

        assertThat(distance).isEqualTo(11)
    }

    @Test
    fun `similarity score -- reference input -- 31`() {
        val input = """
            3   4
            4   3
            2   5
            1   3
            3   9
            3   3
        """.trimIndent()
        val lists = LocationLists.of(input)

        val score = lists.similarityScore()

        assertThat(score).isEqualTo(31)
    }
}