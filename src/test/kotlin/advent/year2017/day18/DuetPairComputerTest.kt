package advent.year2017.day18

import advent.year2017.day18.DuetPairComputer.DuetPair
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DuetPairComputerTest {

    @Test
    fun `messages sent -- after executing reference input -- reference value`() {
        val input = """snd 1
snd 2
snd p
rcv a
rcv b
rcv c
rcv d""".split("\n")
        val computers = DuetPair()
        computers.execute(input)

        val sent = computers.first.messageSentCount

        assertThat(sent).isEqualTo(3)
    }
}