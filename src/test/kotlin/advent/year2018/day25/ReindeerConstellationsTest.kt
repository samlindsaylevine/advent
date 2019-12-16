package advent.year2018.day25

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReindeerConstellationsTest {

    @Test
    fun `numConstellations -- first example -- is 2`() {
        val input = """
             0,0,0,0
             3,0,0,0
             0,3,0,0
             0,0,3,0
             0,0,0,3
             0,0,0,6
             9,0,0,0
            12,0,0,0
        """.trimIndent()
        val constellations = ReindeerConstellations.parse(input)

        val count = constellations.numConstellations()

        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `numConstellations -- second example -- is 4`() {
        val input = """
            -1,2,2,0
            0,0,2,-2
            0,0,0,-2
            -1,2,0,0
            -2,-2,-2,2
            3,0,2,-1
            -1,3,2,2
            -1,0,-1,0
            0,2,1,-2
            3,0,0,0
        """.trimIndent()
        val constellations = ReindeerConstellations.parse(input)

        val count = constellations.numConstellations()

        assertThat(count).isEqualTo(4)
    }

    @Test
    fun `numConstellations -- third example -- is 3`() {
        val input = """
            1,-1,0,1
            2,0,-1,0
            3,2,-1,0
            0,0,3,1
            0,0,-1,-1
            2,3,-2,0
            -2,2,0,0
            2,-2,0,-1
            1,-1,0,-1
            3,2,0,2
        """.trimIndent()
        val constellations = ReindeerConstellations.parse(input)

        val count = constellations.numConstellations()

        assertThat(count).isEqualTo(3)
    }

    @Test
    fun `numConstellations -- fourth example -- is 8`() {
        val input = """
            1,-1,-1,-2
            -2,-2,0,1
            0,2,1,3
            -2,3,-2,1
            0,2,3,-2
            -1,-1,1,-2
            0,-2,-1,0
            -2,2,3,-1
            1,2,2,0
            -1,-2,0,-2
        """.trimIndent()
        val constellations = ReindeerConstellations.parse(input)

        val count = constellations.numConstellations()

        assertThat(count).isEqualTo(8)
    }
}