package advent.year2018.day2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BoxIdsTest {

    @Test
    fun `checksum -- reference box IDs -- has reference checksum`() {
        val ids = BoxIds(listOf(
                "abcdef",
                "bababc",
                "abbcde",
                "abcccd",
                "aabcdd",
                "abcdee",
                "ababab"))

        val checksum = ids.checksum

        assertThat(checksum).isEqualTo(12)
    }
}