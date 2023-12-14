package advent.year2023.day14

import advent.utils.Direction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RollingRocksTest {

  @Test
  fun `load -- input titled north -- 136`() {
    val input = """
      O....#....
      O.OO#....#
      .....##...
      OO.#O....O
      .O.....O#.
      O.#..O.#.#
      ..O..#O..O
      .......O..
      #....###..
      #OO..#....
    """.trimIndent()
    val rocks = RollingRocks.of(input)

    val load = rocks.tilted(Direction.N).load()

    assertThat(load).isEqualTo(136)
  }

  @Test
  fun `load -- input cycled 1000000000 times -- 64`() {
    val input = """
      O....#....
      O.OO#....#
      .....##...
      OO.#O....O
      .O.....O#.
      O.#..O.#.#
      ..O..#O..O
      .......O..
      #....###..
      #OO..#....
    """.trimIndent()
    val rocks = RollingRocks.of(input)

    val load = rocks.cycled(1000000000).load()

    assertThat(load).isEqualTo(64)
  }
}