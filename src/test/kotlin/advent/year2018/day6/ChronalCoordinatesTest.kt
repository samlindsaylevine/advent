package advent.year2018.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChronalCoordinatesTest {

    @Test
    fun `largestFiniteAreaSize -- reference input -- 17`() {
        val input = """
            1, 1
            1, 6
            8, 3
            3, 4
            5, 5
            8, 9
        """.trimIndent()
        val coordinates = ChronalCoordinates(input)

        val areaSize = coordinates.largestFiniteAreaSize()

        assertThat(areaSize).isEqualTo(17)
    }
}