package advent.year2020.day11

import advent.utils.Point
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

  @Test
  fun `stable state by sight lines -- reference input -- reference stable layout`() {
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

    val stable = layout.stableBySightLines()

    val expected = SeatLayout.parse("""
      #.L#.L#.L#
      #LLLLLL.LL
      L.L.L..#..
      ##L#.#L.L#
      L.L#.LL.L#
      #.LLLL#.LL
      ..#.L.....
      LLL###LLL#
      #.LLLLL#.L
      #.L#LL#.L#
    """.trimIndent())
    assertThat(stable).isEqualTo(expected)
    assertThat(stable.occupied.size).isEqualTo(26)
  }

  @Test
  fun `nextBySightLines -- reference example -- first round layout`() {
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

    val next = layout.nextBySightLines()

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
  fun `nextBySightLines twice -- reference example -- second round layout`() {
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

    val next = advance(2, layout, SeatLayout::nextBySightLines)

    val expected = SeatLayout.parse("""
      #.LL.LL.L#
      #LLLLLL.LL
      L.L.L..L..
      LLLL.LL.LL
      L.LL.LL.LL
      L.LLLLL.LL
      ..L.L.....
      LLLLLLLLL#
      #.LLLLLL.L
      #.LLLLL.L#
    """.trimIndent())
    assertThat(next.occupied).containsExactlyElementsOf(expected.occupied)
  }

  @Test
  fun `visibleOccupiedCount -- first example -- 8 seats`() {
    val layout = SeatLayout.parse("""
      .......#.
      ...#.....
      .#.......
      .........
      ..#L....#
      ....#....
      .........
      #........
      ...#.....
    """.trimIndent())

    val count = layout.visibleOccupiedCount(Point(3, 4))

    assertThat(count).isEqualTo(8)
  }

  @Test
  fun `visibleOccupiedCount -- second example -- 0 seats`() {
    val layout = SeatLayout.parse("""
      .............
      .L.L.#.#.#.#.
      .............
    """.trimIndent())

    val count = layout.visibleOccupiedCount(Point(1, 1))

    assertThat(count).isEqualTo(0)
  }

  @Test
  fun `visibleOccupiedCount -- third example -- 0 seats`() {
    val layout = SeatLayout.parse("""
      .##.##.
      #.#.#.#
      ##...##
      ...L...
      ##...##
      #.#.#.#
      .##.##.
    """.trimIndent())

    val count = layout.visibleOccupiedCount(Point(3, 3))

    assertThat(count).isEqualTo(0)
  }
}