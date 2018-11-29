package advent.year2017.day22

import java.io.File

data class VirusGrid(// We don't keep anything in state CLEAN in this map - anything not in the map is CLEAN.
        private val nodeStates: MutableMap<Pair<Int, Int>, NodeState>) {

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
                    .toMutableMap()

            return VirusGrid(initialStates)
        }
    }

    fun isInfected(x: Int, y: Int): Boolean = (nodeState(x, y) == NodeState.INFECTED)
    private fun nodeState(x: Int, y: Int): NodeState = nodeStates[Pair(x, y)] ?: NodeState.CLEAN

    private fun setNodeState(x: Int, y: Int, newState: NodeState) {
        if (newState == NodeState.CLEAN) {
            nodeStates.remove(Pair(x, y))
        } else {
            nodeStates[Pair(x, y)] = newState
        }
    }

    /**
     * Number of infections caused if taking a particular number of steps, starting with a carrier facing up at 0,0.
     */
    fun infectionsCaused(steps: Int, behavior: VirusBehavior = OriginalVirus()): Int {
        val grid = VirusGrid(HashMap(this.nodeStates))
        var carrier = Carrier(0, 0, Direction.UP)
        var infectionCount = 0

        for (i in 1..steps) {

            // If you want to debug progress rate.
            // if (i % 100000 == 0) println("Step $i")

            val currentNodeState = grid.nodeState(carrier.x, carrier.y)

            val (newNodeState, newDirection) = behavior.next(currentNodeState, carrier.direction)

            grid.setNodeState(carrier.x, carrier.y, newNodeState)
            carrier = carrier.copy(direction = newDirection).movedForward()

            if (newNodeState == NodeState.INFECTED) infectionCount++
        }

        return infectionCount
    }
}

enum class NodeState {
    CLEAN,
    INFECTED,
    WEAKENED,
    FLAGGED
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

    println(grid.infectionsCaused(10_000))

    println(grid.infectionsCaused(10_000_000, EvolvedVirus()))
}