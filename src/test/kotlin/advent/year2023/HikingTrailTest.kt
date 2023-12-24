package advent.year2023

import advent.utils.Point
import advent.year2023.day23.HikingEdge
import advent.year2023.day23.HikingTrail
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HikingTrailTest {

  @Test
  fun `toGraph -- simple hallway -- has expected counts`() {
    val input = """
      .
      .
      .
      .
      .
    """.trimIndent()
    val trail = HikingTrail(input)
    val graph = trail.toGraph()

    assertThat(graph.start).isEqualTo(Point(0, 0))
    assertThat(graph.end).isEqualTo(Point(0, 4))
    assertThat(graph.edges).hasSize(8)
  }

  @Test
  fun `toGraph -- simple hallway, compacted -- has only two edges`() {
    val input = """
      .
      .
      .
      .
      .
    """.trimIndent()
    val trail = HikingTrail(input)

    val graph = trail.toGraph().compacted()

    assertThat(graph.start).isEqualTo(Point(0, 0))
    assertThat(graph.end).isEqualTo(Point(0, 4))
    assertThat(graph.edges).containsExactlyInAnyOrder(
            HikingEdge(from = graph.start, to = graph.end, distance = 4),
            HikingEdge(from = graph.end, to = graph.start, distance = 4)
    )
  }

  @Test
  fun `longest hike -- reference input -- has 94 steps`() {
    val input = """
      #.#####################
      #.......#########...###
      #######.#########.#.###
      ###.....#.>.>.###.#.###
      ###v#####.#v#.###.#.###
      ###.>...#.#.#.....#...#
      ###v###.#.#.#########.#
      ###...#.#.#.......#...#
      #####.#.#.#######.#.###
      #.....#.#.#.......#...#
      #.#####.#.#.#########v#
      #.#...#...#...###...>.#
      #.#.#v#######v###.###v#
      #...#.>.#...>.>.#.###.#
      #####v#.#.###v#.#.###.#
      #.....#...#...#.#.#...#
      #.#########.###.#.#.###
      #...###...#...#...#.###
      ###.###.#.###v#####v###
      #...#...#.#.>.>.#.>.###
      #.###.###.#.###.#.#v###
      #.....###...###...#...#
      #####################.#
    """.trimIndent()
    val trail = HikingTrail(input)

    val hike = trail.toGraph().compacted().longestHike()

    assertThat(hike.distance).isEqualTo(94)
  }

  @Test
  fun `longest hike -- reference input, not slippery -- has 154 steps`() {
    val input = """
      #.#####################
      #.......#########...###
      #######.#########.#.###
      ###.....#.>.>.###.#.###
      ###v#####.#v#.###.#.###
      ###.>...#.#.#.....#...#
      ###v###.#.#.#########.#
      ###...#.#.#.......#...#
      #####.#.#.#######.#.###
      #.....#.#.#.......#...#
      #.#####.#.#.#########v#
      #.#...#...#...###...>.#
      #.#.#v#######v###.###v#
      #...#.>.#...>.>.#.###.#
      #####v#.#.###v#.#.###.#
      #.....#...#...#.#.#...#
      #.#########.###.#.#.###
      #...###...#...#...#.###
      ###.###.#.###v#####v###
      #...#...#.#.>.>.#.>.###
      #.###.###.#.###.#.#v###
      #.....###...###...#...#
      #####################.#
    """.trimIndent()
    val trail = HikingTrail(input)

    val hike = trail.toGraph(slippery = false).compacted().longestHike()

    assertThat(hike.distance).isEqualTo(154)
  }
}