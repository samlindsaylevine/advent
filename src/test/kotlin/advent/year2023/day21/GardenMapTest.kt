package advent.year2023.day21

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

    val count = map.countAfter(6)

    assertThat(count).isEqualTo(16)
  }

  @Disabled("Part 2 not yet working!")
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
}