package advent.year2019.day18

import advent.utils.*
import java.io.File

data class KeyedVault(private val walls: Set<Point>,
                      private val doors: Map<Point, Key>,
                      private val keys: Map<Point, Key>,
                      private val starts: Set<Point>) {
  companion object {
    fun parse(input: String): KeyedVault {
      val walls = mutableSetOf<Point>()
      val doors = mutableMapOf<Point, Key>()
      val keys = mutableMapOf<Point, Key>()
      val starts = mutableSetOf<Point>()

      input.trim().lines().forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
          val here = Point(x, y)
          when {
            char == '#' -> walls.add(here)
            char == '.' -> Unit
            char == '@' -> starts.add(here)
            char.isLowerCase() -> keys[here] = char
            char.isUpperCase() -> doors[here] = char.toLowerCase()
          }
        }
      }

      return KeyedVault(walls,
              doors,
              keys,
              starts)
    }
  }

  private val nodeLocations: Set<Point> by lazy {
    (doors.keys + keys.keys + starts).toSet()
  }

  /**
   * Trying to find the shortest path by walking individual squares takes too long for our problem input.
   * Instead, we  will save some time by converting our vault to a graph of nodes with paths between them, and then
   * optimize on that.
   */
  fun shortestPathLength() = this.toGraph().shortestPathLength()

  private fun toGraph(): KeyedVaultGraph {
    val edges = mutableMapOf<KeyedVaultNode, Set<VaultEdge>>()

    val nodeToPosition: Map<KeyedVaultNode, Point> = starts.associateBy { StartNode() } +
            doors.map { DoorNode(it.value) to it.key }.toMap() +
            keys.map { KeyNode(it.value) to it.key }.toMap()

    val nodePairs = nodeToPosition.keys.toList().allUnorderedPairs()

    nodePairs.forEach { pair ->
      val positions = pair.map { nodeToPosition[it] ?: throw IllegalStateException("Missing node $it") }
      val distance = distanceBetween(positions)
      if (distance != null) {
        val (first, second) = pair
        edges.merge(first, setOf(VaultEdge(second, distance))) { a, b -> a + b }
        edges.merge(second, setOf(VaultEdge(first, distance))) { a, b -> a + b }
      }
    }

    return KeyedVaultGraph(edges)
  }

  private fun distanceBetween(nodes: UnorderedPair<Point>): Int? {
    val (first, second) = nodes

    val finder = ShortestPathFinder()

    val paths = finder.find(start = first,
            end = EndState(second),
            nextSteps = Steps {
              it.adjacentNeighbors.filter { neighbor ->
                !walls.contains(neighbor) &&
                        (neighbor == second || !nodeLocations.contains(neighbor))
              }.toSet()
            },
            // Since we only care about the length of the shortest path, we can collapse any paths that end up
            // at the same state, regardless of how they got there.
            collapse = Collapse { steps: List<Point> -> steps.last() })

    return paths.firstOrNull()?.totalCost
  }

  fun split(): KeyedVault {
    if (starts.size != 1) throw IllegalStateException("Can only split with one start; found ${starts.size}")

    val start = starts.first()

    val newStarts = setOf(start + Point(-1, -1),
            start + Point(1, -1),
            start + Point(1, 1),
            start + Point(-1, 1))

    val newWalls = start.adjacentNeighbors + start

    return KeyedVault(this.walls + newWalls,
            this.doors,
            this.keys,
            newStarts)
  }
}

private class KeyedVaultGraph(val edges: Map<KeyedVaultNode, Set<VaultEdge>>) {
  val allKeys = edges.keys.filterIsInstance<KeyNode>().map { it.key }.toSet()

  fun shortestPathLength(): Int {
    val finder = ShortestPathFinder()

    val paths = finder.find(start = VaultGraphExplorationState(edges.keys.filterIsInstance<StartNode>().toSet(),
            emptySet(),
            emptySet()),
            end = EndCondition { it.keysOwned.containsAll(allKeys) },
            nextSteps = StepsWithCost(::nextOptions),
            // Since we only care about the length of the shortest path, we can collapse any paths that end up
            // at the same state, regardless of how they got there.
            collapse = Collapse { steps: List<VaultGraphExplorationState> -> steps.last() },
            filter = Filter { steps: List<VaultGraphExplorationState> -> steps.last().keysOwned.size < steps.size / 4 },
            reportEvery = 100)

    println(paths.first())

    return paths.first().totalCost
  }

  private fun nextOptions(state: VaultGraphExplorationState): Set<Step<VaultGraphExplorationState>> =
          state.positions
                  .flatMap { from -> (edges[from] ?: emptySet()).map { from to it } }
                  .filter { state.canVisit(it.second.target) }
                  .map { Step(state.moving(from = it.first, to = it.second.target), it.second.distance) }
                  .toSet()

  private fun VaultGraphExplorationState.canVisit(node: KeyedVaultNode) =
          node !is DoorNode || this.keysOwned.contains(node.key)

  private fun VaultGraphExplorationState.moving(from: KeyedVaultNode, to: KeyedVaultNode) = when (to) {
    is KeyNode -> VaultGraphExplorationState(this.positions - from + to,
            this.keysOwned + to.key,
            this.doorsOpened)
    is DoorNode -> VaultGraphExplorationState(this.positions - from + to,
            this.keysOwned,
            this.doorsOpened + to.key)
    else -> VaultGraphExplorationState(this.positions - from + to, this.keysOwned, this.doorsOpened)
  }
}

private data class VaultEdge(val target: KeyedVaultNode, val distance: Int)

private sealed class KeyedVaultNode
private class StartNode : KeyedVaultNode() {
  override fun equals(other: Any?) = this === other
  override fun hashCode() = System.identityHashCode(this)
}

private data class KeyNode(val key: Key) : KeyedVaultNode()
private data class DoorNode(val key: Key) : KeyedVaultNode()

private data class VaultGraphExplorationState(val positions: Set<KeyedVaultNode>,
                                              val keysOwned: Set<Key>,
                                              val doorsOpened: Set<Key>)

private typealias Key = Char

fun main() {
  val input = File("src/main/kotlin/advent/year2019/day18/input.txt")
          .readText()

  val vault = KeyedVault.parse(input)

  // This takes a couple minutes (and is probably worth turning reportEvery to 100).
  // println(vault.shortestPathLength())

  println(vault.split().shortestPathLength())
}