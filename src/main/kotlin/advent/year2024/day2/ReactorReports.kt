package advent.year2024.day2

import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign

class ReactorReports(private val reports: List<ReactorReport>) {
    constructor(input: String) : this(input.trim().lines().map(::ReactorReport))

    fun countSafe(allowOneBadLevel: Boolean = false) = reports.count { it.isSafe(allowOneBadLevel = allowOneBadLevel) }
}

class ReactorReport(val levels: List<Int>) {
    constructor(input: String) : this(input.split(" ").map { it.toInt() })

    private fun withOmissions(): List<ReactorReport> {
        return levels.indices.map { i ->
            (levels.take(i) + levels.takeLast(levels.size - i - 1)).let(::ReactorReport)
        }
    }

    fun isSafe(allowOneBadLevel: Boolean = false): Boolean {
        if (allowOneBadLevel) {
            return withOmissions().any { it.isSafe() }
        }

        val diffs = levels.zipWithNext().map { it.first - it.second }
        val distinctSigns = diffs.map { it.sign }.toSet()

        return distinctSigns.size == 1 && diffs.all { it.absoluteValue in (1..3) }
    }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2024/day2/input.txt").readText().trim()
    val reactorReports = ReactorReports(input)

    println(reactorReports.countSafe())
    println(reactorReports.countSafe(allowOneBadLevel = true))
}
