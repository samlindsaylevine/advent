package advent.year2023.day4

import advent.utils.findAllNumbers
import advent.utils.loadingCache
import advent.year2020.day14.substringBetween
import java.io.File

class Scratchcards(val cards: List<Scratchcard>) {
    constructor(input: String) : this(input.trim()
            .lines()
            .map(::Scratchcard))

    // Part 1.
    fun pointTotal() = cards.sumOf { it.points() }

    // Part 2.
    fun totalCardCount(): Long = cards.sumOf { cardCount(it.id) }

    // Simple lookup by number.
    private val cardsByNumber: Map<Int, Scratchcard> = cards.associateBy { it.id }

    // Keep a cache of how many cards each card is worth, including all descendants. This lets us reference the
    // same card many times and not have to re-calculate.
    private val cardCountByStartingCardIdCache = loadingCache<Int, Long> { cardId ->
        val wins = cardsByNumber[cardId]?.winningNumberCount ?: 0
        val wonCardIds = (cardId + 1) until (cardId + 1 + wins)
        1 + wonCardIds.sumOf { cardCount(it) }
    }

    // How many cards a particular starting card is worth, including itself.
    private fun cardCount(startingCardId: Int): Long = cardCountByStartingCardIdCache.get(startingCardId)
}

data class Scratchcard(val id: Int, val winningNumbers: Set<Int>, val numbers: List<Int>) {
    constructor(input: String) : this(
            input.substringBetween("Card ", ":").trim().toInt(),
            input.substringBetween(": ", " |").findAllNumbers().toSet(),
            input.substringAfter("| ").findAllNumbers()
    )

    val winningNumberCount = numbers.count { it in winningNumbers }

    // Part 1.
    fun points(): Long = 1L shl (winningNumberCount - 1)
}

fun main() {
    val input = File("src/main/kotlin/advent/year2023/day4/input.txt").readText().trim()
    val cards = Scratchcards(input)
    println(cards.pointTotal())
    println(cards.totalCardCount())
}