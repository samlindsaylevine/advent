package advent.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointRangeTest {

  @Test
  fun `included points -- horizontal -- has expected points`() {
    val range = Point(2, 100)..Point(5, 100)

    assertThat(range).containsExactly(
      Point(2, 100),
      Point(3, 100),
      Point(4, 100),
      Point(5, 100)
    )
  }

  @Test
  fun `included points -- right to left -- has expected points`() {
    val range = Point(5, -1)..Point(2, -1)

    assertThat(range).containsExactly(
      Point(5, -1),
      Point(4, -1),
      Point(3, -1),
      Point(2, -1)
    )
  }

  @Test
  fun `included points -- diagonal -- has expected points`() {
    val range = Point(4, 0)..Point(0, 4)

    assertThat(range).containsExactly(
      Point(4, 0),
      Point(3, 1),
      Point(2, 2),
      Point(1, 3),
      Point(0, 4)
    )
  }
}