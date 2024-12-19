package advent.year2024.day19

import advent.meta.readInput
import advent.utils.loadingCache

class Onsen(val towels: Set<String>, val designs: List<String>) {
    constructor(input: String) : this(
        input.substringBefore("\n\n").trim().split(", ").toSet(),
        input.substringAfter("\n\n").trim().lines()
    )

    private val maxTowelLength: Int = towels.maxOf { it.length }

    private fun String.towelPrefixes() = (0..Math.min(maxTowelLength, this.length))
        .map { this.substring(0, it) }
        .filter { it in towels }

    private val canMakeCache = loadingCache(::canMake)
    private fun canMake(target: String): Boolean {
        if (target.isEmpty()) return true
        val towels = target.towelPrefixes()
        return towels.any {
            canMakeCache[target.drop(it.length)]
        }
    }

    fun possibleDesignCount() = designs.count { canMakeCache[it] }
}

fun main() {
    val onsen = Onsen(readInput())

    println(onsen.possibleDesignCount())
}