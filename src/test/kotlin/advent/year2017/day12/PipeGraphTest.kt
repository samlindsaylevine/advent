package advent.year2017.day12

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PipeGraphTest {

    private val REFERENCE_INPUT = """0 <-> 2
1 <-> 1
2 <-> 0, 3, 4
3 <-> 2, 4
4 <-> 2, 3, 6
5 <-> 6
6 <-> 4, 5"""

    @Test
    fun `connectedSubgraph -- reference input -- reference output`() {
        val graph = PipeGraph(REFERENCE_INPUT)

        val subgraph = graph.connectedSubgraph(0)

        assertThat(subgraph).hasSize(6)
                .containsExactlyInAnyOrder(0, 2, 3, 4, 5, 6)
    }

    @Test
    fun `connectedSubgraphs -- reference input -- reference output`() {
        val graph = PipeGraph(REFERENCE_INPUT)

        val subgraphs = graph.connectedSubgraphs()

        assertThat(subgraphs).containsExactlyInAnyOrder(setOf(0, 2, 3, 4, 5, 6),
                setOf(1))
    }
}