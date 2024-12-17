package advent.year2024.day17

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChronospatialComputerTest {

    @Test
    fun `execute -- reference computer -- 4,6,3,5,6,3,5,2,1,0`() {
        val input = """
            Register A: 729
            Register B: 0
            Register C: 0

            Program: 0,1,5,4,3,0
        """.trimIndent()
        val computer = ChronospatialComputer.of(input)

        computer.execute()

        assertThat(computer.output).isEqualTo(listOf(4, 6, 3, 5, 6, 3, 5, 2, 1, 0))
    }

    @Test
    fun `computer that should output its own program -- reference example -- does so`() {
        val input = """
            Register A: 117440
            Register B: 0
            Register C: 0

            Program: 0,3,5,4,3,0
        """.trimIndent()
        val computer = ChronospatialComputer.of(input)

        computer.execute()

        assertThat(computer.output).isEqualTo(listOf(0, 3, 5, 4, 3, 0))
    }

    @Test
    fun `quine value -- reference example -- 117440`() {
        val input = """
            Register A: 2024
            Register B: 0
            Register C: 0

            Program: 0,3,5,4,3,0
        """.trimIndent()
        val computer = ChronospatialComputer.of(input)

        val quineValue = computer.quineValue()

        assertThat(quineValue).isEqualTo(117440)
    }
}