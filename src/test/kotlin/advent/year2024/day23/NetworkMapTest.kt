package advent.year2024.day23

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NetworkMapTest {

    private val input = """
        kh-tc
        qp-kh
        de-cg
        ka-co
        yn-aq
        qp-ub
        cg-tb
        vc-aq
        tb-ka
        wh-tc
        yn-cg
        kh-ub
        ta-co
        de-co
        tc-td
        tb-wq
        wh-td
        ta-ka
        td-qp
        aq-cg
        wq-ub
        ub-vc
        de-ta
        wq-aq
        wq-vc
        wh-yn
        ka-de
        kh-ta
        co-tc
        wh-qp
        tb-vc
        td-yn
    """.trimIndent()

    @Test
    fun `trios -- 5 elements -- expected trios`() {
        val elements = setOf(1, 2, 3, 4, 5)

        val trios = elements.trios().toList()

        val unordered = trios.map { setOf(it.first, it.second, it.third) }
        assertThat(unordered).containsExactlyInAnyOrder(
            setOf(1, 2, 3),
            setOf(1, 2, 4),
            setOf(1, 2, 5),
            setOf(1, 3, 4),
            setOf(1, 3, 5),
            setOf(1, 4, 5),
            setOf(2, 3, 4),
            setOf(2, 3, 5),
            setOf(2, 4, 5),
            setOf(3, 4, 5)
        )
    }

    @Test
    fun `countTTriangles -- reference input -- 7`() {
        val networkMap = NetworkMap.of(input)

        val count = networkMap.countTTriangles()

        assertThat(count).isEqualTo(7)
    }

    @Test
    fun `password -- reference input -- co,de,ka,ta`() {
        val networkMap = NetworkMap.of(input)

        val password = networkMap.password()

        assertThat(password).isEqualTo("co,de,ka,ta")
    }
}