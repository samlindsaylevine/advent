package advent.year2018.day2

import java.io.File

class BoxIds(ids: List<String>) {

    private val boxIds = ids.map(::BoxId)

    private class BoxId(val id: String) {
        private val letterFrequency = id.toCharArray().asSequence()
                .groupingBy { it }
                .eachCount()

        fun hasLetterAppearingExactly(time: Int) = letterFrequency.values.contains(time)
    }

    val checksum = boxIds.count { it.hasLetterAppearingExactly(2) } *
            boxIds.count { it.hasLetterAppearingExactly(3) }
}


fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day2/input.txt")
            .readText()
            .trim()

    val boxIds = BoxIds(input.split("\n"))

    println(boxIds.checksum)
}