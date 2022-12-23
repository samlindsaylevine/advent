package advent.year2022.day23

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlantingElvesTest {

  private val input = """
    ....#..
    ..###.#
    #...#.#
    .#...##
    #.###..
    ##.#.##
    .#..#..
  """.trimIndent()

  @Test
  fun `empty ground -- reference input, 10 rounds -- 110`() {
    val elves = PlantingElves(input)

    val completed = elves.after(10)

    assertThat(completed.emptyGround()).isEqualTo(110)
  }

  @Test
  fun `first no move round number -- reference input -- 20`() {
    val elves = PlantingElves(input)

    assertThat(elves.firstNoMoveRound()).isEqualTo(20)
  }
}