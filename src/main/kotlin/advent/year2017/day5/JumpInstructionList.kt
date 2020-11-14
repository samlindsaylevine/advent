package advent.year2017.day5

import java.io.File

class JumpInstructionList(val instructions: List<Int>) {

    fun stepsToExitIncrementing(): Int = stepsToExit { n -> n + 1 }

    fun stepsToExitStranger(): Int = stepsToExit { n -> if (n >= 3) (n - 1) else (n + 1) }

    /**
     * @param offsetChange What we do to an instruction when we leave it.
     */
    private fun stepsToExit(offsetChange: (Int) -> Int): Int {
        val workingInstructions = instructions.toMutableList()
        var currentIndex = 0
        var stepsTaken = 0

        while (0 <= currentIndex && currentIndex < instructions.size) {
            val nextIndex = currentIndex + workingInstructions[currentIndex]
            workingInstructions[currentIndex] = offsetChange(workingInstructions[currentIndex])
            currentIndex = nextIndex
            stepsTaken++
        }

        return stepsTaken
    }


}

fun main() {
    val lines = File("src/main/kotlin/advent/year2017/day5/input.txt")
            .readLines()
            .filter { it.isNotEmpty() }
            .map { it.trim().toInt() }

    val instructions = JumpInstructionList(lines)

    println(instructions.stepsToExitIncrementing())
    println(instructions.stepsToExitStranger())
}