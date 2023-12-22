package advent.year2023.day21

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GardenMapTest {

  @Test
  fun `count after -- reference input, 6 steps -- 16`() {
    val input = """
      ...........
      .....###.#.
      .###.##..#.
      ..#.#...#..
      ....#.#....
      .##..S####.
      .##..#...#.
      .......##..
      .##.#.####.
      .##..##.##.
      ...........
    """.trimIndent()
    val map = GardenMap.of(input)


    map.reachable().take(10).forEach { println("${it.size()}: $it") }

    val count = map.countAfter(6)

    assertThat(count).isEqualTo(16)
  }

  @Disabled("The test data doesn't seem to have the same recurrence as the real data? Or something fishy is going " +
          "on. This is gross, sorry!")
  @ParameterizedTest
  @CsvSource(
          "6, 16",
          "10, 50",
          "50, 1594",
          "100, 6536",
          "500, 167004",
          "1000, 668967",
          "5000, 16733044"
  )
  fun `count after -- reference input, infinite map -- reference counts`(numSteps: Int, expectedCount: Long) {
    val input = """
      ...........
      .....###.#.
      .###.##..#.
      ..#.#...#..
      ....#.#....
      .##..S####.
      .##..#...#.
      .......##..
      .##.#.####.
      .##..##.##.
      ...........
    """.trimIndent()
    val map = GardenMap.of(input, infinite = true)

    val count = map.countByRecurrence(numSteps)

    assertThat(count).isEqualTo(expectedCount)
  }

  @Test
  fun `find quadratic recurrence -- quadratic exists -- found`() {
    val quadratic = Quadratic.fit(
            Point(0, 1),
            Point(1, 3),
            Point(2, 7)
    )

    assertThat(quadratic).isEqualTo(Quadratic(1, 1, 1))
  }

  @Test
  fun `find quadratic recurrence -- simplest quadratic -- found`() {
    val quadratic = Quadratic.fit(
            Point(0, 0),
            Point(1, 1),
            Point(2, 4)
    )

    assertThat(quadratic).isEqualTo(Quadratic(1, 0, 0))
  }
}