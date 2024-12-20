package advent.year2024.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RacetrackTest {

    private val input = """
            ###############
            #...#...#.....#
            #.#.#.#.#.###.#
            #S#...#.#.#...#
            #######.#.#.###
            #######.#.#...#
            #######.#.###.#
            ###..E#...#...#
            ###.#######.###
            #...###...#...#
            #.#####.#.###.#
            #.#...#.#.#...#
            #.#.#.#.#.#.###
            #...#...#...###
            ###############
        """.trimIndent()

    @ParameterizedTest
    @CsvSource(
        "2, 14",
        "4, 14",
        "6, 2",
        "8, 4",
        "10, 2",
        "12, 3",
        "20, 1",
        "38, 1",
        "40, 1",
        "64, 1"
    )
    fun `cheats saving -- reference amounts -- reference count`(amount: Int, expected: Int) {
        val racetrack = Racetrack.of(input)

        val count = racetrack.countCheatsSaving(amount)

        assertThat(count).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "50, 32",
        "52, 31",
        "54, 29",
        "56, 39",
        "58, 25",
        "60, 23",
        "62, 20",
        "64, 19",
        "66, 12",
        "68, 14",
        "70, 12",
        "72, 22",
        "74, 4",
        "76, 3"
    )
    fun `cheats saving -- reference amounts, up to 20 distance -- reference count`(amount: Int, expected: Int) {
        val racetrack = Racetrack.of(input)

        val count = racetrack.countCheatsSaving(amount, cheatDistance = 20)

        assertThat(count).isEqualTo(expected)
    }
}