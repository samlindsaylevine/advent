package advent.year2023.day7

import java.io.File

class CamelCards(private val handsAndBids: List<HandAndBid>) {
    constructor(input: String) : this(input.trim()
            .lines()
            .map {
                val (handPart, bidPart) = it.split(" ")
                HandAndBid(CamelCardHand(handPart), bidPart.toInt())
            }
    )

    fun totalWinnings(comparator: Comparator<CamelCardHand> = CamelCardHand.normalRulesComparator): Int {
        val sorted = handsAndBids.sortedWith(Comparator.comparing(HandAndBid::hand, comparator)).withIndex()
        return sorted.sumOf { it.value.bid * (it.index + 1) }
    }
}

data class HandAndBid(val hand: CamelCardHand, val bid: Int)

enum class CamelCard(val char: Char) {
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    T('T'),
    J('J'),
    Q('Q'),
    K('K'),
    A('A');

    companion object {
        private val byChar = values().associateBy { it.char }
        fun of(char: Char): CamelCard = byChar[char]
                ?: throw IllegalArgumentException("Unrecognized card $char")

        val jokersLowComparator: Comparator<CamelCard> = Comparator { left, right ->
            when {
                left == right -> 0
                left == J -> -1
                right == J -> 1
                else -> left.compareTo(right)
            }
        }
    }
}

enum class CamelCardHandType(val distinctCounts: List<Int>) {
    HIGH_CARD(1, 1, 1, 1, 1),
    ONE_PAIR(2, 1, 1, 1),
    TWO_PAIR(2, 2, 1),
    THREE_OF_A_KIND(3, 1, 1),
    FULL_HOUSE(3, 2),
    FOUR_OF_A_KIND(4, 1),
    FIVE_OF_A_KIND(5);

    companion object {
        private val byCounts = values().associateBy { it.distinctCounts }
        fun of(distinctCounts: List<Int>): CamelCardHandType = byCounts[distinctCounts]
                ?: throw IllegalArgumentException("Unrecognized hand counts $distinctCounts")
    }

    constructor(vararg counts: Int) : this(counts.toList())
}

data class CamelCardHand(val cards: List<CamelCard>) : Comparable<CamelCardHand> {
    constructor(input: String) : this(input.map(CamelCard::of))

    companion object {
        val normalRulesComparator: Comparator<CamelCardHand> = Comparator.comparing(CamelCardHand::type)
                .thenComparing<CamelCard> { it.cards[0] }
                .thenComparing<CamelCard> { it.cards[1] }
                .thenComparing<CamelCard> { it.cards[2] }
                .thenComparing<CamelCard> { it.cards[3] }
                .thenComparing<CamelCard> { it.cards[4] }

        val jokersWildComparator: Comparator<CamelCardHand> = Comparator.comparing(CamelCardHand::typeWithJokers)
                .thenComparing({ it.cards[0] }, CamelCard.jokersLowComparator)
                .thenComparing({ it.cards[1] }, CamelCard.jokersLowComparator)
                .thenComparing({ it.cards[2] }, CamelCard.jokersLowComparator)
                .thenComparing({ it.cards[3] }, CamelCard.jokersLowComparator)
                .thenComparing({ it.cards[4] }, CamelCard.jokersLowComparator)
    }

    private fun List<CamelCard>.distinctCounts(): List<Int> = this.groupingBy { it }.eachCount().values.sortedDescending()
    fun type(): CamelCardHandType = CamelCardHandType.of(cards.distinctCounts())

    fun typeWithJokers(): CamelCardHandType {
        val (jokers, withoutJokers) = cards.partition { it == CamelCard.J }
        val countsWithoutJokers = withoutJokers.distinctCounts()
        val updatedCounts = if (countsWithoutJokers.isEmpty()) {
            listOf(jokers.size)
        } else {
            listOf(countsWithoutJokers.first() + jokers.size) + countsWithoutJokers.drop(1)
        }
        return CamelCardHandType.of(updatedCounts)
    }

    override fun compareTo(other: CamelCardHand): Int = normalRulesComparator.compare(this, other)
}

fun main() {
    val input = File("src/main/kotlin/advent/year2023/day7/input.txt").readText().trim()
    val cards = CamelCards(input)

    println(cards.totalWinnings())
    println(cards.totalWinnings(CamelCardHand.jokersWildComparator))
}
