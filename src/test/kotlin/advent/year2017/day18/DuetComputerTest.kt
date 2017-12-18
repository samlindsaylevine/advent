package advent.year2017.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DuetComputerTest {

    @Test
    fun `executeUntilRecover -- reference input -- reference output`() {
        val input = """set a 1
add a 2
mul a a
mod a 5
snd a
set a 0
rcv a
jgz a -1
set a 1
jgz a -2""".split("\n")
        val computer = DuetComputer()

        val result = computer.executeUntilRecover(input)

        assertThat(result).isEqualTo(4)
    }
}