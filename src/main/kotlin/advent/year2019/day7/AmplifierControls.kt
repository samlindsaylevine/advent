package advent.year2019.day7

import advent.year2019.day5.IntcodeComputer
import com.google.common.collect.Collections2
import java.io.File

class AmplifierControls(private val numAmplifiers: Int = 5,
                        private val program: List<Int>) {

    fun optimalSettings(): PhaseSettings = Collections2.permutations((0 until numAmplifiers).toList())
            .asSequence()
            .map { this.evaluate(it) }
            .maxBy { it.thrusterSignal }
            ?: PhaseSettings(emptyList(), 0)

    private fun evaluate(settings: List<Int>): PhaseSettings {
        val computers = List(numAmplifiers) { IntcodeComputer() }

        val output = computers.withIndex()
                .map { it.value to settings[it.index] }
                .fold(0, { previousOutput, computerAndPhase ->
                    runAmplifier(computerAndPhase.first, computerAndPhase.second, previousOutput)
                })

        return PhaseSettings(settings, output)
    }

    private fun runAmplifier(computer: IntcodeComputer,
                             phaseSetting: Int,
                             input: Int): Int {
        val result = computer.execute(program, listOf(phaseSetting, input).asSupplier())
        return result.output.first()
    }

    private fun <T> Iterable<T>.asSupplier(): () -> T {
        val iterator = this.iterator()

        return { iterator.next() }
    }
}

data class PhaseSettings(val settings: List<Int>,
                         val thrusterSignal: Int)

fun main() {
    val input = File("src/main/kotlin/advent/year2019/day7/input.txt")
            .readText()
            .trim()
            .split(",")
            .map { it.toInt() }

    val optimal = AmplifierControls(program = input).optimalSettings()

    println(optimal.thrusterSignal)
}