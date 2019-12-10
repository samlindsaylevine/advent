package advent.year2018.day15

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShortestPathFinderUnitTest {

    /**
     * An example where you can double an integer or subtract 1 from it, and try to get from 1 to 100.
     */
    @Test
    fun `find -- double or subtract 1 -- returns expected path`() {
        val paths = ShortestPathFinder().find(start = 1,
                end = 100,
                nextSteps = { setOf(it * 2, it - 1) })

        val expected = setOf(Path(listOf(2, 4, 8, 7, 14, 13, 26, 25, 50, 100), 10))

        assertThat(paths).isEqualTo(expected)
    }

    /**
     * An example where you are walking on a grid, but can't go through 0,0.
     */
    @Test
    fun `find -- walk on grid, avoid the origin -- returns both expected paths`() {
        fun nextSteps(pos: Position) = pos.adjacent().filter { it != Position(0, 0) }.toSet()

        val paths = ShortestPathFinder().find(start = Position(-1, -1),
                end = Position(1, 1),
                nextSteps = ::nextSteps)

        // Either up then right, or right then up.
        assertThat(paths).containsExactlyInAnyOrder(
                Path(listOf(Position(-1, 0),
                        Position(-1, 1),
                        Position(0, 1),
                        Position(1, 1)), 4),
                Path(listOf(Position(0, -1),
                        Position(1, -1),
                        Position(1, 0),
                        Position(1, 1)), 4))
    }

    /**
     * An example with no legal paths.
     */
    @Test
    fun `find -- no legal paths -- returns empty set`() {
        val paths = ShortestPathFinder().find(start = 1,
                end = 5,
                nextSteps = { setOf(it - 1, it + 1).filter { x -> x != 3 && x > 0 }.toSet() })

        assertThat(paths).isEmpty()
    }

    @Test
    fun `find -- go from 0,0 to 5,5 while collapsing on first & last space -- returns only two paths`() {
        val paths = ShortestPathFinder().find(start = Position(0, 0),
                end = Position(5, 5),
                nextSteps = { it.adjacent().toSet() },
                collapseKey = { Pair(it.first(), it.last()) })

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

        val paths = ShortestPathFinder().findWithCosts("A", "G", ::next)

        assertThat(paths).hasSize(1)
        assertThat(paths.first().steps).containsExactly("D", "G")
        assertThat(paths.first().totalCost).isEqualTo(4)
    }
}