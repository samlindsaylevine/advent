package advent.year2018.day12

import java.io.File

class PottedPlants(private val potsWithPlants: Set<Int>,
                   private val generationsElapsed: Int,
                   private val rules: Map<PlantRuleCondition, Boolean>) {

    companion object {
        fun parse(input: String): PottedPlants {
            val lines = input.trim().split("\n")

            val initialState = lines[0].removePrefix("initial state: ")
                    .toCharArray()
                    .asSequence()
                    .withIndex()
                    .filter { it.value.isPlant() }
                    .map { it.index }
                    .toSet()

            val ruleLines = lines.subList(2, lines.size)
            val rules = ruleLines.map { parseRule(it) }
                    .toMap()

            return PottedPlants(initialState, 0, rules)
        }

        private fun parseRule(input: String): Pair<PlantRuleCondition, Boolean> {
            val chars = input.toCharArray()

            return Pair(PlantRuleCondition(chars[0].isPlant(),
                    chars[1].isPlant(),
                    chars[2].isPlant(),
                    chars[3].isPlant(),
                    chars[4].isPlant()),
                    chars.last().isPlant())
        }

        private fun Char.isPlant() = (this == '#')
    }

    private fun hasPlant(index: Int) = potsWithPlants.contains(index)

    private fun nextGeneration(): PottedPlants {
        val min = (potsWithPlants.min() ?: 0) - 2
        val max = (potsWithPlants.max() ?: 0) + 2

        return PottedPlants((min..max).filter(this::hasPlantInNextGeneration).toSet(),
                generationsElapsed + 1,
                rules)
    }

    private fun hasPlantInNextGeneration(index: Int): Boolean {
        val condition = PlantRuleCondition(hasPlant(index - 2),
                hasPlant(index - 1),
                hasPlant(index),
                hasPlant(index + 1),
                hasPlant(index + 2))

        return rules[condition] ?: false
    }

    fun after(generations: Int) = (1..generations).fold(this) { plants, _ -> plants.nextGeneration() }

    fun sumOfPotNumbers() = potsWithPlants.sum()
}

data class PlantRuleCondition(val twoLeft: Boolean,
                              val left: Boolean,
                              val center: Boolean,
                              val right: Boolean,
                              val twoRight: Boolean)

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day12/input.txt")
            .readText()
            .trim()

    val plants = PottedPlants.parse(input)

    println(plants.after(20).sumOfPotNumbers())
}