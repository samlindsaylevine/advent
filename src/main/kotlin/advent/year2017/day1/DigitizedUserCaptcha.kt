package advent.year2017.day1

import java.io.File

class DigitizedUserCaptcha(private val digits: String) {

    fun matchesNextSolution(): Int {
        return solution({ nextIndex(it) })
    }

    fun matchesHalfwayAroundSolution(): Int {
        return solution({ halfwayAroundIndex(it) })
    }

    /**
     * @param compareTo A function defining, when given an index, what other index to compare it to.
     */
    private fun solution(compareTo: (Int) -> Int): Int {
        return (0 until digits.length).filter { digits[it] == digits[compareTo(it)] }
                .map { digits[it].toString().toInt() }
                .sum()
    }

    private fun nextIndex(index: Int): Int = (index + 1) % digits.length
    private fun halfwayAroundIndex(index: Int): Int = (index + (digits.length / 2)) % digits.length
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day1/input.txt").readText().trim()
    val captcha = DigitizedUserCaptcha(input)
    println(captcha.matchesNextSolution())
    println(captcha.matchesHalfwayAroundSolution())
}