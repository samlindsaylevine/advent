package advent.year2024.day12

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GardenRegionsTest {

    @Test
    fun `price -- first example -- 140`() {
        val input = """
            AAAA
            BBCD
            BBCC
            EEEC
        """.trimIndent()
        val regions = GardenRegions(input)

        val price = regions.price()

        assertThat(price).isEqualTo(140)
    }

    @Test
    fun `price -- second example -- 772`() {
        val input = """
            OOOOO
            OXOXO
            OOOOO
            OXOXO
            OOOOO
        """.trimIndent()
        val regions = GardenRegions(input)

        val price = regions.price()

        assertThat(price).isEqualTo(772)
    }

    @Test
    fun `price -- larger example -- 1930`() {
        val input = """
            RRRRIICCFF
            RRRRIICCCF
            VVRRRCCFFF
            VVRCCCJFFF
            VVVVCJJCFE
            VVIVCCJJEE
            VVIIICJJEE
            MIIIIIJJEE
            MIIISIJEEE
            MMMISSJEEE
        """.trimIndent()
        val regions = GardenRegions(input)

        val price = regions.price()

        assertThat(price).isEqualTo(1930)
    }
}