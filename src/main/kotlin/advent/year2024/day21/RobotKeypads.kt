package advent.year2024.day21

import advent.meta.readInput
import advent.utils.Point
import advent.utils.next

class RobotKeypads(val codes: List<String>) {
    constructor(input: String) : this(input.trim().lines())

    private fun numericToPoint(numeric: Char): Point = when (numeric) {
        'A' -> Point(2, 0)
        '0' -> Point(1, 0)
        '1' -> Point(0, 1)
        '2' -> Point(1, 1)
        '3' -> Point(2, 1)
        '4' -> Point(0, 2)
        '5' -> Point(1, 2)
        '6' -> Point(2, 2)
        '7' -> Point(0, 3)
        '8' -> Point(1, 3)
        '9' -> Point(2, 3)
        else -> throw IllegalArgumentException("Unrecognized button $numeric")
    }

    private fun numericToShortestDirections(transition: Pair<Char, Char>): String {
        val (fromButton, toButton) = transition
        val from = numericToPoint(fromButton)
        val to = numericToPoint(toButton)
        val crossingTheEmptyPoint = (from.x == 0 && to.y == 0) || (from.y == 0 && to.x == 0)
        val delta = to - from
        // We try to prefer
        // LEFT UP to UP LEFT
        // LEFT DOWN to DOWN LEFT
        // RIGHT UP to UP RIGHT
        // DOWN RIGHT to RIGHT DOWN
        // unless crossing the empty point, in which case we minimize the amount of "turns", i.e., we keep all the same
        // symbol together.
        val movementPart = if (crossingTheEmptyPoint) {
            if (delta.x > 0) {
                ">".repeat(delta.x) + "v".repeat(-delta.y)
            } else {
                "^".repeat(delta.y) + "<".repeat(-delta.x)
            }
        } else {
            when {
                delta.x <= 0 && delta.y <= 0 -> "<".repeat(-delta.x) + "v".repeat(-delta.y)
                delta.x <= 0 && delta.y > 0 -> "<".repeat(-delta.x) + "^".repeat(delta.y)
                delta.x > 0 && delta.y <= 0 -> "v".repeat(-delta.y) + ">".repeat(delta.x)
                else -> "^".repeat(delta.y) + ">".repeat(delta.x)
            }
        }
        return movementPart + "A"
    }

    // Returns a string representing the direction keypad inputs
    fun expandNumericPad(numeric: String): String {
        val steps: List<Pair<Char, Char>> = ("A$numeric").zipWithNext()
        return steps.joinToString(separator = "") { numericToShortestDirections(it) }
    }

    fun expandDirectionPad(directions: String): String {
        val steps = ("A" + directions).zipWithNext()
        return steps.joinToString("") { directionToShortestDirections(it) }
    }

    private fun directionToShortestDirections(transition: Pair<Char, Char>): String {
        val movement = when (transition) {
            '<' to '<' -> ""
            '<' to 'v' -> ">"
            '<' to '>' -> ">>"
            '<' to '^' -> ">^"
            '<' to 'A' -> ">>^"
            '^' to '^' -> ""
            '^' to 'A' -> ">"
            '^' to '<' -> "v<"
            '^' to 'v' -> "v"
            '^' to '>' -> "v>"
            'A' to '^' -> "<"
            'A' to 'A' -> ""
            'A' to '<' -> "v<<"
            'A' to 'v' -> "<v"
            'A' to '>' -> "v"
            'v' to '^' -> "^"
            'v' to 'A' -> "^>"
            'v' to '<' -> "<"
            'v' to 'v' -> ""
            'v' to '>' -> ">"
            '>' to '^' -> "<^"
            '>' to 'A' -> "^"
            '>' to '<' -> "<<"
            '>' to 'v' -> "<"
            '>' to '>' -> ""
            else -> throw IllegalArgumentException("Unrecognized transition $transition")
        }
        return movement + "A"
    }

    fun complexitySum(): Int = codes.sumOf { it.complexity() }

    private fun String.complexity() = lengthOfShortestSequence(this) *
            this.numericPart()

    fun lengthOfShortestSequence(numeric: String) =
        expandNumericPad(numeric).next(2, ::expandDirectionPad).length

    private fun String.numericPart() = this.dropLastWhile { !it.isDigit() }.toInt()
}


// The only decisions to be made when pressing keys is, when our target requires both
// X-axis and Y-axis, presses, in which order to press - e.g., left then up, vs up then left.
// It feels like maybe there is a heuristic we can use where we always prefer one order over the other.
// The left button is one more step away from the A button than the other directions, so that
// seems like an asymmetry.
// To check in on this, we take the sample shortest sequences, and see which orderings appear there.
// (It's possible there might be ties, and Eric might have thrown in a tricky little "wrong order" that didn't
// matter, but it's worth checking in on.)
//
// In practice this reports:
//
// <^: 10 vs ^<: 0
// <v: 23 vs v<: 12
// >^: 23 vs ^>: 0
// >v: 0 vs v>: 0
// So, we clearly want LEFT before UP and RIGHT before UP.
// Our example has nothing to compare right vs down!
// That kind of makes sense if we never have an UP-RIGHT since that is the only way to find ourselves
// needing to do both right and down, at least when controlling another robot and not the original keypad.
// Probably we want to do DOWN before RIGHT, because then the controlling robot will have to make fewer moves
// (down right is down, left, a, right, a, up, a =)
// So that means we always try to prefer:
// LEFT UP to UP LEFT
// LEFT DOWN to DOWN LEFT
// RIGHT UP to UP RIGHT
// DOWN RIGHT to RIGHT DOWN

private fun investigateOrder() {
    val sequenceText = """
        029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
        980A: <v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
        179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
        456A: <v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A
        379A: <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
    """.trimIndent()

    sequenceText.reportCounts("<^", "^<")
    sequenceText.reportCounts("<v", "v<")
    sequenceText.reportCounts(">^", "^>")
    sequenceText.reportCounts(">v", "v>")
}

private fun String.reportCounts(first: String, second: String) {
    fun String.count(substring: String) = this.split(substring).size - 1
    println("$first: ${this.count(first)} vs $second: ${this.count(second)}")
}

fun main() {
    investigateOrder()

    val keypads = RobotKeypads(readInput())

    println(keypads.complexitySum())
}