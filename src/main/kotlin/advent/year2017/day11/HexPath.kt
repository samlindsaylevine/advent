package advent.year2017.day11

import java.io.File

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

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day11/input.txt").readText()

    val path = HexPath(input)
    println(path.endpoint.distanceFromOrigin())
    println(path.furthestDistance)
}