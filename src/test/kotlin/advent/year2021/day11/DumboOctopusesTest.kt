package advent.year2021.day11

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DumboOctopusesTest {

  private val smallExample = DumboOctopuses(
    """
      11111
      19991
      19191
      19991
      11111
    """.trimIndent()
  )

  private val largeExample = DumboOctopuses(
    """
      5483143223
      2745854711
      5264556173
      6141336146
      6357385478
      4167524645
      2176841721
      6882881134
      4846848554
      5283751526
    """.trimIndent()
  )

  @Test
  fun `next -- small example -- has reference energy levels`() {
    val next = smallExample.next()

    assertThat(next.toString()).isEqualTo(
      """
        34543
        40004
        50005
        40004
        34543
      """.trimIndent()
    )
  }

  @Test
  fun `next -- small example, twice -- has reference energy levels`() {
    val next = smallExample.next(2)

    assertThat(next.toString()).isEqualTo(
      """
        45654
        51115
        61116
        51115
        45654
      """.trimIndent()
    )
  }

  @Test
  fun `num flashes -- small example, advanced once -- 9 flashes`() {
    val next = smallExample.next()

    assertThat(next.flashes).isEqualTo(9)
  }

  @Test
  fun `num flashes -- large example, 10 steps -- 204 flashes`() {
    val next = largeExample.next(10)

    assertThat(next.flashes).isEqualTo(204)
  }

  @Test
  fun `num flashes -- large example, 100 steps -- 1656 flashes`() {
    val next = largeExample.next(100)

    assertThat(next.flashes).isEqualTo(1656)
  }

  @Test
  fun `first simultaneous flash -- large example -- step 195`() {
    val time = largeExample.firstSimultaneousFlash()

    assertThat(time).isEqualTo(195)
  }
}