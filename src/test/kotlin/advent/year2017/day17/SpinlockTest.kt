package advent.year2017.day17

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SpinlockTest {

    @ParameterizedTest(name = "values -- 3 steps per insert, {0} inserts -- {1}")
    @CsvSource("0, 0",
            "1, 0 1",
            "2, 0 2 1",
            "3, 0 2 3 1",
            "4, 0 2 4 3 1",
            "5, 0 5 2 4 3 1",
            "6, 0 5 2 4 3 6 1",
            "7, 0 5 7 2 4 3 6 1",
            "8, 0 5 7 2 4 3 8 6 1",
            "9, 0 9 5 7 2 4 3 8 6 1")
    fun `values -- reference input -- reference output`(numSteps: Int, expectedValueString: String) {
        val spinlock = Spinlock(stepsPerInsert = 3, numInsertions = numSteps)
        val expected = expectedValueString.split(" ").map { it.toInt() }

        assertThat(spinlock.values).isEqualTo(expected)
    }

    @Test
    fun `valueAfterLastInsert -- reference input -- reference output`() {
        val spinlock = Spinlock(stepsPerInsert = 3)

        val afterLastInsert = spinlock.valueAfterLastInsert()

        assertThat(afterLastInsert).isEqualTo(638)
    }
}