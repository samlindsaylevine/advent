package advent.year2017.day22

import java.io.File

/**
 * --- Day 22: Sporifica Virus ---
 * Diagnostics indicate that the local grid computing cluster has been contaminated with the Sporifica Virus. The grid
 * computing cluster is a seemingly-infinite two-dimensional grid of compute nodes.  Each node is either clean or
 * infected by the virus.
 * To prevent overloading the nodes (which would render them useless to the virus) or detection by system
 * administrators, exactly one virus carrier moves through the network, infecting or cleaning nodes as it moves. The
 * virus carrier is always located on a single node in the network (the current node) and keeps track of the direction
 * it is facing.
 * To avoid detection, the virus carrier works in bursts; in each burst, it wakes up, does some work, and goes back to
 * sleep. The following steps are all executed in order one time each burst:
 * 
 * If the current node is infected, it turns to its right.  Otherwise, it turns to its left. (Turning is done in-place;
 * the current node does not change.)
 * If the current node is clean, it becomes infected.  Otherwise, it becomes cleaned. (This is done after the node is
 * considered for the purposes of changing direction.)
 * The virus carrier moves forward one node in the direction it is facing.
 * 
 * Diagnostics have also provided a map of the node infection status (your puzzle input).  Clean nodes are shown as .;
 * infected nodes are shown as #.  This map only shows the center of the grid; there are many more nodes beyond those
 * shown, but none of them are currently infected.
 * The virus carrier begins in the middle of the map facing up.
 * For example, suppose you are given a map like this:
 * ..#
 * #..
 * ...
 * 
 * Then, the middle of the infinite grid looks like this, with the virus carrier's position marked with [ ]:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . # . . .
 * . . . #[.]. . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * The virus carrier is on a clean node, so it turns left, infects the node, and moves left:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . # . . .
 * . . .[#]# . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * The virus carrier is on an infected node, so it turns right, cleans the node, and moves up:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . .[.]. # . . .
 * . . . . # . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * Four times in a row, the virus carrier finds a clean, infects it, turns left, and moves forward, ending in the same
 * place and still facing up:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . #[#]. # . . .
 * . . # # # . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * Now on the same node as before, it sees an infection, which causes it to turn right, clean the node, and move
 * forward:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . # .[.]# . . .
 * . . # # # . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * After the above actions, a total of 7 bursts of activity had taken place. Of them, 5 bursts of activity caused an
 * infection.
 * After a total of 70, the grid looks like this, with the virus carrier facing up:
 * . . . . . # # . .
 * . . . . # . . # .
 * . . . # . . . . #
 * . . # . #[.]. . #
 * . . # . # . . # .
 * . . . . . # # . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * By this time, 41 bursts of activity caused an infection (though most of those nodes have since been cleaned).
 * After a total of 10000 bursts of activity, 5587 bursts will have caused an infection.
 * Given your actual map, after 10000 bursts of activity, how many bursts cause a node to become infected? (Do not
 * count nodes that begin infected.)
 * 
 * --- Part Two ---
 * As you go to remove the virus from the infected nodes, it evolves to resist your attempt.
 * Now, before it infects a clean node, it will weaken it to disable your defenses. If it encounters an infected node,
 * it will instead flag the node to be cleaned in the future.  So:
 * 
 * Clean nodes become weakened.
 * Weakened nodes become infected.
 * Infected nodes become flagged.
 * Flagged nodes become clean.
 * 
 * Every node is always in exactly one of the above states.
 * The virus carrier still functions in a similar way, but now uses the following logic during its bursts of action:
 * 
 * Decide which way to turn based on the current node:
 *   
 *   If it is clean, it turns left.
 *   If it is weakened, it does not turn, and will continue moving in the same direction.
 *   If it is infected, it turns right.
 *   If it is flagged, it reverses direction, and will go back the way it came.
 *   
 * 
 * Modify the state of the current node, as described above.
 * The virus carrier moves forward one node in the direction it is facing.
 * 
 * Start with the same map (still using . for clean and # for infected) and still with the virus carrier starting in
 * the middle and facing up.
 * Using the same initial state as the previous example, and drawing weakened as W and flagged as F, the middle of the
 * infinite grid looks like this, with the virus carrier's position again marked with [ ]:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . # . . .
 * . . . #[.]. . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * This is the same as before, since no initial nodes are weakened or flagged.  The virus carrier is on a clean node,
 * so it still turns left, instead weakens the node, and moves left:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . # . . .
 * . . .[#]W . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * The virus carrier is on an infected node, so it still turns right, instead flags the node, and moves up:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . .[.]. # . . .
 * . . . F W . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * This process repeats three more times, ending on the previously-flagged node and facing right:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . W W . # . . .
 * . . W[F]W . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * Finding a flagged node, it reverses direction and cleans the node:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . W W . # . . .
 * . .[W]. W . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * The weakened node becomes infected, and it continues in the same direction:
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . W W . # . . .
 * .[.]# . W . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * . . . . . . . . .
 * 
 * Of the first 100 bursts, 26 will result in infection. Unfortunately, another feature of this evolved virus is speed;
 * of the first 10000000 bursts, 2511944 will result in infection.
 * Given your actual map, after 10000000 bursts of activity, how many bursts cause a node to become infected? (Do not
 * count nodes that begin infected.)
 * 
 */
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
        private fun getByOrdinal(ordinal: Int) = values()[Math.floorMod(ordinal,
                values().size)]
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

fun main() {
    val input = File("src/main/kotlin/advent/year2017/day22/input.txt")
            .readText()
            .trim()

    val grid = VirusGrid.fromInput(input)

    println(grid.infectionsCaused(10_000))

    println(grid.infectionsCaused(10_000_000, EvolvedVirus()))
}