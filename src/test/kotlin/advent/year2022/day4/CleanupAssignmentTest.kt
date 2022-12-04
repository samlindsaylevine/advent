package advent.year2022.day4

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CleanupAssignmentTest {

  @Test
  fun `fully contained count -- reference input -- 2`() {
    val input = """
      2-4,6-8
      2-3,4-5
      5-7,7-9
      2-8,3-7
      6-6,4-6
      2-6,4-8
    """.trimIndent()
    val assignments = input.asCleanupAssignments()

    assertThat(assignments.fullyContainedCount()).isEqualTo(2)
  }

  @Test
  fun `overlap count -- reference input -- 4`() {
    val input = """
      2-4,6-8
      2-3,4-5
      5-7,7-9
      2-8,3-7
      6-6,4-6
      2-6,4-8
    """.trimIndent()
    val assignments = input.asCleanupAssignments()

    assertThat(assignments.overlapCount()).isEqualTo(4)
  }
}