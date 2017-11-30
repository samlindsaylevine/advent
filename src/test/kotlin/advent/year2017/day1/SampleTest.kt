package advent.year2017.day1

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SampleTest {

    @Test
    fun `solution -- always -- has the solution`() {
        val solution = Sample().solution()

        assertThat(solution).contains("has the solution")
    }
}