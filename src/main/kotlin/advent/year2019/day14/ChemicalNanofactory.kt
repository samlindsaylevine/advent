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
    fun oreCost(amount: Long, chemical: Chemical) = oreCost(mapOf(chemical to amount))

    /**
     * How much ore it takes to assemble the provided quantities of each chemical.
     *
     * @param neededQuantities Some of these may be negative if we currently have a surplus.
     */
    private tailrec fun oreCost(neededQuantities: Map<Chemical, Long>): Long {
        if (neededQuantities.filter { it.key != "ORE" }.all { it.value <= 0 }) {
            return neededQuantities["ORE"] ?: 0
        }

        val (chemical, amount) = neededQuantities.filter { it.key != "ORE" && it.value > 0 }.asSequence().first()

        val reaction = reactionsByProduct[chemical] ?: throw IllegalStateException("No reaction producing $chemical")

        val nextQuantities = neededQuantities.toMutableMap()

        nextQuantities[chemical] = amount - reaction.product.second

        reaction.reagants.forEach { (reagant, reagantAmount) ->
            nextQuantities.merge(reagant, reagantAmount, Long::plus)
        }

        return oreCost(nextQuantities)
    }

    fun maxFuelFrom(oreAmount: Long): Long {
        TODO()
    }
}

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
}