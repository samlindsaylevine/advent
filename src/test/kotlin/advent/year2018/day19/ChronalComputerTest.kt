package advent.year2018.day19

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChronalComputerTest {

    @Test
    fun `execute -- reference program -- reference state`() {
        val computer = ChronalComputer()
        val input = """
            #ip 0
            seti 5 0 1
            seti 6 0 2
            addi 0 1 0
            addr 1 2 3
            setr 1 0 0
            seti 8 0 4
            seti 9 0 5
        """.trimIndent()
        val program = ChronalProgram.parse(input)

        computer.execute(program)

        assertThat(computer.registers).containsExactly(7, 5, 6, 0, 0, 9)
    }

    @Test
    fun `sumOfDivisors -- number for part one -- gives answer for part one`() {
        val sum = sumOfDivisors(893)

        assertThat(sum).isEqualTo(960)
    }
}