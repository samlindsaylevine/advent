package advent.year2019.day14

import java.io.File

class ChemicalNanofactory private constructor(private val reactionsByProduct: Map<Chemical, Reaction>) {
    companion object {
        fun parse(input: String): ChemicalNanofactory {
            val reactions = input.lines()
                    .map { Reaction.parse(it) }
            return ChemicalNanofactory(reactions.associateBy { it.product.first })
        }
    }

    /**
     * The amount of ore it costs to make the desired amount of the desired chemical.
     */
    fun oreCost(amount: Long, chemical: Chemical) = synthesize(mapOf(chemical to amount))["ORE"] ?: 0

    /**
     * Starting with an amount of needed quantities of some chemicals, run reactions until we have as much as we
     * need of each chemical - except ORE which we are allowed to still need more of.
     *
     * @param neededQuantities Some of these may be negative if we currently have a surplus.
     */
    private tailrec fun synthesize(neededQuantities: Map<Chemical, Long>): Map<Chemical, Long> {
        if (neededQuantities.filter { it.key != "ORE" }.all { it.value <= 0 }) {
            return neededQuantities
        }

        val (chemical, amount) = neededQuantities.filter { it.key != "ORE" && it.value > 0 }.asSequence().first()

        val reaction = reactionsByProduct[chemical] ?: throw IllegalStateException("No reaction producing $chemical")

        val times = amount.ceilingDivide(reaction.product.second)

        val nextQuantities = neededQuantities.toMutableMap()

        nextQuantities[chemical] = amount - times * reaction.product.second

        reaction.reagants.forEach { (reagant, reagantAmount) ->
            nextQuantities.merge(reagant, times * reagantAmount, Long::plus)
        }

        return synthesize(nextQuantities)
    }

    /**
     * We start with an estimate of (1 trillion)/(cost to make 1 fuel), which we know is possible;
     * and then do a binary-search type approach to find the smallest number that could not be synthesized.
     */
    fun maxFuelFrom(oreAmount: Long): Long {
        val amountToMakeOneFuel = this.oreCost(1, "FUEL")
        val definitelyPossible = oreAmount / amountToMakeOneFuel
        val range = findRange(oreAmount, definitelyPossible)
        return binarySearch(oreAmount, range)
    }

    /**
     * Given the amount of ore we have, and and amount that is definitely possible, find an initial range where the
     * lower bound is possible and the upper bound is impossible. (Keep doubling the possible amount until we find
     * an impossible.)
     */
    private tailrec fun findRange(oreAmount: Long, definitePossible: Long): PossibleFuelRange {
        val double = definitePossible * 2
        return if (isPossible(oreAmount, double)) {
            findRange(oreAmount, double)
        } else {
            PossibleFuelRange(possible = definitePossible, impossible = double)
        }
    }

    /**
     * Whether we can synthesize the desired fuel amount with the provided ore amount.
     */
    private fun isPossible(oreAmount: Long, fuelAmount: Long): Boolean {
        val oreCost = this.oreCost(fuelAmount, "FUEL")
        return oreCost <= oreAmount
    }

    /**
     * Given an ore amount and a possible range, find the largest value in that range that is possible to produce,
     * by testing the midpoint and recursing.
     */
    private tailrec fun binarySearch(oreAmount: Long, range: PossibleFuelRange): Long {
        if (range.impossible == range.possible + 1) return range.possible

        val midpoint = (range.impossible + range.possible) / 2
        return if (isPossible(oreAmount, midpoint)) {
            binarySearch(oreAmount, PossibleFuelRange(midpoint, range.impossible))
        } else {
            binarySearch(oreAmount, PossibleFuelRange(range.possible, midpoint))
        }
    }
}

private data class PossibleFuelRange(val possible: Long, val impossible: Long)

fun Long.ceilingDivide(other: Long) = if (this % other == 0L) (this / other) else (this / other + 1)

private data class Reaction(val reagants: Map<Chemical, Long>,
                            val product: Pair<Chemical, Long>) {
    companion object {
        private val CHEM_REGEX = "(\\d+) (.*)".toRegex()

        fun parse(input: String): Reaction {
            val sides = input.split(" => ")
            val reagantStrings = sides[0].split(", ")
            val reagants = reagantStrings.map { parseChemical(it) }
                    .toMap()
            val product = parseChemical(sides[1])
            return Reaction(reagants, product)
        }

        private fun parseChemical(numberAndChemical: String): Pair<Chemical, Long> {
            val match = CHEM_REGEX.matchEntire(numberAndChemical)
                    ?: throw IllegalArgumentException("Can't parse $numberAndChemical")
            return match.groupValues[2] to match.groupValues[1].toLong()
        }
    }
}

private typealias Chemical = String

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day14/input.txt")
            .readText()
            .trim()

    val factory = ChemicalNanofactory.parse(input)

    println(factory.oreCost(1, "FUEL"))
    println(factory.maxFuelFrom(1_000_000_000_000))
}