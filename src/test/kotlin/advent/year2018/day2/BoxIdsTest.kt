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

    @Test
    fun `lettersInCommon -- differing by one character -- has all other characters`() {
        val first = BoxId("fghij")
        val second = BoxId("fguij")

        val inCommon = first.lettersInCommonWith(second)

        assertThat(inCommon).isEqualTo("fgij")
    }

    @Test
    fun `lettersInCommonBetweenCorrectBoxIds -- reference input -- has reference output`() {
        val ids = BoxIds(listOf(
                "abcde",
                "fghij",
                "klmno",
                "pqrst",
                "fguij",
                "axcye",
                "wvxyz"))

        val letters = ids.lettersInCommonBetweenCorrectBoxIds

        assertThat(letters).isEqualTo("fgij")
    }
}