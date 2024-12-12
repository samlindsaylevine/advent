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

    @Test
    fun `bulk discount price -- first example -- 80`() {
        val input = """
            AAAA
            BBCD
            BBCC
            EEEC
        """.trimIndent()
        val regions = GardenRegions(input)

        val price = regions.bulkDiscountPrice()

        assertThat(price).isEqualTo(80)
    }

    @Test
    fun `bulk discount price -- second example -- 436`() {
        val input = """
            OOOOO
            OXOXO
            OOOOO
            OXOXO
            OOOOO
        """.trimIndent()
        val regions = GardenRegions(input)

        val price = regions.bulkDiscountPrice()

        assertThat(price).isEqualTo(436)
    }

    @Test
    fun `bulk discount price -- E shaped -- 236`() {
        val input = """
            EEEEE
            EXXXX
            EEEEE
            EXXXX
            EEEEE
        """.trimIndent()
        val regions = GardenRegions(input)

        val price = regions.bulkDiscountPrice()

        assertThat(price).isEqualTo(236)
    }

    @Test
    fun `bulk discount price -- diagonally touching -- 368`() {
        val input = """
            AAAAAA
            AAABBA
            AAABBA
            ABBAAA
            ABBAAA
            AAAAAA
        """.trimIndent()
        val regions = GardenRegions(input)

        val price = regions.bulkDiscountPrice()

        assertThat(price).isEqualTo(368)
    }

    @Test
    fun `bulk discount price -- larger example -- 1206`() {
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

        val price = regions.bulkDiscountPrice()

        assertThat(price).isEqualTo(1206)
    }
}