package advent.year2019.day8

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SpaceImageTest {

    @Test
    fun `toString() -- reference image -- reference result`() {
        val image = SpaceImage.parse("0222112222120000", 2, 2)

        val result = image.toString()

        val expected = " *\n* "
        assertThat(result).isEqualTo(expected)
    }

}