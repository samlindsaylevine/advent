package advent.year2018.day24

import java.io.File

class ImmuneSystemSimulation private constructor(private val immuneSystem: MutableList<ArmyGroup>,
                                                 private val infection: MutableList<ArmyGroup>) {
    companion object {
        fun parse(input: String): ImmuneSystemSimulation {
            // Relying on convention that the immune system is always listed first.
            val armies = input.split("\n\n").map { it.trim() }

            return ImmuneSystemSimulation(parseArmy(armies[0]), parseArmy(armies[1]))
        }

        private fun parseArmy(input: String) = input.lines()
                .withIndex()
                .drop(1)
                .map { ArmyGroup.parse(it.value, it.index) }
                .toMutableList()
    }

    /**
     * @return The total number of kills in this fight.
     */
    private fun fight(): Int {
        val targets = findTargets()

        var totalKills = 0

        (immuneSystem + infection).sortedByDescending { it.unit.initiative }
                .forEach { group ->
                    if (group.count > 0) {
                        val target = targets[group]
                        if (target != null) {
                            val kills = group.wouldDealDamage(target) / target.unit.hitPoints
                            target.count -= kills
                            totalKills += kills
                        }
                    }
                }

        immuneSystem.removeIf { it.count <= 0 }
        infection.removeIf { it.count <= 0 }

        return totalKills
    }

    /**
     * Returns the number of units remaining in the winning army. (Mutates the state.)
     */
    fun runEntireCombat(): CombatResult {
        while (immuneSystem.numUnits() > 0 && infection.numUnits() > 0) {
            val kills = fight()
            // In part 2 it's possible to get into a situation where neither side is killing anything. We need to
            // detect this and not loop forever.
            if (kills == 0) return CombatResult(ArmySide.DEADLOCK,
                    immuneSystem.numUnits() + infection.numUnits())
        }

        return if (immuneSystem.isEmpty()) {
            CombatResult(winner = ArmySide.INFECTION,
                    unitsLeft = infection.numUnits())
        } else {
            CombatResult(winner = ArmySide.IMMUNE_SYSTEM,
                    unitsLeft = immuneSystem.numUnits())
        }
    }

    fun boosted(boost: Int) = ImmuneSystemSimulation(this.immuneSystem.map { it.boosted(boost) }.toMutableList(),
            this.infection.map { it.boosted(0) }.toMutableList())

    fun minBoostToWin() = generateSequence(1) { it + 1 }
            .first {
                val combatResult = this.boosted(it).runEntireCombat()
                // println("$it: $combatResult")
                combatResult.winner == ArmySide.IMMUNE_SYSTEM
            }

    private fun List<ArmyGroup>.numUnits() = this.sumBy { it.count }

    private fun findTargets(): Map<ArmyGroup, ArmyGroup> = findTargets(immuneSystem, infection) +
            findTargets(infection, immuneSystem)

    private fun findTargets(attackers: List<ArmyGroup>,
                            defenders: List<ArmyGroup>): Map<ArmyGroup, ArmyGroup> {
        val sortedAttackers = attackers.sortedWith(compareBy({ -it.effectivePower() },
                { -it.unit.initiative }))
        return findTargets(sortedAttackers,
                defenders.toSet(),
                emptyMap())
    }

    private tailrec fun findTargets(unassignedAttackers: List<ArmyGroup>,
                                    unassignedDefenders: Set<ArmyGroup>,
                                    assignments: Map<ArmyGroup, ArmyGroup>): Map<ArmyGroup, ArmyGroup> {
        return if (unassignedAttackers.isEmpty()) {
            assignments
        } else {
            val nextAttacker = unassignedAttackers.first()
            val target = chooseTarget(nextAttacker, unassignedDefenders)

            if (target == null) {
                findTargets(unassignedAttackers.drop(1), unassignedDefenders, assignments)
            } else {
                findTargets(unassignedAttackers.drop(1),
                        unassignedDefenders - target,
                        assignments + (nextAttacker to target))
            }
        }
    }

    private fun chooseTarget(attacker: ArmyGroup,
                             defenders: Set<ArmyGroup>) = defenders.filter { attacker.wouldDealDamage(it) > 0 }
            .maxWithOrNull(compareBy({ attacker.wouldDealDamage(it) },
                    { it.effectivePower() },
                    { it.unit.initiative }))
}

