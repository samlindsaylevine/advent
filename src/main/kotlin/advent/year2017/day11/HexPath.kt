package advent.year2017.day11

import java.io.File

/**
 * --- Day 11: Hex Ed ---
 * Crossing the bridge, you've barely reached the other side of the stream when a program comes up to you, clearly in
 * distress.  "It's my child process," she says, "he's gotten lost in an infinite grid!"
 * Fortunately for her, you have plenty of experience with infinite grids.
 * Unfortunately for you, it's a hex grid.
 * The hexagons ("hexes") in this grid are aligned such that adjacent hexes can be found to the north, northeast,
 * southeast, south, southwest, and northwest:
 *   \ n  /
 * nw +--+ ne
 *   /    \
 * -+      +-
 *   \    /
 * sw +--+ se
 *   / s  \
 * 
 * You have the path the child process took. Starting where he started, you need to determine the fewest number of
 * steps required to reach him. (A "step" means to move from the hex you are in to any adjacent hex.)
 * For example:
 * 
 * ne,ne,ne is 3 steps away.
 * ne,ne,sw,sw is 0 steps away (back where you started).
 * ne,ne,s,s is 2 steps away (se,se).
 * se,sw,se,sw,sw is 3 steps away (s,s,sw).
 * 
 * 
 * --- Part Two ---
 * How many steps away is the furthest he ever got from his starting position?
 * 
 */
class HexPath(text: String) {
    private val endpointAndFurthest = text.trim().split(",")
            .fold(LocationAndFurthest(), { loc, dir -> loc + HexDirection.valueOf(dir) })

    val endpoint = endpointAndFurthest.location
    val furthestDistance = endpointAndFurthest.furthestDistance
}

/**
 * We can represent a hex location using a vector basis of the N direction and the SE direction.
 *
 * In this case, a step in the NE direction is represented as (+1, +1) N and SE.
 */
class HexLocation(val nAxis: Int, val seAxis: Int) {

    fun distanceFromOrigin(): Int = when {
    // We can use (+1, +1) or (-1, -1) single steps (i.e., along the NE axis) to minimize distance in this case.
        sign(nAxis) == sign(seAxis) -> Math.max(Math.abs(nAxis), Math.abs(seAxis))
    // In this case we cannot and only use steps along the N and SE axes.
        else -> Math.abs(nAxis) + Math.abs(seAxis)
    }

    private fun sign(n: Int): Int = when {
        n < 0 -> -1
        n > 0 -> 1
        else -> 0
    }

    operator fun plus(direction: HexDirection) =
            HexLocation(this.nAxis + direction.nAxis, this.seAxis + direction.seAxis)
}

enum class HexDirection(val nAxis: Int, val seAxis: Int) {
    n(1, 0),
    ne(1, 1),
    se(0, 1),
    s(-1, 0),
    sw(-1, -1),
    nw(0, -1);
}

private class LocationAndFurthest(val location: HexLocation = HexLocation(0, 0),
                                  val furthestDistance: Int = 0) {
    operator fun plus(direction: HexDirection): LocationAndFurthest {
        val newLocation = location + direction
        val newFurthest = Math.max(furthestDistance, newLocation.distanceFromOrigin())
        return LocationAndFurthest(newLocation, newFurthest)
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2017/day11/input.txt").readText()

    val path = HexPath(input)
    println(path.endpoint.distanceFromOrigin())
    println(path.furthestDistance)
}