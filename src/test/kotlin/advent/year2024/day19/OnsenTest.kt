package advent.year2024.day19

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OnsenTest {
    @Test
    fun `possibleDesignCount -- reference input -- 6`() {
        val input = """
            r, wr, b, g, bwu, rb, gb, br

            brwrr
            bggr
            gbbr
            rrbgbr
            ubwu
            bwurrg
            brgr
            bbrgwb
        """.trimIndent()
        val onsen = Onsen(input)

        val count = onsen.possibleDesignCount()

        assertThat(count).isEqualTo(6)
    }
}