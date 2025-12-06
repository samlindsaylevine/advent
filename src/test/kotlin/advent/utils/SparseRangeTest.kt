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

  @Test
  fun `sparse range -- add non-overlapping -- leaves unchanged`() {
    val range = SparseRange(1L..4L)

    val added = range + (7L..10L)

    assertThat(added).isEqualTo(SparseRange(listOf(1L..4L, 7L..10L)))
  }

  @Test
  fun `sparse range -- add overlapping the front -- results in union`() {
    val range = SparseRange(4L..10L)

    val added = range + (1L..6L)

    assertThat(added).isEqualTo(SparseRange(listOf(1L..10L)))
  }

  @Test
  fun `sparse range -- add overlapping the back -- results in union`() {
    val range = SparseRange(1L..6L)

    val added = range + (3L..10L)

    assertThat(added).isEqualTo(SparseRange(listOf(1L..10L)))
  }


  @Test
  fun `sparse range -- add already existing -- no change`() {
    val range = SparseRange(1L..6L)

    val added = range + (1L..6L)

    assertThat(added).isEqualTo(SparseRange(listOf(1L..6L)))
  }


  @Test
  fun `sparse range -- add fully surrounding multiple existing -- existing are subsumed`() {
    val range = SparseRange(listOf(2L..3L, 5L..7L))

    val added = range + (1L..10L)

    assertThat(added).isEqualTo(SparseRange(listOf(1L..10L)))
  }


  @Test
  fun `sparse range -- add partially overlapping multiple existing -- existing merged with new`() {
    val range = SparseRange(listOf(1L..5L, 8L..10L))

    val added = range + (4L..9L)

    assertThat(added).isEqualTo(SparseRange(listOf(1L..10L)))
  }
}