package advent.year2019.day9

import advent.year2019.day5.IntcodeComputer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SensorBoostTest {

  @Test
  fun `execute -- quine -- outputs itself`() {
    val computer = IntcodeComputer()
    val program = listOf<Long>(109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99)

    val result = computer.execute(program)

    assertThat(result.output).isEqualTo(program)
  }

  @Test
  fun `execute -- second reference program -- outputs 16 digit number`() {
    val computer = IntcodeComputer()
    val program = listOf<Long>(1102, 34915192, 34915192, 7, 4, 7, 99, 0)

    val result = computer.execute(program)

    assertThat(result.output).hasSize(1)
    assertThat(result.output.first().toString()).hasSize(16)
  }

  @Test

  fun `execute -- third reference program -- outputs large number`() {
    val computer = IntcodeComputer()
    val program = listOf(104, 1125899906842624L, 99)

    val result = computer.execute(program)

    assertThat(result.output).containsExactly(1125899906842624L)
  }
}