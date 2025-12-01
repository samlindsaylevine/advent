package advent.year2025.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SafeDialSequenceTest {

    @Test
    fun `password -- reference input -- 3`() {
        val input = """
            L68
            L30
            R48
            L5
            R60
            L55
            L1
            L99
            R14
            L82
        """.trimIndent()
        val sequence = SafeDialSequence(input)

        val password = sequence.password()

        assertThat(password).isEqualTo(3)
    }
}