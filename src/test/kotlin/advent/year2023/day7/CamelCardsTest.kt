package advent.year2023.day7

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CamelCardsTest {

    @Test
    fun `total winnings -- reference input -- 6440`() {
        val input = """
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
        """.trimIndent()

        val cards = CamelCards(input)
        val winnings = cards.totalWinnings()

        assertThat(winnings).isEqualTo(6440)
    }

    @Test
    fun `total winnings -- jokers wild -- 5905`() {
        val input = """
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
        """.trimIndent()

        val cards = CamelCards(input)
        val winnings = cards.totalWinnings(CamelCardHand.jokersWildComparator)

        assertThat(winnings).isEqualTo(5905)
    }

    @ParameterizedTest(name = "hand types -- jokers wild, hand {0} -- {1}")
    @CsvSource(
            "32T3K, ONE_PAIR",
            "KK677, TWO_PAIR",
            "T55J5, FOUR_OF_A_KIND",
            "KTJJT, FOUR_OF_A_KIND",
            "QQQJA, FOUR_OF_A_KIND"
    )
    fun `hand types -- jokers wild -- hand type is as given`(handString: String, expectedHandType: CamelCardHandType) {
        val hand = CamelCardHand(handString)
        val type = hand.typeWithJokers()

        assertThat(type).isEqualTo(expectedHandType)
    }

    @Test
    fun `card orders -- sorted jokers low -- AKQ1098765432J`() {
        val cards = CamelCard.values().sortedWith(CamelCard.jokersLowComparator)

        assertThat(cards).containsExactly(
                CamelCard.J,
                CamelCard.TWO,
                CamelCard.THREE,
                CamelCard.FOUR,
                CamelCard.FIVE,
                CamelCard.SIX,
                CamelCard.SEVEN,
                CamelCard.EIGHT,
                CamelCard.NINE,
                CamelCard.T,
                CamelCard.Q,
                CamelCard.K,
                CamelCard.A
        )
    }
}