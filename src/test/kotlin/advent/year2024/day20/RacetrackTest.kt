package advent.year2024.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RacetrackTest {

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
        val input = """
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
        val racetrack = Racetrack.of(input)

        val count = racetrack.countCheatsSaving(amount)

        assertThat(count).isEqualTo(expected)
    }
}