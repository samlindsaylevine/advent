package advent.year2019.day6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrbitMapTest {

    @Test
    fun `totalDirectAndIndirect -- reference example -- 42`() {
        val input = """
            COM)B
            B)C
            C)D
            D)E
            E)F
            B)G
            G)H
            D)I
            E)J
            J)K
            K)L
        """.trimIndent()
        val map = OrbitMap.parse(input)

        val total = map.totalDirectAndIndirect()

        assertThat(total).isEqualTo(42)
    }

    @Test
    fun `transfers -- reference example -- is 4`() {
        val input = """
            COM)B
            B)C
            C)D
            D)E
            E)F
            B)G
            G)H
            D)I
            E)J
            J)K
            K)L
            K)YOU
            I)SAN
        """.trimIndent()
        val map = OrbitMap.parse(input)

        val transfers = map.transfers("YOU", "SAN")

        assertThat(transfers).isEqualTo(4)
    }
}