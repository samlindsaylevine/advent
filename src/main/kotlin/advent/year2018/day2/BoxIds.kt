package advent.year2018.day2

import java.io.File

class BoxIds(ids: List<String>) {

    private val boxIds = ids.map(::BoxId)

    val checksum = boxIds.count { it.hasLetterAppearingExactly(2) } *
            boxIds.count { it.hasLetterAppearingExactly(3) }

    private val correctBoxIds = boxIds.allPairs()
            .filter { it.first.lettersInCommonWith(it.second).length == it.first.id.length - 1 }
            .first()

    val lettersInCommonBetweenCorrectBoxIds: String = correctBoxIds.first.lettersInCommonWith(correctBoxIds.second)

    private fun <T> List<T>.allPairs(): Sequence<Pair<T, T>> = this.asSequence()
            .flatMap { first -> this.asSequence().map { second -> Pair(first, second) } }
}


class BoxId(val id: String) {
    private val letterFrequency = id.toCharArray().asSequence()
            .groupingBy { it }
            .eachCount()

    fun hasLetterAppearingExactly(time: Int) = letterFrequency.values.contains(time)

    fun lettersInCommonWith(other: BoxId) = id.zip(other.id)
            .asSequence()
            .filter { it.first == it.second }
            .map { it.first }
            .joinToString("")
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day2/input.txt")
            .readText()
            .trim()

    val boxIds = BoxIds(input.split("\n"))

    println(boxIds.checksum)
    println(boxIds.lettersInCommonBetweenCorrectBoxIds)
}