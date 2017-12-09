package advent.year2017.day9

import java.io.File

class GarbageStream(input: String) {

    private val processed = StreamProcessor(input)

    fun groupCount(): Int = processed.groups.size

    fun totalScore(): Int = processed.groups.map { it.depth }.sum()

    fun uncancelledGarbageCount(): Int = processed.uncancelledGarbageCount

    private data class Group(val text: String, val depth: Int)

    private class StreamProcessor(input: String) {
        var groups = mutableListOf<Group>()
        private var inGarbage = false
        private var nextCharCancelled = false
        // This is a stack, the most recently encountered is the last element.
        private var currentGroups = mutableListOf<StringBuilder>()
        var uncancelledGarbageCount = 0

        init {
            for (char in input) {
                currentGroups.forEach { it.append(char) }

                when {
                    char == '{' && !inGarbage && !nextCharCancelled -> currentGroups.add(StringBuilder("{"))
                    char == '}' && !inGarbage && !nextCharCancelled -> {
                        val currentDepth = currentGroups.size
                        groups.add(Group(currentGroups.removeAt(currentGroups.size - 1).toString(), currentDepth))
                    }
                    char == '<' && !inGarbage && !nextCharCancelled -> inGarbage = true
                    char == '>' && !nextCharCancelled -> inGarbage = false
                    inGarbage && !nextCharCancelled && char != '!' -> uncancelledGarbageCount++
                }

                nextCharCancelled = (char == '!' && !nextCharCancelled)
            }
        }
    }
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day9/input.txt").readText().trim()
    val stream = GarbageStream(input)

    println(stream.totalScore())
    println(stream.uncancelledGarbageCount())
}