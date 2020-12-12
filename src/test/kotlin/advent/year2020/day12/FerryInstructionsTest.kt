package advent.year2020.day12

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FerryInstructionsTest {

  @Test
  fun `execute -- reference input -- 25 distance from origin`() {
    val input = FerryInstructions("""
      F10
      N3
      F7
      R90
      F11
    """.trimIndent())

    val finalState = input.execute()
    val distance = finalState.distanceFromOrigin()

    assertThat(distance).isEqualTo(25)
  }

  @Test
  fun `executeWithWaypoint -- reference input -- 286 distance from origin`() {
    val input = FerryInstructions("""
      F10
      N3
      F7
      R90
      F11
    """.trimIndent())

    val finalState = input.executeWithWaypoint()
    val distance = finalState.distanceFromOrigin()

    assertThat(distance).isEqualTo(286)
  }
}