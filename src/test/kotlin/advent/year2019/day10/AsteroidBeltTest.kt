package advent.year2019.day10

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AsteroidBeltTest {

    @CsvSource("12, 119, 1",
            "119, 12, 1",
            "35, 15, 5",
            "15, 35, 5",
            "-100, 15, 5",
            "-100, -15, 5",
            "100, -15, 5")
    @ParameterizedTest(name = "gcd -- {0}, {1} -- {2}")
    fun `gcd -- some sample values -- give expected result`(a: Int, b: Int, expected: Int) {
        val gcd = gcd(a, b)

        assertThat(gcd).isEqualTo(expected)
    }

    @Test
    fun `mostObservable -- example one -- 8`() {
        val input = """
            .#..#
            .....
            #####
            ....#
            ...##
        """.trimIndent()
        val asteroids = AsteroidBelt.parse(input)

        val mostObservable = asteroids.mostObservable()

        assertThat(mostObservable?.count).isEqualTo(8)
    }

    @Test
    fun `mostObservable -- example two -- 33`() {
        val input = """
            ......#.#.
            #..#.#....
            ..#######.
            .#.#.###..
            .#..#.....
            ..#....#.#
            #..#....#.
            .##.#..###
            ##...#..#.
            .#....####
        """.trimIndent()
        val asteroids = AsteroidBelt.parse(input)

        val mostObservable = asteroids.mostObservable()

        assertThat(mostObservable?.count).isEqualTo(33)
    }

    @Test
    fun `mostObservable -- example three -- 35`() {
        val input = """
            #.#...#.#.
            .###....#.
            .#....#...
            ##.#.#.#.#
            ....#.#.#.
            .##..###.#
            ..#...##..
            ..##....##
            ......#...
            .####.###.
        """.trimIndent()
        val asteroids = AsteroidBelt.parse(input)

        val mostObservable = asteroids.mostObservable()

        assertThat(mostObservable?.count).isEqualTo(35)
    }

    @Test
    fun `mostObservable -- example four -- 41`() {
        val input = """
            .#..#..###
            ####.###.#
            ....###.#.
            ..###.##.#
            ##.##.#.#.
            ....###..#
            ..#.#..#.#
            #..#.#.###
            .##...##.#
            .....#.#..
        """.trimIndent()
        val asteroids = AsteroidBelt.parse(input)

        val mostObservable = asteroids.mostObservable()

        assertThat(mostObservable?.count).isEqualTo(41)
    }

    @Test
    fun `mostObservable -- example five -- 210`() {
        val input = """
            .#..##.###...#######
            ##.############..##.
            .#.######.########.#
            .###.#######.####.#.
            #####.##.#.##.###.##
            ..#####..#.#########
            ####################
            #.####....###.#.#.##
            ##.#################
            #####.##.###..####..
            ..######..##.#######
            ####.##.####...##..#
            .#####..#.######.###
            ##...#.##########...
            #.##########.#######
            .####.#.###.###.#.##
            ....##.##.###..#####
            .#.#.###########.###
            #.#.#.#####.####.###
            ###.##.####.##.#..##
        """.trimIndent()
        val asteroids = AsteroidBelt.parse(input)

        val mostObservable = asteroids.mostObservable()

        assertThat(mostObservable?.count).isEqualTo(210)
    }

    @Test
    fun `vaporizedAsteroids -- smaller example -- reference value`() {
        val input = """
            .#....#####...#..
            ##...##.#####..##
            ##...#...#.#####.
            ..#.....#...###..
            ..#.#.....#....##
        """.trimIndent()
        val asteroids = AsteroidBelt.parse(input)

        val mostObservable = asteroids.mostObservable()
        assertThat(mostObservable?.asteroid).isEqualTo(Point(8, 3))

        val vaporized = asteroids.vaporizedAsteroids()

        assertThat(vaporized).containsExactly(
                Point(8, 1),
                Point(9, 0),
                Point(9, 1),
                Point(10, 0),
                Point(9, 2),
                Point(11, 1),
                Point(12, 1),
                Point(11, 2),
                Point(15, 1),
                Point(12, 2),
                Point(13, 2),
                Point(14, 2),
                Point(15, 2),
                Point(12, 3),
                Point(16, 4),
                Point(15, 4),
                Point(10, 4),
                Point(4, 4),
                Point(2, 4),
                Point(2, 3),
                Point(0, 2),
                Point(1, 2),
                Point(0, 1),
                Point(1, 1),
                Point(5, 2),
                Point(1, 0),
                Point(5, 1),
                Point(6, 1),
                Point(6, 0),
                Point(7, 0),
                Point(8, 0),
                Point(10, 1),
                Point(14, 0),
                Point(16, 1),
                Point(13, 3),
                Point(14, 3)
        )
    }

    @Test
    fun `vaporizedAsteroids -- large example -- reference value`() {
        val input = """
            .#..##.###...#######
            ##.############..##.
            .#.######.########.#
            .###.#######.####.#.
            #####.##.#.##.###.##
            ..#####..#.#########
            ####################
            #.####....###.#.#.##
            ##.#################
            #####.##.###..####..
            ..######..##.#######
            ####.##.####...##..#
            .#####..#.######.###
            ##...#.##########...
            #.##########.#######
            .####.#.###.###.#.##
            ....##.##.###..#####
            .#.#.###########.###
            #.#.#.#####.####.###
            ###.##.####.##.#..##
        """.trimIndent()
        val asteroids = AsteroidBelt.parse(input)

        val vaporized = asteroids.vaporizedAsteroids()

        assertThat(vaporized[0]).isEqualTo(Point(11, 12))
        assertThat(vaporized[1]).isEqualTo(Point(12, 1))
        assertThat(vaporized[2]).isEqualTo(Point(12, 2))
        assertThat(vaporized[9]).isEqualTo(Point(12, 8))
        assertThat(vaporized[19]).isEqualTo(Point(16, 0))
        assertThat(vaporized[49]).isEqualTo(Point(16, 9))
        assertThat(vaporized[99]).isEqualTo(Point(10, 16))
        assertThat(vaporized[198]).isEqualTo(Point(9, 6))
        assertThat(vaporized[199]).isEqualTo(Point(8, 2))
        assertThat(vaporized[200]).isEqualTo(Point(10, 9))
        assertThat(vaporized[298]).isEqualTo(Point(11, 1))
    }

    @CsvSource("0, -1, 0",
            "1, -1, ${Math.PI / 4}",
            "1, 0, ${Math.PI / 2}",
            "1, 1, ${3 * Math.PI / 4}",
            "0, 1, ${Math.PI}",
            "-1, 1, ${5 * Math.PI / 4}",
            "-1, 0, ${3 * Math.PI / 2}",
            "-1, -1, ${7 * Math.PI / 4}")
    @ParameterizedTest(name = "theta -- ({0}, {1}) -- is {2}")
    fun `theta -- sample values -- sample result`(x: Int, y: Int, expected: Double) {
        val theta = Point(x, y).theta

        assertThat(theta).isCloseTo(expected, Offset.offset(0.000001))
    }
}