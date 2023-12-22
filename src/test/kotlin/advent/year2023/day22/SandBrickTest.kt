package advent.year2023.day22

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SandBrickTest {

  @Test
  fun `amount removable -- reference input -- 5`() {
    val input = """
      1,0,1~1,2,1
      0,0,2~2,0,2
      0,2,3~2,2,3
      0,0,4~0,2,4
      2,0,5~2,2,5
      0,1,6~2,1,6
      1,1,8~1,1,9
    """.trimIndent()
    val bricks = SandBricks(input)

    bricks.settle()
    val removableCount = bricks.removable().size
    bricks.removable().forEach(::println)

    assertThat(removableCount).isEqualTo(5)
  }


  @Test
  fun `dropped distances -- reference input, after settling -- as per example`() {
    val input = """
      1,0,1~1,2,1
      0,0,2~2,0,2
      0,2,3~2,2,3
      0,0,4~0,2,4
      2,0,5~2,2,5
      0,1,6~2,1,6
      1,1,8~1,1,9
    """.trimIndent()
    val bricks = SandBricks(input)

    bricks.settle()
    val droppedDistances = bricks.bricks.map { it.droppedBy }

    assertThat(droppedDistances).containsExactly(0, 0, 1, 1, 2, 2, 3)
  }
}