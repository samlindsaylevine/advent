package advent.year2024.day4

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WordSearchTest {

    @Test
    fun `count -- reference input, XMAS -- 18`() {
        val input = """
            MMMSXXMASM
            MSAMXMSMSA
            AMXSXMAAMM
            MSAMASMSMX
            XMASAMXAMM
            XXAMMXXAMA
            SMSMSASXSS
            SAXAMASAAA
            MAMMMXMMMM
            MXMXAXMASX
        """.trimIndent()
        val wordSearch = WordSearch(input)

        val count = wordSearch.count("XMAS")

        assertThat(count).isEqualTo(18)
    }

    @Test
    fun `count crosses -- reference input -- 9`() {
        val input = """
            MMMSXXMASM
            MSAMXMSMSA
            AMXSXMAAMM
            MSAMASMSMX
            XMASAMXAMM
            XXAMMXXAMA
            SMSMSASXSS
            SAXAMASAAA
            MAMMMXMMMM
            MXMXAXMASX
        """.trimIndent()
        val wordSearch = WordSearch(input)

        val count = wordSearch.countCrosses()

        assertThat(count).isEqualTo(9)
    }
}