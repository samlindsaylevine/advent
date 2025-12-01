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

    @Test
    fun `clickPassword -- sequence with one R1000 -- 10`() {
        val sequence = SafeDialSequence("R1000")

        val password = sequence.clickPassword()

        assertThat(password).isEqualTo(10)
    }

    @Test
    fun `clickPassword -- sequence with one R50 -- 1`() {
        val sequence = SafeDialSequence("R50")

        val password = sequence.clickPassword()

        assertThat(password).isEqualTo(1)
    }

    @Test
    fun `clickPassword -- sequence with one R150 -- 2`() {
        val sequence = SafeDialSequence("R150")

        val password = sequence.clickPassword()

        assertThat(password).isEqualTo(2)
    }

    @Test
    fun `clickPassword -- L50 -- 1`() {
        val sequence = SafeDialSequence("L50")

        val password = sequence.clickPassword()

        assertThat(password).isEqualTo(1)
    }

    @Test
    fun `clickPassword -- L50, L1, R1, L1, R1 -- 3`() {
        val input = """
            L50
            L1
            R1
            L1
            R1
        """.trimIndent()
        val sequence = SafeDialSequence(input)

        val password = sequence.clickPassword()

        assertThat(password).isEqualTo(3)
    }

    @Test
    fun `clickPassword -- reference input -- 6`() {
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

        val password = sequence.clickPassword()

        assertThat(password).isEqualTo(6)
    }
}