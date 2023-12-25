package advent.year2023.day25

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WiringDiagramTest {

  @Test
  fun `disconnectedGroupProduct -- reference example -- 54`() {
    val input = """
      jqt: rhn xhk nvd
      rsh: frs pzl lsr
      xhk: hfx
      cmg: qnr nvd lhk bvb
      rhn: xhk bvb hfx
      bvb: xhk hfx
      pzl: lsr hfx nvd
      qnr: nvd
      ntq: jqt hfx bvb xhk
      nvd: lhk
      lsr: lhk
      rzs: qnr cmg lsr rsh
      frs: qnr lhk lsr
    """.trimIndent()
    val diagram = WiringDiagram(input)

    val product = diagram.disconnectedGroupProduct()

    assertThat(product).isEqualTo(54)
  }
}