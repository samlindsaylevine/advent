package advent.year2023.day5

import advent.utils.compare
import advent.utils.findAllLongs
import advent.year2020.day14.substringBetween
import java.io.File

class SeedCatalog(private val seeds: List<Long>, private val maps: Map<String, SeedCatalogMap>) {
    constructor(input: String) : this(
            input.substringBetween("seeds: ", "\n\n").findAllLongs(),
            input.substringAfter("\n\n")
                    .split("\n\n")
                    .map(::SeedCatalogMap)
                    .associateBy { it.from }
    )

    private val seedRanges = seeds.chunked(2)
            .map { (first, second) -> first until (first + second) }
            .let(::LongRangeUnion)

    fun lowestLocation() = seeds.minOf { find("seed", it, "location") }
    fun lowestFromRanges() = find("seed", seedRanges, "location")
            .ranges
            .minOf { it.first }

    fun find(currentType: String, currentValue: Long, targetType: String): Long {
        if (currentType == targetType) return currentValue
        val currentMap = maps[currentType] ?: throw IllegalStateException("No map for transforming $currentType")
        val nextType = currentMap.to
        val nextValue = currentMap.transform(currentValue)
        return find(nextType, nextValue, targetType)
    }

    fun find(currentType: String, currentRanges: LongRangeUnion, targetType: String): LongRangeUnion {
        if (currentType == targetType) return currentRanges
        val currentMap = maps[currentType] ?: throw IllegalStateException("No map for transforming $currentType")
        val nextType = currentMap.to
        val nextRanges = currentRanges.ranges
                .map { currentMap.transform(it) }
                .flatMap { it.ranges }
                .let(::LongRangeUnion)
        return find(nextType, nextRanges, targetType)
    }
}

class SeedCatalogMap(val from: String, val to: String, val ranges: List<SeedCatalogRange>) {
    constructor(input: String) : this(
            input.lines().first().substringBefore("-to-"),
            input.lines().first().substringBetween("-to-", " map"),
            input.lines().drop(1).map(::SeedCatalogRange).sortedBy { it.fromStart }
    )

    fun transform(value: Long): Long = ranges.firstOrNull { value in it }
            ?.transform(value)
            ?: value

    fun transform(range: LongRange): LongRangeUnion {
        var untransformed = listOf(range)
        val output = mutableListOf<LongRange>()
        ranges.forEach { transformingRange ->
            val comparisons = untransformed.map { it compare transformingRange.asRange }
            untransformed = comparisons.flatMap { it.left }
            output.addAll(comparisons.map { it.both }
                    .filter { !it.isEmpty() }
                    .map { transformingRange.transform(it) })
        }
        output.addAll(untransformed)
        return LongRangeUnion(output)
    }
}

class SeedCatalogRange(val fromStart: Long, toStart: Long, val length: Long) {
    constructor(input: String) : this(
            input.substringBetween(" ", " ").toLong(),
            input.substringBefore(" ").toLong(),
            input.substringAfterLast(" ").toLong()
    )

    val asRange = fromStart until fromStart + length

    operator fun contains(value: Long) = value in asRange
    private val delta = toStart - fromStart
    fun transform(value: Long) = value + delta
    fun transform(range: LongRange) = (range.first + delta)..(range.last + delta)
}

class LongRangeUnion(input: List<LongRange>) {
    // Guaranteed sorted in order by starting number.
    val ranges: List<LongRange> = input.sortedBy { it.first }
}


fun main() {
    val input = File("src/main/kotlin/advent/year2023/day5/input.txt").readText().trim()
    val catalog = SeedCatalog(input)

    println(catalog.lowestLocation())
    println(catalog.lowestFromRanges())
}