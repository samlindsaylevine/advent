package advent.year2024.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CorruptedMemoryTest {

    @Test
    fun `multiplicationSum -- reference input -- 161`() {
        val input = """
            xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
        """.trimIndent()
        val memory = CorruptedMemory(input)

        val sum = memory.multiplicationSum()

        assertThat(sum).isEqualTo(161)
    }

    @Test
    fun `conditionalSum -- reference input -- 48`() {
        val input = """
            xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
        """.trimIndent()
        val memory = CorruptedMemory(input)

        val sum = memory.conditionalSum()

        assertThat(sum).isEqualTo(48)
    }
}