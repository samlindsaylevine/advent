package advent.year2021.day2

import advent.utils.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SubmarineCommandTest {

  @Test
  fun `reference input -- final position -- 15, 10`() {
    val commands = listOf(
      "forward 5",
      "down 5",
      "forward 8",
      "up 3",
      "down 8",
      "forward 2"
    ).map(SubmarineCommand::parse)

    val finalPosition = commands.finalPosition()

    assertThat(finalPosition).isEqualTo(Point(15, 10))
  }

  @Test
  fun `reference input -- complicated position -- 15, 60`() {
    val commands = listOf(
      "forward 5",
      "down 5",
      "forward 8",
      "up 3",
      "down 8",
      "forward 2"
    ).map(SubmarineCommand::parse)

    val finalState = commands.complicatedPosition()

    assertThat(finalState.position).isEqualTo(Point(15, 60))
  }
}