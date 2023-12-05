package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LongRangesTest {

    @Test
    fun `compare -- no overlap -- distinct`() {
        val (left, both, right) = (1L..10L) compare (11L..20L)

        assertThat(left).containsExactly(1L..10L)
        assertThat(both).isEqualTo(LongRange.EMPTY)
        assertThat(right).containsExactly(11L..20L)
    }

    @Test
    fun `compare -- right entirely in left -- expected results`() {
        val (left, both, right) = (1L..10L) compare (3L..7L)

        assertThat(left).containsExactly(1L..2L, 8L..10L)
        assertThat(both).isEqualTo(3L..7L)
        assertThat(right).isEmpty()
    }

    @Test
    fun `compare -- left entirely in right -- expected results`() {
        val (left, both, right) = (3L..7L) compare (1L..10L)

        assertThat(left).isEmpty()
        assertThat(both).isEqualTo(3L..7L)
        assertThat(right).containsExactly(1L..2L, 8L..10L)
    }

    @Test
    fun `compare -- overlapping, this starts first -- expected results`() {
        val (left, both, right) = (1L..7L) compare (5L..10L)

        assertThat(left).containsExactly(1L..4L)
        assertThat(both).isEqualTo(5L..7L)
        assertThat(right).containsExactly(8L..10L)
    }

    @Test
    fun `compare -- overlapping, other starts first -- expected results`() {
        val (left, both, right) = (5L..10L) compare (1L..7L)

        assertThat(left).containsExactly(8L..10L)
        assertThat(both).isEqualTo(5L..7L)
        assertThat(right).containsExactly(1L..4L)
    }

    @Test
    fun `compare -- identical ranges -- full range in both`() {
        val (left, both, right) = (1L..10L) compare (1L..10L)

        assertThat(left).isEmpty()
        assertThat(both).isEqualTo(1L..10L)
        assertThat(right).isEmpty()
    }
}