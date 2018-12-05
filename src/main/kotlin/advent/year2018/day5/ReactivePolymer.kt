package advent.year2018.day5

import java.io.File

data class ReactivePolymer(val formula: String) {

    private fun reducedOnce(): ReactivePolymer {
        val characters = formula.toCharArray()
        val reduceIndex = (0 until formula.length - 1)
                .firstOrNull { i -> characters[i].isOppositePolarity(characters[i + 1]) }

        val newFormula = if (reduceIndex == null) {
            formula
        } else {
            formula.substring(0, reduceIndex) + formula.substring(reduceIndex + 2)
        }

        return ReactivePolymer(newFormula)
    }

    private fun Char.isOppositePolarity(other: Char) = (this.toLowerCase() == other || this.toUpperCase() == other) &&
            this != other

    fun reduced(): ReactivePolymer = reduced(this)

    private tailrec fun reduced(polymer: ReactivePolymer): ReactivePolymer {
        val next = polymer.reducedOnce()
        return if (polymer == next) polymer else reduced(next)
    }

    private fun unitTypes() = formula.toLowerCase().toCharArray().toSet()

    fun without(unit: Char) = ReactivePolymer(formula.filter { it != unit && it != unit.toUpperCase() })

    fun bestImprovement() = unitTypes()
            .map { unit -> this.without(unit) }
            .map { it.reduced() }
            .minBy { it.formula.length }
            ?: ReactivePolymer("")
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2018/day5/input.txt")
            .readText()
            .trim()

    val polymer = ReactivePolymer(input)

    println(polymer.reduced().formula.length)
    println(polymer.bestImprovement().formula.length)
}