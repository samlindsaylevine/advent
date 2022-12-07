package advent.utils

import advent.year2018.day15.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.function.Consumer

class ShortestPathFinderUnitTest {

  /**
   * An example where you can double an integer or subtract 1 from it, and try to get from 1 to 100.
   */
  @Test
  fun `find -- double or subtract 1 -- returns expected path`() {
    val paths = ShortestPathFinder().find(start = 1,
      end = EndState(100),
      nextSteps = Steps { setOf(it * 2, it - 1) })

    val expected = setOf(Path(listOf(2, 4, 8, 7, 14, 13, 26, 25, 50, 100), 10))

    assertThat(paths).isEqualTo(expected)
  }

  /**
   * An example where you are walking on a grid, but can't go through 0,0.
   */
  @Test
  fun `find -- walk on grid, avoid the origin -- returns both expected paths`() {
    fun nextSteps(pos: Position) = pos.adjacent().filter { it != Position(0, 0) }.toSet()

    val paths = ShortestPathFinder().find(
      start = Position(-1, -1),
      end = EndState(Position(1, 1)),
      nextSteps = Steps(::nextSteps)
    )

    // Either up then right, or right then up.
    assertThat(paths).containsExactlyInAnyOrder(
      Path(
        listOf(
          Position(-1, 0),
          Position(-1, 1),
          Position(0, 1),
          Position(1, 1)
        ), 4
      ),
      Path(
        listOf(
          Position(0, -1),
          Position(1, -1),
          Position(1, 0),
          Position(1, 1)
        ), 4
      )
    )
  }

  /**
   * An example with no legal paths.
   */
  @Test
  fun `find -- no legal paths -- returns empty set`() {
    val paths = ShortestPathFinder().find(start = 1,
      end = EndState(5),
      nextSteps = Steps { setOf(it - 1, it + 1).filter { x -> x != 3 && x > 0 }.toSet() })

    assertThat(paths).isEmpty()
  }

  @Test
  fun `find -- go from 0,0 to 5,5 while collapsing on first & last space -- returns only two paths`() {
    val paths = ShortestPathFinder().find(start = Position(0, 0),
      end = EndState(Position(5, 5)),
      nextSteps = Steps { it.adjacent().toSet() },
      collapse = Collapse { steps: List<Position> -> Pair(steps.first(), steps.last()) })

    assertThat(paths).hasSize(2)
  }

  @Test
  fun `findWithCosts -- a simple example -- finds cheapest path, not the shortest`() {
    fun next(point: String) = when (point) {
      "A" -> setOf(Step("B", 1), Step("D", 2), Step("G", 12))
      "B" -> setOf(Step("C", 1))
      "C" -> setOf(Step("D", 1))
      "D" -> setOf(Step("E", 1), Step("G", 2))
      "E" -> setOf(Step("F", 1))
      "F" -> setOf(Step("G", 1))
      else -> emptySet()
    }

    val paths = ShortestPathFinder().find(
      "A",
      EndState("G"),
      StepsWithCost(::next)
    )

    assertThat(paths).hasSize(1)
    assertThat(paths.first().steps).containsExactly("D", "G")
    assertThat(paths.first().totalCost).isEqualTo(4)
  }

  @Test
  fun `find -- walk on grid, avoid the origin, filter out -1, 1 -- returns only one path`() {
    fun nextSteps(pos: Position) = pos.adjacent().filter { it != Position(0, 0) }.toSet()

    val paths = ShortestPathFinder().find(start = Position(-1, -1),
      end = EndState(Position(1, 1)),
      nextSteps = Steps(::nextSteps),
      filter = Filter { it.last() == Position(-1, 1) })

    assertThat(paths).containsExactly(
      Path(
        listOf(
          Position(0, -1),
          Position(1, -1),
          Position(1, 0),
          Position(1, 1)
        ), 4
      )
    )
  }

  @Test
  fun `find -- walk on grid, get to 2 distance away -- returns 12 paths`() {
    fun nextSteps(pos: Point) = pos.adjacentNeighbors

    val paths = ShortestPathFinder().find(
      start = Point(0, 0),
      end = EndCondition { it.distanceFrom(Point(0, 0)) == 2 },
      nextSteps = Steps(::nextSteps)
    )

    assertThat(paths).hasSize(12)
    assertThat(paths).allSatisfy(Consumer {
      assertThat(it.steps).hasSize(2)
    })
  }
}