package advent.year2017.day25

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TuringMachineTest {

    @Test
    fun `execute -- reference program -- has reference checksum`() {
        val input = """
            Begin in state A.
            Perform a diagnostic checksum after 6 steps.

            In state A:
              If the current value is 0:
                - Write the value 1.
                - Move one slot to the right.
                - Continue with state B.
              If the current value is 1:
                - Write the value 0.
                - Move one slot to the left.
                - Continue with state B.

            In state B:
              If the current value is 0:
                - Write the value 1.
                - Move one slot to the left.
                - Continue with state A.
              If the current value is 1:
                - Write the value 1.
                - Move one slot to the right.
                - Continue with state A.
        """.trimIndent()
        val program = TuringProgramParser().parse(input)

        val result = program.execute()

        assertThat(result.checksum()).isEqualTo(3)
    }
}