package advent.year2023.day5

import advent.utils.findAllLongs
import advent.year2020.day14.substringBetween
import java.io.File

class SeedCatalog(val seeds: List<Long>, val maps: Map<String, SeedCatalogMap>) {
    constructor(input: String) : this(
            input.substringBetween("seeds: ", "\n\n").findAllLongs(),
            input.substringAfter("\n\n")
                    .split("\n\n")
                    .map(::SeedCatalogMap)
                    .associateBy { it.from }
    )

    fun lowestLocation() = seeds.minOf { find("seed", it, "location") }

    fun find(currentType: String, currentValue: Long, targetType: String): Long {
        if (currentType == targetType) return currentValue
        val currentMap = maps[currentType] ?: throw IllegalStateException("No map for transforming $currentType")
        val nextType = currentMap.to
        val nextValue = currentMap.transform(currentValue)
        return find(nextType, nextValue, targetType)
    }
}

class SeedCatalogMap(val from: String, val to: String, val ranges: List<SeedCatalogRange>) {
    constructor(input: String) : this(
            input.lines().first().substringBefore("-to-"),
            input.lines().first().substringBetween("-to-", " map"),
            input.lines().drop(1).map(::SeedCatalogRange)
    )

    fun transform(value: Long): Long = ranges.firstOrNull { value in it }
            ?.transform(value)
            ?: value
}

class SeedCatalogRange(val fromStart: Long, val toStart: Long, val length: Long) {
    constructor(input: String) : this(
            input.substringBetween(" ", " ").toLong(),
            input.substringBefore(" ").toLong(),
            input.substringAfterLast(" ").toLong()
    )

    operator fun contains(value: Long) = value in (fromStart until fromStart + length)
    fun transform(value: Long) = value + toStart - fromStart
}

fun main() {
    val input = File("src/main/kotlin/advent/year2023/day5/input.txt").readText().trim()
    val catalog = SeedCatalog(input)

    println(catalog.lowestLocation())
}