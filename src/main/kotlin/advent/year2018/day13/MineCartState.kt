package advent.year2018.day13

import advent.year2018.day10.Coordinates
import com.google.common.collect.ImmutableMultiset
import java.io.File
import java.util.Comparator.comparing

class MineCartState(private val tracks: Tracks,
                    private val carts: Collection<MineCart>,
                    private val timeElapsed: Int = 0,
                    val collisionLocations: Set<Coordinates> = setOf()) {
    companion object {
        fun parse(input: String): MineCartState {
            val inputPoints = input.lines().withIndex().flatMap { (y, line) ->
                line.toCharArray().withIndex().map { (x, char) ->
                    val (cartDirection, trackType) = parseChar(char)
                    InputPoint(Coordinates(x, y), cartDirection, trackType)
                }
            }

            val tracks = Tracks(inputPoints.asSequence()
                    .filter { it.trackType != null }
                    .map { it.coordinates to it.trackType!! }
                    .toMap())

            val carts = ImmutableMultiset.copyOf(inputPoints.filter { it.cartDirection != null }
                    .map { MineCart(it.coordinates, it.cartDirection!!) })

            return MineCartState(tracks, carts)
        }

        private fun parseChar(input: Char): Pair<Direction?, TrackType?> = when (input) {
            '<' -> Pair(Direction.LEFT, TrackType.STRAIGHT_ACROSS)
            '>' -> Pair(Direction.RIGHT, TrackType.STRAIGHT_ACROSS)
            '^' -> Pair(Direction.UP, TrackType.STRAIGHT_UP)
            'v' -> Pair(Direction.DOWN, TrackType.STRAIGHT_UP)
            else -> Pair(null, TrackType.values().find { it.picture == input })
        }

        private data class InputPoint(val coordinates: Coordinates,
                                      val cartDirection: Direction?,
                                      val trackType: TrackType?)
    }

    fun cartLocations() = carts.map { it.position }

    fun next(): MineCartState {
        val toMove = carts.sortedWith(comparing { cart: MineCart -> cart.position.y }
                .thenComparing { cart: MineCart -> cart.position.x })

        val (carts, collisions) = moveNextCart(emptyList(), toMove, this.collisionLocations)

        return MineCartState(tracks,
                carts,
                timeElapsed + 1,
                collisions)
    }

    data class MoveResult(val cartLocations: Set<MineCart>, val collisions: Set<Coordinates>)

    // This is a bit of a mess... maybe doing this purely functionally instead of imperatively wasn't the best
    // match to the problem domain.
    private tailrec fun moveNextCart(alreadyMoved: List<MineCart>,
                                     yetToMove: List<MineCart>,
                                     collisions: Set<Coordinates>): MoveResult {
        return if (yetToMove.isEmpty()) {
            MoveResult(alreadyMoved.toSet(), collisions)
        } else {
            val toMove = yetToMove.first()
            val rest = yetToMove.drop(1)

            val nextPos = toMove.next(tracks)

            val collided = yetToMove.any { it.position == nextPos.position } ||
                    alreadyMoved.any { it.position == nextPos.position }

            val nextMoved = if (collided) alreadyMoved.filter { it.position != nextPos.position } else alreadyMoved + nextPos
            val nextCollisions = if (collided) collisions + nextPos.position else collisions

            moveNextCart(nextMoved, rest.filter { it.position != nextPos.position }, nextCollisions)
        }
    }

    fun advancedToCrash() = advancedTo(this) { it.collisionLocations.isNotEmpty() }
    fun advancedToOneCart() = advancedTo(this) { it.carts.size == 1 }

    private tailrec fun advancedTo(state: MineCartState,
                                   condition: (MineCartState) -> Boolean): MineCartState =
            if (condition(state)) {
                state
            } else {
                advancedTo(state.next(), condition)
            }
}

class MineCart(val position: Coordinates,
               val direction: Direction,
               private val nextIntersectionChoices: List<Turn> = listOf(Turn.LEFT, Turn.STRAIGHT, Turn.RIGHT)) {

    fun turnedAtIntersection() = MineCart(this.position,
            this.nextIntersectionChoices.first().newDirection(direction),
            this.nextIntersectionChoices.subList(1, this.nextIntersectionChoices.size) + this.nextIntersectionChoices.subList(0, 1))

    fun turned(turn: Turn) = MineCart(this.position, turn.newDirection(this.direction), nextIntersectionChoices)

    fun next(tracks: Tracks): MineCart {
        val translated = MineCart(this.direction + this.position, direction, nextIntersectionChoices)
        val newTrack = tracks.tracksByPosition[translated.position]
                ?: throw IllegalStateException("Cart derailed at ${translated.position}")
        return newTrack.turnAction(translated)
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);

    companion object {
        private fun getByOrdinal(ordinal: Int) = values()[Math.floorMod(ordinal,
                values().size)]
    }

    fun right() = getByOrdinal(this.ordinal + 1)
    fun left() = getByOrdinal(this.ordinal - 1)
    fun straight() = this

    operator fun plus(position: Coordinates) = Coordinates(this.x + position.x, this.y + position.y)
}

enum class Turn(val newDirection: (Direction) -> Direction) {
    LEFT(Direction::left),
    RIGHT(Direction::right),
    STRAIGHT(Direction::straight)
}

enum class TrackType(val picture: Char,
                     val turnAction: (MineCart) -> MineCart) {
    STRAIGHT_UP('|', { it }),
    STRAIGHT_ACROSS('-', { it }),
    POSITIVE_SLOPE_CURVE('/', {
        val turnDirection = when (it.direction) {
            Direction.UP -> Turn.RIGHT
            Direction.RIGHT -> Turn.LEFT
            Direction.DOWN -> Turn.RIGHT
            Direction.LEFT -> Turn.LEFT
        }
        it.turned(turnDirection)
    }),
    NEGATIVE_SLOPE_CURVE('\\', {
        val turnDirection = when (it.direction) {
            Direction.UP -> Turn.LEFT
            Direction.RIGHT -> Turn.RIGHT
            Direction.DOWN -> Turn.LEFT
            Direction.LEFT -> Turn.RIGHT
        }
        it.turned(turnDirection)
    }),
    INTERSECTION('+', { it.turnedAtIntersection() })
}

data class Tracks(val tracksByPosition: Map<Coordinates, TrackType>)

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day13/input.txt")
            .readText()

    val carts = MineCartState.parse(input)

    println(carts.advancedToCrash().collisionLocations)
    println(carts.advancedToOneCart().cartLocations())
}