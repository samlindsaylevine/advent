package advent.year2022.day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PasswordBoardTest {

  private val input = """
            ...#
            .#..
            #...
            ....
    ...#.......#
    ........#...
    ..#....#....
    ..........#.
            ...#....
            .....#..
            .#......
            ......#.
    
    10R5L5R10L4R5L5
  """.trimIndent()

  @Test
  fun `password -- reference input -- 6032`() {
    val board = PasswordBoard(input)

    assertThat(board.password()).isEqualTo(6032)
  }
}