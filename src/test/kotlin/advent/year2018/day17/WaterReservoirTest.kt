package advent.year2018.day17

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WaterReservoirTest {

    @Test
    fun `waterTiles -- reference input -- has reference count`() {
        val input = """
            x=495, y=2..7
            y=7, x=495..501
            x=501, y=3..7
            x=498, y=2..4
            x=506, y=1..2
            x=498, y=10..13
            x=504, y=10..13
            y=13, x=498..504
        """.trimIndent()

        val reservoir = WaterReservoir.parse(input)

        assertThat(reservoir.waterTileCount()).isEqualTo(57)
        assertThat(reservoir.settledWaterCount()).isEqualTo(29)
    }

    @Test
    fun `waterTiles -- bucket in a bucket -- should fill up and overflow outer bucket`() {
        val input = """
            x=499, y=4..6
            y=6, x=500..500
            x=501, y=4..6
            x=497, y=3..8
            y=8, x=498..502
            x=503, y=3..8
            x=505, y=1..1
        """.trimIndent()

        val reservoir = WaterReservoir.parse(input)
        val actual = reservoir.display()

        val expected = """
            ....|......
            ....|....#.
            |||||||||..
            |#~~~~~#|..
            |#~#~#~#|..
            |#~#~#~#|..
            |#~###~#|..
            |#~~~~~#|..
            |#######|..
        """.trimIndent()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `waterTiles -- split and rejoin -- should nicely rejoin`() {
        val input = """
            x=499, y=2..3
            y=3, x=500..500
            x=501, y=2..3
            x=497, y=5..7
            y=7, x=498..502
            x=503, y=5..7
            x=505, y=1..1
        """.trimIndent()

        val reservoir = WaterReservoir.parse(input)
        val actual = reservoir.display()

        val expected = """
            ....|......
            ..|||||..#.
            ..|#~#|....
            ..|###|....
            |||||||||..
            |#~~~~~#|..
            |#~~~~~#|..
            |#######|..
        """.trimIndent()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `waterTiles -- split and rejoin into uneven basin -- should only overfill side by 1`() {
        val input = """
            x=499, y=2..3
            y=3, x=500..500
            x=501, y=2..3
            x=497, y=5..7
            y=7, x=498..502
            x=503, y=6..7
            x=505, y=1..1
        """.trimIndent()

        val reservoir = WaterReservoir.parse(input)
        val actual = reservoir.display()

        val expected = """
            ....|......
            ..|||||..#.
            ..|#~#|....
            ..|###|....
            ..|...|....
            .#|||||||..
            .#~~~~~#|..
            .#######|..
        """.trimIndent()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `waterTiles -- split and rejoin into nested basin -- should only overfill side by 1`() {
        val input = """
            x=499, y=2..3
            y=3, x=500..500
            x=501, y=2..3
            x=497, y=5..9
            y=9, x=498..502
            x=503, y=6..9
            x=505, y=1..1
            x=499, y=6..7
            x=500, y=7..7
            x=501, y=6..7
        """.trimIndent()

        val reservoir = WaterReservoir.parse(input)
        val actual = reservoir.display()

        val expected = """
            ....|......
            ..|||||..#.
            ..|#~#|....
            ..|###|....
            ..|...|....
            .#|||||||..
            .#~#~#~#|..
            .#~###~#|..
            .#~~~~~#|..
            .#######|..
        """.trimIndent()
        assertThat(actual).isEqualTo(expected)
    }
}