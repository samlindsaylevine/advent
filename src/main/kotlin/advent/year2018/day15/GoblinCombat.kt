package advent.year2018.day15

import advent.utils.ShortestPathFinder
import java.io.File

class GoblinCombat private constructor(val height: Int,
                                       val width: Int,
                                       var combatants: MutableSet<Combatant>,
                                       private val walls: Set<Position>) {

    private val pathFinder = ShortestPathFinder()

    companion object {
        fun parse(input: String, elfAttackPower: Int = 3): GoblinCombat {
            val lines = input.split("\n")
            val height = lines.size
            val width = lines.map { it.length }.max() ?: 0
            val charsByPosition = lines.withIndex()
                    .flatMap { it.value.toCharArray().mapIndexed { x, c -> Position(x, it.index) to c } }
                    .toMap()
            val goblins = charsByPosition.entries
                    .filter { it.value == 'G' }
                    .map { Goblin(it.key) }
                    .toSet()
            val elves = charsByPosition.entries
                    .filter { it.value == 'E' }
                    .map { Elf(it.key, elfAttackPower) }
                    .toSet()
            val combatants = (goblins + elves).toMutableSet()
            val walls = charsByPosition.entries
                    .filter { it.value == '#' }
                    .map { it.key }
                    .toSet()
            return GoblinCombat(height, width, combatants, walls)
        }

        fun outcomeAtMinElfAttackPowerToWin(input: String): Outcome = generateSequence(4, { it + 1 })
                .map {
                    println("Attempting attack power $it")
                    GoblinCombat.parse(input, it).battle()
                }
                .filter { it.numberOfDeadElves == 0 }
                .first()
    }

    private var elapsedRounds = 0

    fun advance(): Outcome? {
        val turnOrder = combatants.sortedBy { it.position }

        for (combatant in turnOrder) {
            if (!combatant.isAlive()) continue

            val targets = findTargets(combatant)

            if (targets.isEmpty()) {
                return Outcome(elapsedRounds,
                        combatants.filter { it.isAlive() }.sumBy { it.hitPoints },
                        combatants.count { it is Elf && !it.isAlive() })
            }


            if (!targets.flatMap { it.position.adjacent() }.contains(combatant.position)) {
                val adjacentToTargets = targets.flatMap { it.position.adjacent() }
                        .filter { isOpen(it) || it == combatant.position }
                combatant.position = nextMove(combatant.position, adjacentToTargets)
            }

            val targetToAttack = targets.filter { it.position.adjacent().contains(combatant.position) }
                    .allMinBy { it.hitPoints }
                    .minBy { it.position }

            if (targetToAttack != null) {
                targetToAttack.hitPoints -= combatant.attackPower
            }
        }

        elapsedRounds++
        println("End round $elapsedRounds")
        return null
    }

    tailrec fun battle(): Outcome = this.advance() ?: this.battle()

    override fun toString() = (0 until height).joinToString(separator = "\n") { y ->
        (0 until width).map { x -> this.charAt(Position(x, y)) }.joinToString(separator = "")
    }

    private fun charAt(position: Position) = when {
        walls.contains(position) -> '#'
        combatants.any { it.position == position && it.isAlive() && it is Goblin } -> 'G'
        combatants.any { it.position == position && it.isAlive() && it is Elf } -> 'E'
        else -> '.'
    }

    private fun isOpen(position: Position) = position.x >= 0 &&
            position.x < this.width &&
            position.y >= 0 &&
            position.y < this.height &&
            this.charAt(position) == '.'

    private fun findTargets(combatant: Combatant) = combatants.filter { it.isAlive() && combatant.isEnemy(it) }

    private fun nextMove(originalPosition: Position, targetSquares: Collection<Position>): Position {
        val paths = targetSquares.flatMap { target ->
            pathFinder.find(
                    originalPosition,
                    target,
                    { it.adjacent().filter(this::isOpen).toSet() },
                    // We don't care about the paths' middle elements, just their first element and their last.
                    collapseKey = { Pair(it.first(), it.last()) })
        }

        if (paths.isEmpty()) {
            // No target is reachable, do not move.
            return originalPosition
        }

        val shortestPaths = paths.allMinBy { it.steps.size }
        val pathsToFirstTarget = shortestPaths.allMinBy { it.steps.last() }

        return pathsToFirstTarget.map { it.steps.first() }.min()
                ?: throw IllegalStateException("Shouldn't have 0 paths")

    }

    data class Outcome(val turnsElapsed: Int,
                       val hpRemaining: Int,
                       val numberOfDeadElves: Int) {
        val value = turnsElapsed * hpRemaining
    }
}

data class Position(val x: Int, val y: Int) : Comparable<Position> {
    /**
     * This is "reading order", i.e.., lower Y first, then lower X first for tiebreakers.
     */
    override fun compareTo(other: Position) = if (this.y.compareTo(other.y) != 0) {
        this.y.compareTo(other.y)
    } else {
        this.x.compareTo(other.x)
    }

    fun adjacent() = listOf(Position(this.x - 1, this.y),
            Position(this.x, this.y - 1),
            Position(this.x + 1, this.y),
            Position(this.x, this.y + 1))
}

sealed class Combatant(var position: Position,
                       val attackPower: Int,
                       var hitPoints: Int = 200) {
    abstract fun isEnemy(other: Combatant): Boolean
    fun isAlive() = hitPoints > 0
}

class Elf(position: Position, attackPower: Int) : Combatant(position, attackPower) {
    override fun isEnemy(other: Combatant) = other is Goblin
}

class Goblin(position: Position) : Combatant(position, attackPower = 3) {
    override fun isEnemy(other: Combatant) = other is Elf
}

private inline fun <T, R : Comparable<R>> Iterable<T>.allMinBy(selector: (T) -> R): Set<T> {
    return this.fold(emptySet(), { acc, element ->
        when {
            acc.isEmpty() || acc.all { selector(element) < selector(it) } -> setOf(element)
            acc.all { selector(element) == selector(it) } -> acc + element
            else -> acc
        }
    })
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day15/input.txt")
            .readText()

    val combat = GoblinCombat.parse(input)
    val outcome = combat.battle()
    println("Outcome is ${outcome.value}")

    val minPowerOutcome = GoblinCombat.outcomeAtMinElfAttackPowerToWin(input)
    println("Outcome at min power to win is ${minPowerOutcome.value}")
}