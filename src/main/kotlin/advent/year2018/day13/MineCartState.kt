package advent.year2018.day13

import advent.year2018.day10.Coordinates
import com.google.common.collect.ImmutableMultiset
import java.io.File
import java.util.Comparator.comparing

/**
 * --- Day 13: Mine Cart Madness ---
 * A crop of this size requires significant logistics to transport produce, soil, fertilizer, and so on. The Elves are
 * very busy pushing things around in carts on some kind of rudimentary system of tracks they've come up with.
 * Seeing as how cart-and-track systems don't appear in recorded history for another 1000 years, the Elves seem to be
 * making this up as they go along. They haven't even figured out how to avoid collisions yet.
 * You map out the tracks (your puzzle input) and see where you can help.
 * Tracks consist of straight paths (| and -), curves (/ and \), and intersections (+). Curves connect exactly two
 * perpendicular pieces of track; for example, this is a closed loop:
 * /----\
 * |    |
 * |    |
 * \----/
 * 
 * Intersections occur when two perpendicular paths cross. At an intersection, a cart is capable of turning left,
 * turning right, or continuing straight.  Here are two loops connected by two intersections:
 * /-----\
 * |     |
 * |  /--+--\
 * |  |  |  |
 * \--+--/  |
 *    |     |
 *    \-----/
 * 
 * Several carts are also on the tracks. Carts always face either up (^), down (v), left (<), or right (>). (On your
 * initial map, the track under each cart is a straight path matching the direction the cart is facing.)
 * Each time a cart has the option to turn (by arriving at any intersection), it turns left the first time, goes
 * straight the second time, turns right the third time, and then repeats those directions starting again with left the
 * fourth time, straight the fifth time, and so on. This process is independent of the particular intersection at which
 * the cart has arrived - that is, the cart has no per-intersection memory.
 * Carts all move at the same speed; they take turns moving a single step at a time. They do this based on their
 * current location: carts on the top row move first (acting from left to right), then carts on the second row move
 * (again from left to right), then carts on the third row, and so on.  Once each cart has moved one step, the process
 * repeats; each of these loops is called a tick.
 * For example, suppose there are two carts on a straight track:
 * |  |  |  |  |
 * v  |  |  |  |
 * |  v  v  |  |
 * |  |  |  v  X
 * |  |  ^  ^  |
 * ^  ^  |  |  |
 * |  |  |  |  |
 * 
 * First, the top cart moves. It is facing down (v), so it moves down one square.  Second, the bottom cart moves.  It
 * is facing up (^), so it moves up one square. Because all carts have moved, the first tick ends.  Then, the process
 * repeats, starting with the first cart.  The first cart moves down, then the second cart moves up - right into the
 * first cart, colliding with it! (The location of the crash is marked with an X.) This ends the second and last tick.
 * Here is a longer example:
 * /->-\        
 * |   |  /----\
 * | /-+--+-\  |
 * | | |  | v  |
 * \-+-/  \-+--/
 *   \------/   
 * 
 * /-->\        
 * |   |  /----\
 * | /-+--+-\  |
 * | | |  | |  |
 * \-+-/  \->--/
 *   \------/   
 * 
 * /---v        
 * |   |  /----\
 * | /-+--+-\  |
 * | | |  | |  |
 * \-+-/  \-+>-/
 *   \------/   
 * 
 * /---\        
 * |   v  /----\
 * | /-+--+-\  |
 * | | |  | |  |
 * \-+-/  \-+->/
 *   \------/   
 * 
 * /---\        
 * |   |  /----\
 * | /->--+-\  |
 * | | |  | |  |
 * \-+-/  \-+--^
 *   \------/   
 * 
 * /---\        
 * |   |  /----\
 * | /-+>-+-\  |
 * | | |  | |  ^
 * \-+-/  \-+--/
 *   \------/   
 * 
 * /---\        
 * |   |  /----\
 * | /-+->+-\  ^
 * | | |  | |  |
 * \-+-/  \-+--/
 *   \------/   
 * 
 * /---\        
 * |   |  /----<
 * | /-+-->-\  |
 * | | |  | |  |
 * \-+-/  \-+--/
 *   \------/   
 * 
 * /---\        
 * |   |  /---<\
 * | /-+--+>\  |
 * | | |  | |  |
 * \-+-/  \-+--/
 *   \------/   
 * 
 * /---\        
 * |   |  /--<-\
 * | /-+--+-v  |
 * | | |  | |  |
 * \-+-/  \-+--/
 *   \------/   
 * 
 * /---\        
 * |   |  /-<--\
 * | /-+--+-\  |
 * | | |  | v  |
 * \-+-/  \-+--/
 *   \------/   
 * 
 * /---\        
 * |   |  /<---\
 * | /-+--+-\  |
 * | | |  | |  |
 * \-+-/  \-<--/
 *   \------/   
 * 
 * /---\        
 * |   |  v----\
 * | /-+--+-\  |
 * | | |  | |  |
 * \-+-/  \<+--/
 *   \------/   
 * 
 * /---\        
 * |   |  /----\
 * | /-+--v-\  |
 * | | |  | |  |
 * \-+-/  ^-+--/
 *   \------/   
 * 
 * /---\        
 * |   |  /----\
 * | /-+--+-\  |
 * | | |  X |  |
 * \-+-/  \-+--/
 *   \------/   
 * 
 * After following their respective paths for a while, the carts eventually crash.  To help prevent crashes, you'd like
 * to know the location of the first crash. Locations are given in X,Y coordinates, where the furthest left column is
 * X=0 and the furthest top row is Y=0:
 *            111
 *  0123456789012
 * 0/---\        
 * 1|   |  /----\
 * 2| /-+--+-\  |
 * 3| | |  X |  |
 * 4\-+-/  \-+--/
 * 5  \------/   
 * 
 * In this example, the location of the first crash is 7,3.
 * 
 * --- Part Two ---
 * There isn't much you can do to prevent crashes in this ridiculous system. However, by predicting the crashes, the
 * Elves know where to be in advance and instantly remove the two crashing carts the moment any crash occurs.
 * They can proceed like this for a while, but eventually, they're going to run out of carts. It could be useful to
 * figure out where the last cart that hasn't crashed will end up.
 * For example:
 * />-<\  
 * |   |  
 * | /<+-\
 * | | | v
 * \>+</ |
 *   |   ^
 *   \<->/
 * 
 * /---\  
 * |   |  
 * | v-+-\
 * | | | |
 * \-+-/ |
 *   |   |
 *   ^---^
 * 
 * /---\  
 * |   |  
 * | /-+-\
 * | v | |
 * \-+-/ |
 *   ^   ^
 *   \---/
 * 
 * /---\  
 * |   |  
 * | /-+-\
 * | | | |
 * \-+-/ ^
 *   |   |
 *   \---/
 * 
 * After four very expensive crashes, a tick ends with only one cart remaining; its final location is 6,4.
 * What is the location of the last cart at the end of the first tick where it is the only cart left?
 * 
 */
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

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day13/input.txt")
            .readText()

    val carts = MineCartState.parse(input)

    println(carts.advancedToCrash().collisionLocations)
    println(carts.advancedToOneCart().cartLocations())
}