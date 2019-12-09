package advent.year2019.day9

import advent.year2019.day5.IntcodeComputer
import java.io.File

fun main() {
    val program = File("src/main/kotlin/advent/year2019/day9/input.txt")
            .readText()
            .trim()
            .split(",")
            .map { it.toLong() }

    val result = IntcodeComputer().execute(program, input = { 1 })
    println(result.output)

    val secondResult = IntcodeComputer().execute(program, input = { 2 })
    println(secondResult.output)
}