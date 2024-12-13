package advent.year2024.day13

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClawMachinesTest {

    @Test
    fun `cost -- first machine -- 280`() {
        val input = """
            Button A: X+94, Y+34
            Button B: X+22, Y+67
            Prize: X=8400, Y=5400
        """.trimIndent()
        val machine = ClawMachine.of(input)

        val cost = machine.cost()

        assertThat(cost).isEqualTo(280L)
    }

    @Test
    fun `cost -- second machine -- no prize`() {
        val input = """
            Button A: X+26, Y+66
            Button B: X+67, Y+21
            Prize: X=12748, Y=12176
        """.trimIndent()
        val machine = ClawMachine.of(input)

        val cost = machine.cost()

        assertThat(cost).isNull()
    }

    @Test
    fun `cost -- third machine -- 200`() {
        val input = """
            Button A: X+17, Y+86
            Button B: X+84, Y+37
            Prize: X=7870, Y=6450
        """.trimIndent()
        val machine = ClawMachine.of(input)

        val cost = machine.cost()

        assertThat(cost).isEqualTo(200L)
    }

    @Test
    fun `cost -- fourth machine -- no prize`() {
        val input = """
            Button A: X+69, Y+23
            Button B: X+27, Y+71
            Prize: X=18641, Y=10279
        """.trimIndent()
        val machine = ClawMachine.of(input)

        val cost = machine.cost()

        assertThat(cost).isNull()
    }

    @Test
    fun `cost -- all reference machines -- 480`() {
        val input = """
            Button A: X+94, Y+34
            Button B: X+22, Y+67
            Prize: X=8400, Y=5400

            Button A: X+26, Y+66
            Button B: X+67, Y+21
            Prize: X=12748, Y=12176

            Button A: X+17, Y+86
            Button B: X+84, Y+37
            Prize: X=7870, Y=6450

            Button A: X+69, Y+23
            Button B: X+27, Y+71
            Prize: X=18641, Y=10279
        """.trimIndent()
        val machines = ClawMachines(input)

        val cost = machines.cost()

        assertThat(cost).isEqualTo(480)
    }
}