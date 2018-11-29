package advent.year2017.day22

import java.io.File

class VirusGrid(// We don't keep anything in state CLEAN in this map - anything not in the map is CLEAN.
        private val nodeStates: Map<Pair<Int, Int>, NodeState>) {

    companion object {
        fun fromInput(input: String): VirusGrid {
            val lines = input.split("\n")

            val height = lines.size
            val width = lines.first().length

            val yOffset = (height - 1) / 2
            val xOffset = (width - 1) / 2

            val initialStates = (-xOffset..xOffset).flatMap { x ->
                (-yOffset..yOffset).map { y -> Pair(x, y) }
            }
                    .asSequence()
                    .filter { pair -> lines[-pair.second + yOffset][pair.first + xOffset] == '#' }
                    .map { coordinates -> Pair(coordinates, NodeState.INFECTED) }
                    .toMap()

            return VirusGrid(initialStates)
        }
    }

    fun isInfected(x: Int, y: Int): Boolean = (nodeState(x, y) == NodeState.INFECTED)
    fun nodeState(x: Int, y: Int): NodeState = nodeStates[Pair(x, y)] ?: NodeState.CLEAN

    fun withNodeState(x: Int, y: Int, newState: NodeState): VirusGrid = if (newState == NodeState.CLEAN) {
        VirusGrid(nodeStates - Pair(x, y))
    } else {
        VirusGrid(nodeStates + Pair(Pair(x, y), newState))
    }
}

enum class NodeState {
    CLEAN,
    INFECTED,
    WEAKENED,
    FLAGGED
}

class CarrierTrip(val causedAnInfectionBursts: Int,
                  private val totalBursts: Int,
                  private val carrier: Carrier,
                  val grid: VirusGrid,
                  val behavior: VirusBehavior) {

    constructor(grid: VirusGrid, behavior: VirusBehavior = OriginalVirus()) : this(0,
            0,
            Carrier(0, 0, Direction.UP),
            grid,
            behavior)

    fun next(): CarrierTrip {
        val currentNodeState = grid.nodeState(carrier.x, carrier.y)

        val (newNodeState, newDirection) = behavior.next(currentNodeState, carrier.direction)

        val turnedCarrier = carrier.copy(direction = newDirection)

        val newInfectionCount = if (newNodeState == NodeState.INFECTED) causedAnInfectionBursts + 1 else causedAnInfectionBursts
        val nextGrid = grid.withNodeState(carrier.x, carrier.y, newNodeState)

        val nextCarrier = turnedCarrier.movedForward()

        return CarrierTrip(newInfectionCount,
                totalBursts + 1,
                nextCarrier,
                nextGrid,
                behavior)
    }

    fun afterBursts(count: Int): CarrierTrip = (0 until count).fold(this) { trip, i ->
        if (i % 10000 == 0) println(i)
        trip.next()
    }
}

data class Carrier(val x: Int, val y: Int, val direction: Direction) {
    fun movedForward() = Carrier(this.x + direction.x, this.y + direction.y, direction)
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, 1),
    RIGHT(1, 0),
    DOWN(0, -1),
    LEFT(-1, 0);

    companion object {
        private fun getByOrdinal(ordinal: Int) = Direction.values()[Math.floorMod(ordinal,
                Direction.values().size)]
    }

    fun right() = getByOrdinal(this.ordinal + 1)
    fun left() = getByOrdinal(this.ordinal - 1)
}

interface VirusBehavior {
    fun next(currentState: NodeState, currentDirection: Direction): Pair<NodeState, Direction>
}

class OriginalVirus : VirusBehavior {
    override fun next(currentState: NodeState, currentDirection: Direction) = when (currentState) {
        NodeState.CLEAN -> Pair(NodeState.INFECTED, currentDirection.left())
        NodeState.INFECTED -> Pair(NodeState.CLEAN, currentDirection.right())
        else -> Pair(NodeState.CLEAN, currentDirection.left())
    }
}

class EvolvedVirus : VirusBehavior {
    override fun next(currentState: NodeState, currentDirection: Direction) = when (currentState) {
        NodeState.CLEAN -> Pair(NodeState.WEAKENED, currentDirection.left())
        NodeState.WEAKENED -> Pair(NodeState.INFECTED, currentDirection)
        NodeState.INFECTED -> Pair(NodeState.FLAGGED, currentDirection.right())
        NodeState.FLAGGED -> Pair(NodeState.CLEAN, currentDirection.right().right())
    }
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day22/input.txt")
            .readText()
            .trim()

    val grid = VirusGrid.fromInput(input)

    println(CarrierTrip(grid).afterBursts(10_000).causedAnInfectionBursts)

    // The actual sane way to do this would probably be to either
    // a) look for when the grid cycles, i.e., the grid & carrier state is exactly the same at any point in the trip as
    // it has been once before; and then calculate how many cycles are taken up by 10,000,000 steps and not have to
    // simulate them all, then simply calculate the number of those steps that would have caused an infection;
    // or b) if it is not cycling, figure out if there is some other kind of periodicity / curve fitting possible
    // in order to extrapolate a value.

    // But, hell, developer time is expensive and execution time is cheap. I just ran it and let it go for
}