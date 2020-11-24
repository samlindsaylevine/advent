package advent.year2019.day5

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class IntcodeComputerTest {

  @Test
  fun `execute -- input and output example -- outputs input`() {
    val program = listOf<Long>(3, 0, 4, 0, 99)

    val result = IntcodeComputer().execute(program, input = { 1000 })

    assertThat(result.output).containsExactly(1000)
  }

  @Test
  fun `execute -- parameter modes example -- operates as example defines`() {
    val program = listOf<Long>(1002, 4, 3, 4, 33)

    val result = IntcodeComputer().execute(program)

    assertThat(result.finalState.asList()).containsExactly(1002, 4, 3, 4, 99)
  }

  @Test
  fun `execute -- negative integer example -- operates as example defines`() {
    val program = listOf<Long>(1101, 100, -1, 4, 0)

    val result = IntcodeComputer().execute(program)

    assertThat(result.finalState.asList()).containsExactly(1101, 100, -1, 4, 99)
  }

  @ParameterizedTest(name = "equal to 8 position program -- input {0} -- outputs {1}")
  @CsvSource("7, 0",
          "8, 1",
          "9, 0")
  fun `equal to 8 position program -- reference inputs -- reference outputs`(input: Long, expected: Long) {
    val program = listOf<Long>(3, 9, 8, 9, 10, 9, 4, 9, 99, -1, 8)

    val result = IntcodeComputer().execute(program, input = { input })

    assertThat(result.output).containsExactly(expected)
  }

  @ParameterizedTest(name = "less than 8 position program -- input {0} -- outputs {1}")
  @CsvSource("7, 1",
          "8, 0",
          "9, 0")
  fun `less than 8 position program -- reference inputs -- reference outputs`(input: Long, expected: Long) {
    val program = listOf<Long>(3, 9, 7, 9, 10, 9, 4, 9, 99, -1, 8)

    val result = IntcodeComputer().execute(program, input = { input })

    assertThat(result.output).containsExactly(expected)
  }

  @ParameterizedTest(name = "equal to 8 immediate program -- input {0} -- outputs {1}")
  @CsvSource("7, 0",
          "8, 1",
          "9, 0")
  fun `equal to 8 immediate program -- reference inputs -- reference outputs`(input: Long, expected: Long) {
    val program = listOf<Long>(3, 3, 1108, -1, 8, 3, 4, 3, 99)

    val result = IntcodeComputer().execute(program, input = { input })

    assertThat(result.output).containsExactly(expected)
  }

  @ParameterizedTest(name = "less than 8 immediate program -- input {0} -- outputs {1}")
  @CsvSource("7, 1",
          "8, 0",
          "9, 0")
  fun `less than 8 immediate program -- reference inputs -- reference outputs`(input: Long, expected: Long) {
    val program = listOf<Long>(3, 3, 1107, -1, 8, 3, 4, 3, 99)

    val result = IntcodeComputer().execute(program, input = { input })

    assertThat(result.output).containsExactly(expected)
  }

  @ParameterizedTest(name = "non-zero position program -- input {0} -- outputs {1}")
  @CsvSource("-100, 1",
          "0, 0",
          "9, 1")
  fun `non-zero position program -- reference inputs -- reference outputs`(input: Long, expected: Long) {
    val program = listOf<Long>(3, 12, 6, 12, 15, 1, 13, 14, 13, 4, 13, 99, -1, 0, 1, 9)

    val result = IntcodeComputer().execute(program, input = { input })

    assertThat(result.output).containsExactly(expected)
  }

  @ParameterizedTest(name = "non-zero immediate program -- input {0} -- outputs {1}")
  @CsvSource("-100, 1",
          "0, 0",
          "9, 1")
  fun `non-zero immediate program -- reference inputs -- reference outputs`(input: Long, expected: Long) {
    val program = listOf<Long>(3, 3, 1105, -1, 9, 1101, 0, 0, 12, 4, 12, 99, 1)

    val result = IntcodeComputer().execute(program, input = { input })

    assertThat(result.output).containsExactly(expected)
  }

  @ParameterizedTest(name = "larger example -- input {0} -- outputs {1}")
  @CsvSource("7, 999",
          "8, 1000",
          "9, 1001")
  fun `larger example -- reference inputs -- reference outputs`(input: Long, expected: Long) {
    val program = listOf<Long>(3, 21, 1008, 21, 8, 20, 1005, 20, 22, 107, 8, 21, 20, 1006, 20, 31,
            1106, 0, 36, 98, 0, 0, 1002, 21, 125, 20, 4, 20, 1105, 1, 46, 104,
            999, 1105, 1, 46, 1101, 1000, 1, 20, 4, 20, 1105, 1, 46, 98, 99)

    val result = IntcodeComputer().execute(program, input = { input })

    assertThat(result.output).containsExactly(expected)
  }

  @Test
  fun `list as input -- a few elements -- returns expected`() {
    val list = listOf("A", "B", "C")

    val input = list.asInput()

    assertThat(input()).isEqualTo("A")
    assertThat(input()).isEqualTo("B")
    assertThat(input()).isEqualTo("C")
    val t = catchThrowable { input() }

    assertThat(t).isInstanceOf(IndexOutOfBoundsException::class.java)
  }

}