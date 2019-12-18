package advent.year2019.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KeyedVaultTest {

    @Test
    fun `shortestPathLength -- first example -- 8`() {
        val input = """
            #########
            #b.A.@.a#
            #########
        """.trimIndent()
        val vault = KeyedVault.parse(input)

        val length = vault.shortestPathLength()

        assertThat(length).isEqualTo(8)
    }

    @Test
    fun `shortestPathLength -- second example -- 86`() {
        val input = """
            ########################
            #f.D.E.e.C.b.A.@.a.B.c.#
            ######################.#
            #d.....................#
            ########################
        """.trimIndent()
        val vault = KeyedVault.parse(input)

        val length = vault.shortestPathLength()

        assertThat(length).isEqualTo(86)
    }

    @Test
    fun `shortestPathLength -- third example -- 132`() {
        val input = """
            ########################
            #...............b.C.D.f#
            #.######################
            #.....@.a.B.c.d.A.e.F.g#
            ########################
        """.trimIndent()
        val vault = KeyedVault.parse(input)

        val length = vault.shortestPathLength()

        assertThat(length).isEqualTo(132)
    }

    //@Disabled("too long?")
    @Test
    fun `shortestPathLength -- fourth example -- 136`() {
        val input = """
            #################
            #i.G..c...e..H.p#
            ########.########
            #j.A..b...f..D.o#
            ########@########
            #k.E..a...g..B.n#
            ########.########
            #l.F..d...h..C.m#
            #################
        """.trimIndent()
        val vault = KeyedVault.parse(input)

        val length = vault.shortestPathLength()

        assertThat(length).isEqualTo(136)
    }

    @Test
    fun `shortestPathLength -- fifth example -- 81`() {
        val input = """
            ########################
            #@..............ac.GI.b#
            ###d#e#f################
            ###A#B#C################
            ###g#h#i################
            ########################
        """.trimIndent()
        val vault = KeyedVault.parse(input)

        val length = vault.shortestPathLength()

        assertThat(length).isEqualTo(81)
    }
}