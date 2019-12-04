package advent.year2019.day4

class FuelDepotPassword(val number: Int) {
    private val digits = number.toString().split("").filter { it.isNotEmpty() }.map { it.toInt() }

    fun isValid() = digits.size == 6 &&
            digits.hasAdjacentDigits() &&
            digits.neverDecreases()

    fun isValidExcludingLargerGroups() = digits.size == 6 &&
            digits.hasAdjacentDigitsNotInAGroup()
            && digits.neverDecreases()

    private fun List<Int>.hasAdjacentDigits() = this.zipWithNext().any { it.first == it.second }
    private fun List<Int>.neverDecreases() = this.zipWithNext().all { it.first <= it.second }

    private fun List<Int>.hasAdjacentDigitsNotInAGroup() = (0 until this.size - 1).any { index ->
        val current = this[index]
        val next = this[index + 1]
        val previous = if (index == 0) null else this[index - 1]
        val afterNext = if (index == this.size - 2) null else this[index + 2]

        current == next && current != previous && current != afterNext
    }
}

fun main() {
    val passwords = (130254..678275).map(::FuelDepotPassword)
    println(passwords.count { it.isValid() })
    println(passwords.count { it.isValidExcludingLargerGroups() })
}