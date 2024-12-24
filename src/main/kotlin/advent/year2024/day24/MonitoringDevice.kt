package advent.year2024.day24

import advent.meta.readInput
import advent.utils.loadingCache

class MonitoringDevice(components: List<DeviceComponent>) {
    constructor(input: String) : this(input.trim()
        .lines()
        .filter { it.isNotEmpty() }
        .map(DeviceComponent::of))

    private val componentsByOutput = components.associateBy { it.output }

    private val valueByOutput = loadingCache(::calculateValue)

    private fun calculateValue(wire: String): Int {
        val component = componentsByOutput[wire] ?: throw IllegalStateException("No component $wire")
        return when (component) {
            is InitialWireValue -> component.value
            is GateConnection -> {
                val firstValue = valueByOutput[component.firstInput]
                val secondValue = valueByOutput[component.secondInput]
                component.operation(firstValue, secondValue)
            }
        }
    }

    fun zValue(): Long {
        val zWires = componentsByOutput.keys.filter { it.startsWith("z") }.sortedDescending()

        val binary = zWires.joinToString(separator = "") { valueByOutput[it].toString() }

        return binary.toLong(radix = 2)
    }
}

sealed class DeviceComponent(open val output: String) {
    companion object {
        private val initialValueRegex = "(\\w+): (\\d)".toRegex()
        private val gateRegex = "(\\w+) (\\w+) (\\w+) -> (\\w+)".toRegex()
        fun of(input: String): DeviceComponent {
            val initialValueMatch = initialValueRegex.matchEntire(input)
            if (initialValueMatch != null) {
                val (output, value) = initialValueMatch.destructured
                return InitialWireValue(output, value.toInt())
            }
            val gateMatch = gateRegex.matchEntire(input)
            if (gateMatch != null) {
                val (firstInput, operationName, secondInput, output) = gateMatch.destructured
                return GateConnection(firstInput, secondInput, GateOperation.valueOf(operationName), output)
            }
            throw IllegalArgumentException("Unparseable component $input")
        }
    }
}

data class InitialWireValue(override val output: String, val value: Int) : DeviceComponent(output)
data class GateConnection(
    val firstInput: String,
    val secondInput: String,
    val operation: GateOperation,
    override val output: String
) : DeviceComponent(output)

enum class GateOperation(val operation: (Int, Int) -> Int) {
    AND(Int::and),
    OR(Int::or),
    XOR(Int::xor);

    operator fun invoke(first: Int, second: Int) = operation(first, second)
}

fun main() {
    val device = MonitoringDevice(readInput())

    println(device.zValue())
}