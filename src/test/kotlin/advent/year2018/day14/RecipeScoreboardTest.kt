package advent.year2018.day14

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RecipeScoreboardTest {

    @ParameterizedTest(name = "nextRecipeScores -- after {0} recipes -- {1}")
    @CsvSource("5, 0124515891",
            "9, 5158916779",
            "18, 9251071085",
            "2018, 5941429882")
    fun `nextRecipeScores -- reference counts -- reference values`(recipes: Int, expected: String) {
        val result = RecipeScoreboard().nextRecipeScores(recipes, 10)

        assertThat(result).isEqualTo(expected)
    }

    @ParameterizedTest(name = "countLeftOfSequence -- sequence {0} -- appears after {1} recipes")
    @CsvSource("51589, 9",
            "01245, 5",
            "92510, 18",
            "59414, 2018")
    fun `countLeftOfSequence -- reference counts -- reference values`(sequence: String, expected: Int) {
        val result = RecipeScoreboard().countLeftOfSequence(sequence)

        assertThat(result).isEqualTo(expected)
    }
}