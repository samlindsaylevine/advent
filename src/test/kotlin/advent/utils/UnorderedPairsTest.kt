package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UnorderedPairsTest {

    @Test
    fun `allUnorderedPairs -- list -- gives all unordered pairs`() {
        val input = listOf(1, 2, 3, 4)

        val allPairs = input.allUnorderedPairs()

        assertThat(allPairs).containsExactlyInAnyOrder(
                1 with 2,
                1 with 3,
                2 with 3,
                1 with 4,
                2 with 4,
                3 with 4
        )
    }
}