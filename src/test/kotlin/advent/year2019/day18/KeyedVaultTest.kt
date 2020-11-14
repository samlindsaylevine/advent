package advent.year2019.day18

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
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

  @Test
  fun `shortestPathLength -- first multi-start example -- 8`() {
    val input = """
            #######
            #a.#Cd#
            ##@#@##
            #######
            ##@#@##
            #cB#Ab#
            #######
        """.trimIndent()
    val vault = KeyedVault.parse(input)

    val length = vault.shortestPathLength()

    assertThat(length).isEqualTo(8)
  }

  @Test
  fun `shortestPathLength -- second multi-start example -- 24`() {
    val input = """
            ###############
            #d.ABC.#.....a#
            ######@#@######
            ###############
            ######@#@######
            #b.....#.....c#
            ###############
        """.trimIndent()
    val vault = KeyedVault.parse(input)

    val length = vault.shortestPathLength()

    assertThat(length).isEqualTo(24)
  }

  @Test
  fun `shortestPathLength -- third multi-start example -- 32`() {
    val input = """
            #############
            #DcBa.#.GhKl#
            #.###@#@#I###
            #e#d#####j#k#
            ###C#@#@###J#
            #fEbA.#.FgHi#
            #############
        """.trimIndent()
    val vault = KeyedVault.parse(input)

    val length = vault.shortestPathLength()

    assertThat(length).isEqualTo(32)
  }

  @Test
  @Disabled("too slow")
  fun `shortestPathLength -- fourth multi-start example -- 72`() {
    val input = """
            #############
            #g#f.D#..h#l#
            #F###e#E###.#
            #dCba@#@BcIJ#
            #############
            #nK.L@#@G...#
            #M###N#H###.#
            #o#m..#i#jk.#
            #############
        """.trimIndent()
    val vault = KeyedVault.parse(input)

    val length = vault.shortestPathLength()

    assertThat(length).isEqualTo(72)
  }

  @Test
  fun `split -- reference input --- reference output`() {
    val input = """
            #######
            #a.#Cd#
            ##...##
            ##.@.##
            ##...##
            #cB#Ab#
            #######
        """.trimIndent()
    val expected = """
            #######
            #a.#Cd#
            ##@#@##
            #######
            ##@#@##
            #cB#Ab#
            #######
        """.trimIndent()
    val vault = KeyedVault.parse(input)
    val expectedVault = KeyedVault.parse(expected)

    val split = vault.split()

    assertThat(split).isEqualTo(expectedVault)
  }
}