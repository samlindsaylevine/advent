package advent.year2017.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MemoryBankTest {

    @ParameterizedTest(name = "rellocate -- input {0} -- result is {1}")
    @CsvSource(
            "0 2 7 0, 2 4 1 2",
            "2 4 1 2, 3 1 2 3",
            "0 2 3 4, 1 3 4 1",
            "1 3 4 1, 2 4 1 2")
    fun `reallocate -- reference inputs -- reference output`(input: String, expected: String) {
        val original = MemoryBank(input)

        val result = original.reallocate()

        assertThat(result).isEqualTo(MemoryBank(expected))
    }

    @Test
    fun `redistributionsBeforeLoop -- 0 2 7 0 -- 5 redistributions`() {
        val original = MemoryBank("0 2 7 0")

        val redistributions = original.redistributionsBeforeLoop()

        assertThat(redistributions).isEqualTo(5)
    }

    @Test
    fun `sizeOfLoop -- 0 2 7 0 -- size 4`() {
        val original = MemoryBank("0 2 7 0")

        val sizeOfLoop = original.sizeOfLoop()

        assertThat(sizeOfLoop).isEqualTo(4)
    }
}