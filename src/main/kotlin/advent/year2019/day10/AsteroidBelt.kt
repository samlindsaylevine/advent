package advent.year2019.day10

import advent.utils.Point
import advent.utils.times
import advent.year2018.day6.allMinBy
import java.io.File

class AsteroidBelt(private val asteroids: Set<Point>) {

    companion object {
        fun parse(input: String): AsteroidBelt {
            val asteroids = input.lines().withIndex().flatMap { (y, line) ->
                line.withIndex().mapNotNull { (x, char) -> if (char == '#') Point(x, y) else null }
            }
            return AsteroidBelt(asteroids.toSet())
        }
    }

    fun mostObservable() = asteroids
            .map { MostObservable(it, numObservableFrom(it)) }
            .maxByOrNull { it.count }

  data class MostObservable(val asteroid: Point, val count: Int)

    private fun numObservableFrom(asteroid: Point) = asteroids.count { asteroid canObserve it }

    private infix fun Point.canObserve(other: Point): Boolean {
        if (this == other) return false
        // To calculate whether we will hit some other asteroid while moving from this to other, we:
        // * Calculate the vector from this to other (i.e., other - this)
        // * Determine whether this vector has any fractional part that has integer coordinates (putting us at risk of
        //   hitting another asteroid
        //   * Do this by calculating the GCD of the x and y of this vector; dividing the vector by that value gives
        //     us the step vector
        // * See if there is any asteroid at an integer multiple of the step vector
        val vector = other - this
        val gcd = gcd(vector.x, vector.y)
        val stepVector = vector / gcd
        return (1 until gcd)
                .map { this + it * stepVector }
                .none { asteroids.contains(it) }
    }

    fun vaporizedAsteroids(): List<Point> {
        val origin = mostObservable()?.asteroid ?: return emptyList()

        val first = asteroids.allMinBy { (it - origin).theta }
                .minByOrNull { it.distanceFrom(origin) }
                ?: return emptyList()

        return vaporizedAsteroids(listOf(first), origin, asteroids - first - origin)
    }

    private tailrec fun vaporizedAsteroids(soFar: List<Point>,
                                           origin: Point,
                                           remaining: Set<Point>): List<Point> {
        val next = nextHit(soFar.last(), origin, remaining)
        return if (next == null) {
            soFar
        } else {
            vaporizedAsteroids(soFar + next, origin, remaining - next)
        }
    }

    private fun nextHit(previousHit: Point,
                        origin: Point,
                        asteroids: Set<Point>): Point? {
        val previousTheta = (previousHit - origin).theta
        val candidates = if (asteroids.any { (it - origin).theta > previousTheta }) {
            asteroids.filter { (it - origin).theta > previousTheta }.allMinBy { (it - origin).theta }
        } else {
            asteroids.allMinBy { (it - origin).theta }
        }
        return candidates.minByOrNull { it.distanceFrom(origin) }
    }

}

/**
 * Who is ready for some EUCLIDEAN ALGORITHM!?
 *
 * Note - the GCD should always return a positive value.
 */
tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) Math.abs(a) else gcd(b, a % b)

tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) Math.abs(a) else gcd(b, a % b)


/**
 * Arctan only returns in the range -pi/2 to +pi/2. We want a range that goes from 0 to 2*pi. Furthermore,
 * we want the angle 0 to be pointing up, and increase positively in the clockwise direction.
 */
val Point.theta: Double
    get() {
        val atan = Math.atan(-this.y.toDouble() / this.x)

        val clockwiseFromUp = Math.PI / 2 - atan

        return if (x >= 0) clockwiseFromUp else (clockwiseFromUp + Math.PI)
    }

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day10/input.txt")
            .readText()
            .trim()

    val asteroids = AsteroidBelt.parse(input)

    println(asteroids.mostObservable()?.count)

    val vaporized = asteroids.vaporizedAsteroids()
    println(vaporized[199].x * 100 + vaporized[199].y)
}