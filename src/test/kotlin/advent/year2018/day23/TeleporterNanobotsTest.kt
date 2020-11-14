package advent.year2018.day23

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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

    @ParameterizedTest(name = "intRange split -- {0} to {1} -- yields {2} to {3} and {4} to {5}")
    @CsvSource("1, 10, 1, 5, 6, 10",
            "1, 3, 1, 2, 3, 3",
            "1, 2, 1, 1, 2, 2")
    fun `intRange split -- sample values -- sample output`(inputMin: Int,
                                                           inputMax: Int,
                                                           firstMin: Int,
                                                           firstMax: Int,
                                                           secondMin: Int,
                                                           secondMax: Int) {
        val input = inputMin..inputMax

        val result = input.split()

        assertThat(result).isEqualTo(firstMin..firstMax to secondMin..secondMax)
    }

    @Test
    fun `originDistanceOfMostOverlap -- reference input -- is 36`() {
        val input = """
            pos=<10,12,12>, r=2
            pos=<12,14,12>, r=2
            pos=<16,12,12>, r=4
            pos=<14,14,14>, r=6
            pos=<50,50,50>, r=200
            pos=<10,10,10>, r=5
        """.trimIndent()
        val nanobots = TeleporterNanobots.parse(input)

        val distance = nanobots.originDistanceOfMostOverlap()

        assertThat(distance).isEqualTo(36)
    }
}