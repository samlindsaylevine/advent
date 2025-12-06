package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SparseRangeTest {

  @Test
  fun `sparse range -- remove from front -- eliminates removed range`() {
    val range = SparseRange(1L..10L)

    val removed = range - (1L..4L)

    assertThat(removed).isEqualTo(SparseRange(5L..10L))
  }

  @Test
  fun `sparse range -- remove from back -- eliminates removed range`() {
    val range = SparseRange(1L..10L)

    val removed = range - (5L..100L)

    assertThat(removed).isEqualTo(SparseRange(1L..4L))
  }

  @Test
  fun `sparse range -- remove entire -- eliminates removed range`() {
    val range = SparseRange(1L..10L)

    val removed = range - (-2L..100L)

    assertThat(removed).isEqualTo(SparseRange(emptyList()))
  }

  @Test
  fun `sparse range -- remove from middle -- leaves two ranges`() {
    val range = SparseRange(1L..10L)

    val removed = range - (4L..7L)

    assertThat(removed).isEqualTo(SparseRange(listOf(1L..3L, 8L..10L)))
  }

  @Test
  fun `sparse range -- remove non-overlapping -- leaves unchanged`() {
    val range = SparseRange(1L..10L)

    val removed = range - (12L..20L)

    assertThat(removed).isEqualTo(range)
  }

}