data class CombatResult(val winner: ArmySide, val unitsLeft: Int)

enum class ArmySide { INFECTION, IMMUNE_SYSTEM, DEADLOCK }

private class ArmyGroup(var count: Int,
                        val index: Int,
                        val unit: ArmyUnit) {
    companion object {
        private val REGEX = ("(\\d+) units each with (\\d+) hit points (.*)" +
                "with an attack that does (\\d+) (.*) damage at initiative (\\d+)").toRegex()

        private val RESISTANCE_REGEX = "(.*) to (.*)".toRegex()

        fun parse(input: String, index: Int): ArmyGroup {
            val match = REGEX.matchEntire(input) ?: throw IllegalArgumentException("Unparseable army group $input")
            val count = match.groupValues[1].toInt()
            val hitPoints = match.groupValues[2].toInt()
            val resistances = parseResistanceBlock(match.groupValues[3])
            val damage = match.groupValues[4].toInt()
            val attackType = match.groupValues[5]
            val initiative = match.groupValues[6].toInt()

            return ArmyGroup(count,
                    index,
                    ArmyUnit(hitPoints,
                            damage,
                            initiative,
                            attackType,
                            resistances.weaknesses,
                            resistances.immunities))
        }

        private fun parseResistanceBlock(input: String) = when {
            input.contains('(') -> parseResistances(input.substringAfter('(')
                    .substringBefore(')'))
            else -> Resistances(emptySet(), emptySet())
        }

        /**
         * E.g. :
         * weak to cold
         * immune to bludgeoning
         * weak to radiation, bludgeoning
         * weak to fire; immune to bludgeoning
         * immune to fire; weak to bludgeoning, slashing
         *
         */
        private fun parseResistances(input: String) = if (input.contains("; ")) {
            val sections = input.split("; ")
            sections.map(::parseResistanceSection)
                    .reduce(Resistances::plus)
        } else {
            parseResistanceSection(input)
        }

        private fun parseResistanceSection(section: String): Resistances {
            val match = RESISTANCE_REGEX.matchEntire(section)
                    ?: throw IllegalArgumentException("Unparseable resistance section $section")

            val signifier = match.groupValues[1]
            val isWeak = signifier == "weak"
            val types = match.groupValues[2].split(", ").toSet()

            val weaknesses = if (isWeak) types else emptySet()
            val immunities = if (isWeak) emptySet() else types

            return Resistances(weaknesses, immunities)
        }

        private data class Resistances(val weaknesses: Set<DamageType>,
                                       val immunities: Set<DamageType>) {
            operator fun plus(other: Resistances) = Resistances(this.weaknesses + other.weaknesses,
                    this.immunities + other.immunities)
        }
    }

    fun effectivePower() = count * unit.damage

    fun wouldDealDamage(other: ArmyGroup): Int = when {
        other.unit.immunities.contains(this.unit.attackType) -> 0
        other.unit.weaknesses.contains(this.unit.attackType) -> 2 * this.effectivePower()
        else -> this.effectivePower()
    }

    fun boosted(boost: Int) = ArmyGroup(this.count,
            this.index,
            this.unit.boosted(boost))
}

private data class ArmyUnit(val hitPoints: Int,
                            val damage: Int,
                            val initiative: Int,
                            val attackType: DamageType,
                            val weaknesses: Set<DamageType>,
                            val immunities: Set<DamageType>) {
    fun boosted(boost: Int) = this.copy(damage = this.damage + boost)
}

typealias DamageType = String

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day24/input.txt")
            .readText()

    val simulation = ImmuneSystemSimulation.parse(input)

    val unitsLeft = simulation.boosted(0).runEntireCombat().unitsLeft

    println(unitsLeft)

    val minToWin = simulation.minBoostToWin()
    val unitsLeftAfterBoost = simulation.boosted(minToWin).runEntireCombat().unitsLeft

    println(unitsLeftAfterBoost)
}