package advent.year2018.day20

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import advent.year2018.day20.Direction.N
import advent.year2018.day20.Direction.S
import advent.year2018.day20.Direction.W
import advent.year2018.day20.Direction.E

class RoomsGridTest {

    @Test
    fun `allPaths -- reference input -- should have all possible paths`() {
        val input = """^ENWWW(NEEE|SSE(EE|N))$"""
        val paths = RoomPaths.parse(input)

        val allPaths = paths.allPaths().toList()

        assertThat(allPaths).containsExactlyInAnyOrder(
                listOf(E, N, W, W, W, N, E, E, E),
                listOf(E, N, W, W, W, S, S, E, E, E),
                listOf(E, N, W, W, W, S, S, E, N)
        )
    }

    @Test
    fun `toString -- first reference input -- has reference map`() {
        val input = """^ENWWW(NEEE|SSE(EE|N))$"""
        val roomGrid = RoomsGrid.fromPaths(RoomPaths.parse(input))

        val map = roomGrid.toString()

        val expected = """
            #########
            #.|.|.|.#
            #-#######
            #.|.|.|.#
            #-#####-#
            #.#.#X|.#
            #-#-#####
            #.|.|.|.#
            #########
        """.trimIndent()
        assertThat(map).isEqualTo(expected)
    }

    @Test
    fun `toString -- with empty option -- has reference map`() {
        val input = """^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$"""
        val roomGrid = RoomsGrid.fromPaths(RoomPaths.parse(input))

        val map = roomGrid.toString()

        val expected = """
            ###########
            #.|.#.|.#.#
            #-###-#-#-#
            #.|.|.#.#.#
            #-#####-#-#
            #.#.#X|.#.#
            #-#-#####-#
            #.#.|.|.|.#
            #-###-###-#
            #.|.|.#.|.#
            ###########
        """.trimIndent()
        assertThat(map).isEqualTo(expected)
    }

    @Test
    fun `toString -- first extra example -- has reference map`() {
        val input = """^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$"""
        val roomGrid = RoomsGrid.fromPaths(RoomPaths.parse(input))

        val map = roomGrid.toString()

        val expected = """
            #############
            #.|.|.|.|.|.#
            #-#####-###-#
            #.#.|.#.#.#.#
            #-#-###-#-#-#
            #.#.#.|.#.|.#
            #-#-#-#####-#
            #.#.#.#X|.#.#
            #-#-#-###-#-#
            #.|.#.|.#.#.#
            ###-#-###-#-#
            #.|.#.|.|.#.#
            #############
        """.trimIndent()
        assertThat(map).isEqualTo(expected)
    }

    @Test
    fun `toString -- second extra example -- has reference map`() {
        val input = """^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$"""
        val roomGrid = RoomsGrid.fromPaths(RoomPaths.parse(input))

        val map = roomGrid.toString()

        val expected = """
            ###############
            #.|.|.|.#.|.|.#
            #-###-###-#-#-#
            #.|.#.|.|.#.#.#
            #-#########-#-#
            #.#.|.|.|.|.#.#
            #-#-#########-#
            #.#.#.|X#.|.#.#
            ###-#-###-#-#-#
            #.|.#.#.|.#.|.#
            #-###-#####-###
            #.|.#.|.|.#.#.#
            #-#-#####-#-#-#
            #.#.|.|.|.#.|.#
            ###############
        """.trimIndent()
        assertThat(map).isEqualTo(expected)
    }
}