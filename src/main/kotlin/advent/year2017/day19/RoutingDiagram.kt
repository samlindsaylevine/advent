package advent.year2017.day19

import java.io.File

class RoutingDiagram(text: String) {

    val rows: List<CharArray> = text.split("\n")
            .filter { it.isNotBlank() }
            .map { it.toCharArray() }

    fun traverse(): Voyage {
        val startingX = rows[0].indexOfFirst { it != ' ' }
        var journeyComplete = false
        var currentPosition = Position(startingX, 0)
        var previousPosition = Position(startingX, -1)
        var stepsTaken = 1
        val encountered = StringBuilder()

        while (!journeyComplete) {
            val currentValue = valueAt(currentPosition)
            if (currentValue != '-' && currentValue != '|' && currentValue != '+') {
                encountered.append(currentValue)
            }

            val straightAhead = currentPosition.straightFrom(previousPosition)

            if (isTraversable(straightAhead)) {
                previousPosition = currentPosition
                currentPosition = straightAhead
                stepsTaken++
            } else {
                val legalMoves = (currentPosition.neighbors - previousPosition)
                        .filter { isTraversable(it) }
                when {
                    legalMoves.size > 1 -> throw IllegalStateException("Multiple options $legalMoves, what do")
                    legalMoves.size == 1 -> {
                        previousPosition = currentPosition
                        currentPosition = legalMoves.first()
                        stepsTaken++
                    }
                    else -> journeyComplete = true
                }

            }
        }

        return Voyage(encountered.toString(), stepsTaken)
    }

    private fun valueAt(position: Position): Char {
        if (position.y < 0 || position.y >= rows.size) return ' '

        val row = rows[position.y]

        if (position.x < 0 || position.x >= row.size) return ' '

        return row[position.x]
    }

    private fun isTraversable(position: Position): Boolean = valueAt(position) != ' '

    data class Voyage(val encounteredLetters: String, val stepsTaken: Int)

    private data class Position(val x: Int, val y: Int) {
        val neighbors by lazy {
            setOf(Position(x - 1, y),
                    Position(x, y + 1),
                    Position(x + 1, y),
                    Position(x, y - 1))
        }

        fun straightFrom(previous: Position) = Position(x + (x - previous.x), y + (y - previous.y))
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2017/day19/input.txt")
            .readText()

    val diagram = RoutingDiagram(input)
    val voyage = diagram.traverse()

    println(voyage.encounteredLetters)
    println(voyage.stepsTaken)
}