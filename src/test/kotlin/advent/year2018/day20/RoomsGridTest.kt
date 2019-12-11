package advent.year2018.day20

import advent.utils.Direction.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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

    @ParameterizedTest(name = "distanceToFurthestRoom -- {0} -- {1}")
    @CsvSource("^WNE\$, 3",
            "^ENWWW(NEEE|SSE(EE|N))\$, 10",
            "^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN\$, 18",
            "^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))\$, 23",
            "^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))\$, 31")
    fun `distanceToFurthestRoom -- reference examples -- reference values`(input: String, expected: Int) {
        val roomsGrid = RoomsGrid.fromPaths(RoomPaths.parse(input))

        val distance = roomsGrid.distanceToFurthestRoom()

        assertThat(distance).isEqualTo(expected)
    }
}