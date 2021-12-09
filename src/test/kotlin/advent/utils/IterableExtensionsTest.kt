package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IterableExtensionsTest {

  @Test
  fun `permutations -- four items -- has all expected elements`() {
    val items = setOf("a", "b", "c", "d")

    val permutations = items.permutations()

    assertThat(permutations.toList()).containsExactlyInAnyOrder(
      listOf("a", "b", "c", "d"),
      listOf("a", "b", "d", "c"),
      listOf("a", "c", "b", "d"),
      listOf("a", "c", "d", "b"),
      listOf("a", "d", "b", "c"),
      listOf("a", "d", "c", "b"),
      listOf("b", "a", "c", "d"),
      listOf("b", "a", "d", "c"),
      listOf("b", "c", "a", "d"),
      listOf("b", "c", "d", "a"),
      listOf("b", "d", "a", "c"),
      listOf("b", "d", "c", "a"),
      listOf("c", "a", "b", "d"),
      listOf("c", "a", "d", "b"),
      listOf("c", "b", "a", "d"),
      listOf("c", "b", "d", "a"),
      listOf("c", "d", "a", "b"),
      listOf("c", "d", "b", "a"),
      listOf("d", "a", "b", "c"),
      listOf("d", "a", "c", "b"),
      listOf("d", "b", "a", "c"),
      listOf("d", "b", "c", "a"),
      listOf("d", "c", "a", "b"),
      listOf("d", "c", "b", "a")
    )
  }
}