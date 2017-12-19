package advent.year2017.day19

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoutingDiagramTest {

    private val REFERENCE_INPUT = """     |
     |  +--+
     A  |  C
 F---|----E|--+
     |  |  |  D
     +B-+  +--+"""

    @Test
    fun `encounteredLetters -- reference input -- reference output`() {
        val diagram = RoutingDiagram(REFERENCE_INPUT)

        val letters = diagram.traverse().encounteredLetters

        assertThat(letters).isEqualTo("ABCDEF")
    }

    @Test
    fun `stepsTaken -- reference input -- reference output`() {
        val diagram = RoutingDiagram(REFERENCE_INPUT)

        val steps = diagram.traverse().stepsTaken

        assertThat(steps).isEqualTo(38)
    }
}