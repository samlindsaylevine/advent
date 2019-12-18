package advent.year2019.day18

import advent.utils.Point

class KeyedVault(val walls: Set<Point>,
                 val doors: Map<Key, Point>,
                 val keys: Map<Key, Point>) {
    companion object {
        fun parse(input: String): KeyedVault {
            TODO()
        }
    }

    fun shortestPathLength(): Int {
        TODO()
    }

    private fun nextOptions(current: VaultExplorationState): Set<VaultExplorationState> {
        TODO()
    }
}

private data class VaultExplorationState(val position: Point,
                                         val keysOwned: Set<Key>)

private typealias Key = String