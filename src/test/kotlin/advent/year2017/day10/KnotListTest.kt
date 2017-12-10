package advent.year2017.day10

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KnotListTest {

    @Test
    fun `applyLengths -- reference input size 5 -- reference output`() {
        val knotList = KnotList(5)

        knotList.applyLengths(listOf(3, 4, 1, 5))

        assertThat(knotList.values()).containsExactly(3, 4, 2, 1, 0)
        assertThat(knotList.productOfFirstTwo()).isEqualTo(12)
    }

}