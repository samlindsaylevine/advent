package advent.year2019.day7

import advent.year2019.day5.IntcodeComputer
import com.google.common.collect.Collections2
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class AmplifierControls(private val numAmplifiers: Int = 5,
                        private val program: List<Int>) {

    fun optimalSettings() = optimize(0 until numAmplifiers, ::evaluate)

    fun optimalLoopedSettings() = optimize(5 until numAmplifiers + 5, ::evaluateLooped)

    private fun optimize(settingValues: IntRange,
                         evaluator: (List<Int>) -> PhaseSettings) =
            Collections2.permutations(settingValues.toList())
                    .asSequence()
                    .map { evaluator(it) }
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

    /**
     * We're going to get a bit overkill on this and actually directly simulate the problem as closely as possible
     * by running all the computers asynchronously, feeding each other data on BlockingQueues.
     */
    private fun evaluateLooped(settings: List<Int>): PhaseSettings {

        val computers = List(numAmplifiers) { IntcodeComputer() }

        // The Nth queue is the input to the Nth computer, and the output of the N-1th computer. (The first queue,
        // at index 0, is naturally the output of the final computer.)
        val queues = List(numAmplifiers) { LinkedBlockingQueue<Int>() }

        var lastThrusterOutput: Int? = null

        // Ensure that we have enough threads for our parallelization.
        val executor = Executors.newFixedThreadPool(settings.size)

        val futures = computers.dropLast(1)
                .withIndex()
                .map {
                    executor.submit {
                        val inputQueue = queues[it.index]
                        val outputQueue = queues[it.index + 1]
                        val computer = it.value
                        computer.execute(program,
                                settings[it.index] andThen { inputQueue.take() },
                                { value -> outputQueue.put(value) })
                    }
                }

        // Special behavior for the last computer because it a) loops around and writes to the first queue and b) we
        // capture its output as our thruster output.
        val lastFuture = executor.submit {
            computers.last().execute(program,
                    settings.last() andThen { queues.last().take() },
                    { value -> lastThrusterOutput = value; queues.first().put(value) })
        }

        queues.first().put(0)

        (futures + lastFuture).forEach { it.get() }

        // Arguably this should be happening in a finally block so as not to leak threads.
        executor.shutdown()

        return PhaseSettings(settings,
                lastThrusterOutput ?: throw IllegalStateException("Never got any output"))
    }

    private fun runAmplifier(computer: IntcodeComputer,
                             phaseSetting: Int,
                             input: Int): Int {
        val result = computer.execute(program, phaseSetting andThen { input })
        return result.output.first()
    }
}

typealias Supplier<T> = () -> T

private infix fun <T> T.andThen(supplier: Supplier<T>): Supplier<T> {
    var returnedFirst = false
    return {
        if (returnedFirst) supplier() else {
            returnedFirst = true; this
        }
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

    val controls = AmplifierControls(program = input)

    println(controls.optimalSettings().thrusterSignal)
    println(controls.optimalLoopedSettings().thrusterSignal)
}