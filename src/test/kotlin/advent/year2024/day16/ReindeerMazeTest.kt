package advent.year2024.day16

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReindeerMazeTest {

    @Test
    fun `lowest score -- first example -- 7036`() {
        val input = """
            ###############
            #.......#....E#
            #.#.###.#.###.#
            #.....#.#...#.#
            #.###.#####.#.#
            #.#.#.......#.#
            #.#.#####.###.#
            #...........#.#
            ###.#.#####.#.#
            #...#.....#.#.#
            #.#.#.###.#.#.#
            #.....#...#.#.#
            #.###.#.#.#.#.#
            #S..#.....#...#
            ###############
        """.trimIndent()
        val maze = ReindeerMaze.of(input)

        val score = maze.shortestPaths().lowestScore()

        assertThat(score).isEqualTo(7036)
    }

    @Test
    fun `lowest score -- second example -- 11048`() {
        val input = """
            #################
            #...#...#...#..E#
            #.#.#.#.#.#.#.#.#
            #.#.#.#...#...#.#
            #.#.#.#.###.#.#.#
            #...#.#.#.....#.#
            #.#.#.#.#.#####.#
            #.#...#.#.#.....#
            #.#.#####.#.###.#
            #.#.#.......#...#
            #.#.###.#####.###
            #.#.#...#.....#.#
            #.#.#.#####.###.#
            #.#.#.........#.#
            #.#.#.#########.#
            #S#.............#
            #################
        """.trimIndent()
        val maze = ReindeerMaze.of(input)

        val score = maze.shortestPaths().lowestScore()

        assertThat(score).isEqualTo(11048)
    }

    @Test
    fun `unique tiles  -- first example -- 45`() {
        val input = """
            ###############
            #.......#....E#
            #.#.###.#.###.#
            #.....#.#...#.#
            #.###.#####.#.#
            #.#.#.......#.#
            #.#.#####.###.#
            #...........#.#
            ###.#.#####.#.#
            #...#.....#.#.#
            #.#.#.###.#.#.#
            #.....#...#.#.#
            #.###.#.#.#.#.#
            #S..#.....#...#
            ###############
        """.trimIndent()
        val maze = ReindeerMaze.of(input)

        val count = maze.shortestPaths().uniqueTileCount()

        assertThat(count).isEqualTo(45)
    }

    @Test
    fun `unique tiles -- second example -- 64`() {
        val input = """
            #################
            #...#...#...#..E#
            #.#.#.#.#.#.#.#.#
            #.#.#.#...#...#.#
            #.#.#.#.###.#.#.#
            #...#.#.#.....#.#
            #.#.#.#.#.#####.#
            #.#...#.#.#.....#
            #.#.#####.#.###.#
            #.#.#.......#...#
            #.#.###.#####.###
            #.#.#...#.....#.#
            #.#.#.#####.###.#
            #.#.#.........#.#
            #.#.#.#########.#
            #S#.............#
            #################
        """.trimIndent()
        val maze = ReindeerMaze.of(input)

        val count = maze.shortestPaths().uniqueTileCount()

        assertThat(count).isEqualTo(64)
    }
}