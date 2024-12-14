package advent.year2024.day14

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RestroomRobotsTest {

    @Test
    fun `robot positions -- reference input, after 100 seconds -- as in example`() {
        val input = """
            p=0,4 v=3,-3
            p=6,3 v=-1,-3
            p=10,3 v=-1,2
            p=2,0 v=2,-1
            p=0,0 v=1,3
            p=3,0 v=-2,-2
            p=7,6 v=-1,-3
            p=3,0 v=-1,-2
            p=9,3 v=2,3
            p=7,3 v=-1,2
            p=2,4 v=2,-3
            p=9,5 v=-3,-3
        """.trimIndent()
        val smallRoom = RestroomRobotRoom(width = 11, height = 7)
        val robots = RestroomRobots.of(input, smallRoom)
        val next = robots.next(100)

        val image = (0 until smallRoom.height).map { y ->
            (0 until smallRoom.width).map { x -> next.robots.count { it.position == Point(x, y) } }
                .map { if (it == 0) '.' else it.digitToChar() }
                .joinToString(separator = "")
        }.joinToString(separator = "\n")

        val expected = """
            ......2..1.
            ...........
            1..........
            .11........
            .....1.....
            ...12......
            .1....1....
        """.trimIndent()
        assertThat(image).isEqualTo(expected)
    }

    @Test
    fun `safety factor -- reference input, after 100 seconds -- 12`() {
        val input = """
            p=0,4 v=3,-3
            p=6,3 v=-1,-3
            p=10,3 v=-1,2
            p=2,0 v=2,-1
            p=0,0 v=1,3
            p=3,0 v=-2,-2
            p=7,6 v=-1,-3
            p=3,0 v=-1,-2
            p=9,3 v=2,3
            p=7,3 v=-1,2
            p=2,4 v=2,-3
            p=9,5 v=-3,-3
        """.trimIndent()
        val smallRoom = RestroomRobotRoom(width = 11, height = 7)
        val robots = RestroomRobots.of(input, smallRoom)

        val next = robots.next(100)
        next.robots.forEach { println(it.position) }
        val safety = robots.next(100).safetyFactor()

        assertThat(safety).isEqualTo(12)
    }
}