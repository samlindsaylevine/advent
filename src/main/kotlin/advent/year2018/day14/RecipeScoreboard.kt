package advent.year2018.day14

import java.util.*
import kotlin.streams.toList

/**
 * Note - mutable because the immutable solution took too long (having to make copies of the whole list every time to
 * step forward).
 */
class RecipeScoreboard(private var recipes: MutableList<Int> = mutableListOf(3, 7),
                       private var firstElfIndex: Int = 0,
                       private var secondElfIndex: Int = 1) {

    fun advance() {
        val firstElfValue = recipes[firstElfIndex]
        val secondElfValue = recipes[secondElfIndex]
        recipes.addAll(digitSum(firstElfValue, secondElfValue))

        firstElfIndex = (firstElfIndex + 1 + firstElfValue) % recipes.size
        secondElfIndex = (secondElfIndex + 1 + secondElfValue) % recipes.size
    }

    /**
     * a and b must be single digit positive integers.
     */
    private fun digitSum(a: Int, b: Int): List<Int> {
        val sum = a + b
        return if (sum >= 10) listOf(1, sum % 10) else listOf(sum)
    }

    /**
     * Advances the board until the recipe scores can be calculated, then returns them.
     *
     * @param after Wait until this many recipes have been calculated.
     * @param count Then return this many of the next recipes (concated together).
     */
    fun nextRecipeScores(after: Int, count: Int): String {
        while (recipes.size < after + count) {
            this.advance()
        }

        val scores = recipes.subList(after, after + count)

        return scores.joinToString(separator = "")
    }

    fun countLeftOfSequence(sequence: String): Int {
        val sublist = sequence.chars().map { Character.getNumericValue(it) }.toList()

        // As a speed-up optimization we don't check the whole list of recipes for the sublist (can you say O(N^2)?), we
        // only look at the last few which includes the newly added 1 or 2 recipes. I didn't bother thinking too hard
        // about how many to look at and just always look at double the length of the sublist we want.
        while (Collections.indexOfSubList(recipes.takeLast(sequence.length * 2), sublist) == -1) {
            this.advance()
        }

        return Collections.indexOfSubList(recipes, sublist)
    }
}

fun main() {
    println(RecipeScoreboard().nextRecipeScores(after = 890691, count = 10))
    println(RecipeScoreboard().countLeftOfSequence("890691"))
}