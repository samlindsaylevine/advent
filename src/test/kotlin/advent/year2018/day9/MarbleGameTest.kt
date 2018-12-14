package advent.year2018.day9

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MarbleGameTest {

    @ParameterizedTest(name = "highScore -- {0} players and {1} marbles -- {2} points")
    @CsvSource("9, 25, 32",
            "10, 1618, 8317",
            "13, 7999, 146373",
            "17, 1104, 2764",
            "21, 6111, 54718",
            "30, 5807, 37305")
    fun `highScore -- reference games -- reference scores`(numPlayers: Int,
                                                           lastMarble: Int,
                                                           expectedScore: Long) {
        val game = MarbleGame(numPlayers, lastMarble)

        val score = game.highScore()

        assertThat(score).isEqualTo(expectedScore)
    }
}