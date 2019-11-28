package advent.year2018.day16

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChronalComputerTest {

    @Test
    fun `parse sample -- first item in file -- gives expected output`() {
        val input = """
            Before: [1, 1, 0, 3]
            3 0 2 0
            After:  [0, 1, 0, 3]""".trimIndent()

        val sample = SampleComputation.parse(input)

        assertThat(sample.beforeRegisters).containsExactly(1, 1, 0, 3)
        assertThat(sample.instruction.opCode).isEqualTo(3)
        assertThat(sample.instruction.inputA).isEqualTo(0)
        assertThat(sample.instruction.inputB).isEqualTo(2)
        assertThat(sample.instruction.output).isEqualTo(0)
        assertThat(sample.afterRegisters).containsExactly(0, 1, 0, 3)
    }

    @Test
    fun `behavesLike -- reference sample -- reference behavesLike`() {
        val input = """
            Before: [3, 2, 1, 1]
            9 2 1 2
            After:  [3, 2, 2, 1]
        """.trimIndent()
        val sample = SampleComputation.parse(input)

        assertThat(sample.behavesLike(ChronalOpCode.mulr)).isTrue()
        assertThat(sample.behavesLike(ChronalOpCode.addi)).isTrue()
        assertThat(sample.behavesLike(ChronalOpCode.seti)).isTrue()
        assertThat(sample.behavesLikeCount()).isEqualTo(3)
    }
}