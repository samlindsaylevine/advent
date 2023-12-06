package advent.year2023.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BoatRacesTest {
    @Test
    fun `number of ways to beat the records -- reference input -- 288`() {
        val input = """
            Time:      7  15   30
            Distance:  9  40  200
        """.trimIndent()

        val races = BoatRaces.of(input)

        println(races.races)
        println(races.races.map { it.recordBeatingOptions() })
        val result = races.numberOfWaysToBeatTheRecords()

        assertThat(result).isEqualTo(288)
    }

    @Test
    fun `number of ways to beat the record -- one race, without kerning -- 71503`() {
        val input = """
            Time:      7  15   30
            Distance:  9  40  200
        """.trimIndent()

        val singleRace = BoatRace.of(input)
        val result = singleRace.recordBeatingOptions().size()

        assertThat(result).isEqualTo(71503)
    }
}