package advent.year2017.day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class VirusGridTest {

    @ParameterizedTest(name = "after \"{0}\" bursts -- with reference input -- \"{1}\" of them caused an infection")
    @CsvSource("7, 5",
            "70, 41",
            "10000, 5587")
    fun `after x bursts -- with reference input -- y of them caused an infection`(numBursts: Int,
                                                                                  expected: Int) {
        val referenceGrid = VirusGrid.fromInput("""
            ..#
            #..
            ...
        """.trimIndent())

        val result = CarrierTrip(referenceGrid).afterBursts(numBursts)

        assertThat(result.causedAnInfectionBursts).isEqualTo(expected)
    }

    @ParameterizedTest(name = "in the parsed input grid -- position \"{0}\", \"{1}\" -- is infected \"{2}\"")
    @CsvSource("-12, 12, false",
            "-11, 12, false",
            "-10, 12, false",
            "-9, 12, true",
            "-8, 12, false",
            "-7, 12, true",
            "11, -12, true",
            "12, -12, false")
    fun `parsing input grid -- test is infected -- get expected`(x: Int, y: Int, expected: Boolean) {
        val input = """
            ...#.#.####.....#.##..###
            ##.#.###..#.....#.##...#.
            ..#.##..#.#.##.#...#..###
            ###...##....###.#..#...#.
            ...#..#.........##..###..
            #..#.#.#.#.#.#.#.##.####.
            #...#.##...###...##..#..#
            ##...#.###..###...####.##
            ###..#.#####.##..###.#.##
            #..#....#.##..####...####
            ...#.#......###.#..#..##.
            .#.#...##.#.#####..###.#.
            .....#..##..##..###....##
            #.#..###.##.##.#####.##..
            ###..#..###.##.#..#.##.##
            .#######.###....######.##
            ..#.#.###.##.##...###.#..
            #..#.####...###..###..###
            #...#..###.##..##...#.#..
            ........###..#.#.##..##..
            .#############.#.###..###
            ##..#.###....#.#..#..##.#
            ..#.#.#####....#..#####..
            .#.#..#...#...##.#..#....
            ##.#..#..##........#..##.
        """.trimIndent()

        val grid = VirusGrid.fromInput(input)
        val isInfected = grid.isInfected(x, y)

        assertThat(isInfected).isEqualTo(expected)
    }

    @Test
    fun `turn right -- from LEFT -- get UP`() {
        assertThat(Direction.LEFT.right()).isEqualTo(Direction.UP)
    }

    @Test
    fun `turn left -- from UP -- get LEFT`() {
        assertThat(Direction.LEFT.right()).isEqualTo(Direction.UP)
    }
}