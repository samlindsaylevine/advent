package advent.year2017.day19

import java.io.File

/**
 * --- Day 19: A Series of Tubes ---
 * Somehow, a network packet got lost and ended up here.  It's trying to follow a routing diagram (your puzzle input),
 * but it's confused about where to go.
 * Its starting point is just off the top of the diagram. Lines (drawn with |, -, and +) show the path it needs to
 * take, starting by going down onto the only line connected to the top of the diagram. It needs to follow this path
 * until it reaches the end (located somewhere within the diagram) and stop there.
 * Sometimes, the lines cross over each other; in these cases, it needs to continue going the same direction, and only
 * turn left or right when there's no other option.  In addition, someone has left letters on the line; these also
 * don't change its direction, but it can use them to keep track of where it's been. For example:
 *      |          
 *      |  +--+    
 *      A  |  C    
 *  F---|----E|--+ 
 *      |  |  |  D 
 *      +B-+  +--+ 
 * 
 * 
 * Given this diagram, the packet needs to take the following path:
 * 
 * Starting at the only line touching the top of the diagram, it must go down, pass through A, and continue onward to
 * the first +.
 * Travel right, up, and right, passing through B in the process.
 * Continue down (collecting C), right, and up (collecting D).
 * Finally, go all the way left through E and stopping at F.
 * 
 * Following the path to the end, the letters it sees on its path are ABCDEF.
 * The little packet looks up at you, hoping you can help it find the way.  What letters will it see (in the order it
 * would see them) if it follows the path? (The routing diagram is very wide; make sure you view it without line
 * wrapping.)
 * 
 * --- Part Two ---
 * The packet is curious how many steps it needs to go.
 * For example, using the same routing diagram from the example above...
 *      |          
 *      |  +--+    
 *      A  |  C    
 *  F---|--|-E---+ 
 *      |  |  |  D 
 *      +B-+  +--+ 
 * 
 * 
 * ...the packet would go:
 * 
 * 6 steps down (including the first line at the top of the diagram).
 * 3 steps right.
 * 4 steps up.
 * 3 steps right.
 * 4 steps down.
 * 3 steps right.
 * 2 steps up.
 * 13 steps left (including the F it stops on).
 * 
 * This would result in a total of 38 steps.
 * How many steps does the packet need to go?
 * 
 */
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