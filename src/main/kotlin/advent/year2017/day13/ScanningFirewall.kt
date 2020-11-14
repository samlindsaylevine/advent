package advent.year2017.day13

import java.io.File

class ScanningFirewall(lines: Sequence<String>) {

    val scanners = lines.map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { SecurityScanner.fromText(it) }
            .toList()

    constructor(text: String) : this(text
            .split("\n")
            .asSequence())

    fun severityOfTripStartingOn(time: Int): Int = tripStartingOn(time).severity

    private fun tripStartingOn(time: Int): Trip {
        val catches = scanners.filter { it.positionOn(time + it.depth) == 0 }
                .map { it.depth * it.range }
                .toList()

        return Trip(catches.sum(), catches.isNotEmpty())
    }

    fun earliestSafeTrip(): Int = generateSequence(0, { it + 1 })
            .first { !tripStartingOn(it).wasEverCaught }

    data class SecurityScanner(val depth: Int, val range: Int) {
        companion object {
            private val REGEX = "(\\d+): (\\d+)".toRegex()

            fun fromText(text: String): SecurityScanner {
                val match = REGEX.matchEntire(text) ?: throw IllegalArgumentException("Illegal scanner format: $text")
                return SecurityScanner(match.groupValues[1].toInt(), match.groupValues[2].toInt())
            }
        }

        /**
         * 0 = top, 1 = 1 below top, etc.
         */
        fun positionOn(time: Int): Int {
            if (range == 1) return 0

            // At time t = 0, a scanner is at the top. Its full cycle time is (2 * range) - 2 - in a cycle it visits
            // each position twice except the top and the bottom.
            val timeWithinCycle = time % (2 * range - 2)
            return if (timeWithinCycle < range) timeWithinCycle else
                (2 * range - timeWithinCycle - 2)
        }
    }

    data class Trip(val severity: Int, val wasEverCaught: Boolean)


}

fun main() {
    val firewall = File("src/main/kotlin/advent/year2017/day13/input.txt")
            .useLines { ScanningFirewall(it) }

    println(firewall.severityOfTripStartingOn(0))
    println(firewall.earliestSafeTrip())
}