package advent.year2018.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChronalCoordinatesTest {

    private val referenceInput = """
            1, 1
            1, 6
            8, 3
            3, 4
            5, 5
            8, 9
        """.trimIndent()

    @Test
    fun `largestFiniteAreaSize -- reference input -- 17`() {
        val coordinates = ChronalCoordinates(referenceInput)

        val areaSize = coordinates.largestFiniteAreaSize()

        assertThat(areaSize).isEqualTo(17)
    }

    @Test
    fun `areaWithinTotalDistance -- reference input, distance 32 -- 16`() {
        val coordinates = ChronalCoordinates(referenceInput)

        val area = coordinates.areaWithinTotalDistance(32)

        assertThat(area).isEqualTo(16)
    }

    @Test
    fun `total distance -- reference input, 4, 3 -- 30`() {
        val coordinates = ChronalCoordinates(referenceInput)

        val distance = coordinates.totalDistance(Point(4, 3))

        assertThat(distance).isEqualTo(30)
    }
}