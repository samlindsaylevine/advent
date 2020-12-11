package advent.year2020.day11

import advent.year2018.day18.advance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SeatLayoutTest {

  @Test
  fun `next -- reference input -- reference one round layout`() {
    val layout = SeatLayout.parse("""
      L.LL.LL.LL
      LLLLLLL.LL
      L.L.L..L..
      LLLL.LL.LL
      L.LL.LL.LL
      L.LLLLL.LL
      ..L.L.....
      LLLLLLLLLL
      L.LLLLLL.L
      L.LLLLL.LL
    """.trimIndent())

    val next = layout.next()

    val expected = SeatLayout.parse("""
      #.##.##.##
      #######.##
      #.#.#..#..
      ####.##.##
      #.##.##.##
      #.#####.##
      ..#.#.....
      ##########
      #.######.#
      #.#####.##
    """.trimIndent())
    assertThat(next).isEqualTo(expected)
  }

  @Test
  fun `next twice -- reference input -- reference second round layout`() {
    val layout = SeatLayout.parse("""
      L.LL.LL.LL
      LLLLLLL.LL
      L.L.L..L..
      LLLL.LL.LL
      L.LL.LL.LL
      L.LLLLL.LL
      ..L.L.....
      LLLLLLLLLL
      L.LLLLLL.L
      L.LLLLL.LL
    """.trimIndent())

    val next = advance(2, layout, SeatLayout::next)

    val expected = SeatLayout.parse("""
      #.LL.L#.##
      #LLLLLL.L#
      L.L.L..L..
      #LLL.LL.L#
      #.LL.LL.LL
      #.LLLL#.##
      ..L.L.....
      #LLLLLLLL#
      #.LLLLLL.L
      #.#LLLL.##
    """.trimIndent())
    assertThat(next).isEqualTo(expected)
  }

  @Test
  fun `stable state -- reference input -- reference stable layout`() {
    val layout = SeatLayout.parse("""
      L.LL.LL.LL
      LLLLLLL.LL
      L.L.L..L..
      LLLL.LL.LL
      L.LL.LL.LL
      L.LLLLL.LL
      ..L.L.....
      LLLLLLLLLL
      L.LLLLLL.L
      L.LLLLL.LL
    """.trimIndent())

    val stable = layout.stableState()

    val expected = SeatLayout.parse("""
      #.#L.L#.##
      #LLL#LL.L#
      L.#.L..#..
      #L##.##.L#
      #.#L.LL.LL
      #.#L#L#.##
      ..L.L.....
      #L#L##L#L#
      #.LLLLLL.L
      #.#L#L#.##
    """.trimIndent())
    assertThat(stable).isEqualTo(expected)
    assertThat(stable.occupied.size).isEqualTo(37)
  }
}