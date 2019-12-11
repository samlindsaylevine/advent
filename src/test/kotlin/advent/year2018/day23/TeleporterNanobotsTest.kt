package advent.year2018.day23

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeleporterNanobotsTest {

    @Test
    fun `botCountInRangeOfStrongest -- reference input -- is 7`() {
        val input = """
            pos=<0,0,0>, r=4
            pos=<1,0,0>, r=1
            pos=<4,0,0>, r=3
            pos=<0,2,0>, r=1
            pos=<0,5,0>, r=3
            pos=<0,0,3>, r=1
            pos=<1,1,1>, r=1
            pos=<1,1,2>, r=1
            pos=<1,3,1>, r=1
        """.trimIndent()
        val nanobots = TeleporterNanobots.parse(input)

        val inRange = nanobots.botCountInRangeOfStrongest()

        assertThat(inRange).isEqualTo(7)
    }
}