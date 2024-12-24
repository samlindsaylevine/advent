package advent.year2024.day24

import advent.meta.readInput
import advent.utils.findNumber
import advent.utils.loadingCache
import advent.utils.merge

/**
 * --- Day 24: Crossed Wires ---
 * You and The Historians arrive at the edge of a large grove somewhere in the jungle. After the last incident, the
 * Elves installed a small device that monitors the fruit. While The Historians search the grove, one of them asks if
 * you can take a look at the monitoring device; apparently, it's been malfunctioning recently.
 * The device seems to be trying to produce a number through some boolean logic gates. Each gate has two inputs and one
 * output. The gates all operate on values that are either true (1) or false (0).
 *
 * AND gates output 1 if both inputs are 1; if either input is 0, these gates output 0.
 * OR gates output 1 if one or both inputs is 1; if both inputs are 0, these gates output 0.
 * XOR gates output 1 if the inputs are different; if the inputs are the same, these gates output 0.
 *
 * Gates wait until both inputs are received before producing output; wires can carry 0, 1 or no value at all. There
 * are no loops; once a gate has determined its output, the output will not change until the whole system is reset.
 * Each wire is connected to at most one gate output, but can be connected to many gate inputs.
 * Rather than risk getting shocked while tinkering with the live system, you write down all of the gate connections
 * and initial wire values (your puzzle input) so you can consider them in relative safety. For example:
 * x00: 1
 * x01: 1
 * x02: 1
 * y00: 0
 * y01: 1
 * y02: 0
 *
 * x00 AND y00 -> z00
 * x01 XOR y01 -> z01
 * x02 OR y02 -> z02
 *
 * Because gates wait for input, some wires need to start with a value (as inputs to the entire system). The first
 * section specifies these values. For example, x00: 1 means that the wire named x00 starts with the value 1 (as if a
 * gate is already outputting that value onto that wire).
 * The second section lists all of the gates and the wires connected to them. For example, x00 AND y00 -> z00 describes
 * an instance of an AND gate which has wires x00 and y00 connected to its inputs and which will write its output to
 * wire z00.
 * In this example, simulating these gates eventually causes 0 to appear on wire z00, 0 to appear on wire z01, and 1 to
 * appear on wire z02.
 * Ultimately, the system is trying to produce a number by combining the bits on all wires starting with z. z00 is the
 * least significant bit, then z01, then z02, and so on.
 * In this example, the three output bits form the binary number 100 which is equal to the decimal number 4.
 * Here's a larger example:
 * x00: 1
 * x01: 0
 * x02: 1
 * x03: 1
 * x04: 0
 * y00: 1
 * y01: 1
 * y02: 1
 * y03: 1
 * y04: 1
 *
 * ntg XOR fgs -> mjb
 * y02 OR x01 -> tnw
 * kwq OR kpj -> z05
 * x00 OR x03 -> fst
 * tgd XOR rvg -> z01
 * vdt OR tnw -> bfw
 * bfw AND frj -> z10
 * ffh OR nrd -> bqk
 * y00 AND y03 -> djm
 * y03 OR y00 -> psh
 * bqk OR frj -> z08
 * tnw OR fst -> frj
 * gnj AND tgd -> z11
 * bfw XOR mjb -> z00
 * x03 OR x00 -> vdt
 * gnj AND wpb -> z02
 * x04 AND y00 -> kjc
 * djm OR pbm -> qhw
 * nrd AND vdt -> hwm
 * kjc AND fst -> rvg
 * y04 OR y02 -> fgs
 * y01 AND x02 -> pbm
 * ntg OR kjc -> kwq
 * psh XOR fgs -> tgd
 * qhw XOR tgd -> z09
 * pbm OR djm -> kpj
 * x03 XOR y03 -> ffh
 * x00 XOR y04 -> ntg
 * bfw OR bqk -> z06
 * nrd XOR fgs -> wpb
 * frj XOR qhw -> z04
 * bqk OR frj -> z07
 * y03 OR x01 -> nrd
 * hwm AND bqk -> z03
 * tgd XOR rvg -> z12
 * tnw OR pbm -> gnj
 *
 * After waiting for values on all wires starting with z, the wires in this system have the following values:
 * bfw: 1
 * bqk: 1
 * djm: 1
 * ffh: 0
 * fgs: 1
 * frj: 1
 * fst: 1
 * gnj: 1
 * hwm: 1
 * kjc: 0
 * kpj: 1
 * kwq: 0
 * mjb: 1
 * nrd: 1
 * ntg: 0
 * pbm: 1
 * psh: 1
 * qhw: 1
 * rvg: 0
 * tgd: 0
 * tnw: 1
 * vdt: 1
 * wpb: 0
 * z00: 0
 * z01: 0
 * z02: 0
 * z03: 1
 * z04: 0
 * z05: 1
 * z06: 1
 * z07: 1
 * z08: 1
 * z09: 1
 * z10: 1
 * z11: 0
 * z12: 0
 *
 * Combining the bits from all wires starting with z produces the binary number 0011111101000. Converting this number
 * to decimal produces 2024.
 * Simulate the system of gates and wires. What decimal number does it output on the wires starting with z?
 *
 * --- Part Two ---
 * After inspecting the monitoring device more closely, you determine that the system you're simulating is trying to
 * add two binary numbers.
 * Specifically, it is treating the bits on wires starting with x as one binary number, treating the bits on wires
 * starting with y as a second binary number, and then attempting to add those two numbers together. The output of this
 * operation is produced as a binary number on the wires starting with z. (In all three cases, wire 00 is the least
 * significant bit, then 01, then 02, and so on.)
 * The initial values for the wires in your puzzle input represent just one instance of a pair of numbers that sum to
 * the wrong value. Ultimately, any two binary numbers provided as input should be handled correctly. That is, for any
 * combination of bits on wires starting with x and wires starting with y, the sum of the two numbers those bits
 * represent should be produced as a binary number on the wires starting with z.
 * For example, if you have an addition system with four x wires, four y wires, and five z wires, you should be able to
 * supply any four-bit number on the x wires, any four-bit number on the y numbers, and eventually find the sum of
 * those two numbers as a five-bit number on the z wires. One of the many ways you could provide numbers to such a
 * system would be to pass 11 on the x wires (1011 in binary) and 13 on the y wires (1101 in binary):
 * x00: 1
 * x01: 1
 * x02: 0
 * x03: 1
 * y00: 1
 * y01: 0
 * y02: 1
 * y03: 1
 *
 * If the system were working correctly, then after all gates are finished processing, you should find 24 (11+13) on
 * the z wires as the five-bit binary number 11000:
 * z00: 0
 * z01: 0
 * z02: 0
 * z03: 1
 * z04: 1
 *
 * Unfortunately, your actual system needs to add numbers with many more bits and therefore has many more wires.
 * Based on forensic analysis of scuff marks and scratches on the device, you can tell that there are exactly four
 * pairs of gates whose output wires have been swapped. (A gate can only be in at most one such pair; no gate's output
 * was swapped multiple times.)
 * For example, the system below is supposed to find the bitwise AND of the six-bit number on x00 through x05 and the
 * six-bit number on y00 through y05 and then write the result as a six-bit number on z00 through z05:
 * x00: 0
 * x01: 1
 * x02: 0
 * x03: 1
 * x04: 0
 * x05: 1
 * y00: 0
 * y01: 0
 * y02: 1
 * y03: 1
 * y04: 0
 * y05: 1
 *
 * x00 AND y00 -> z05
 * x01 AND y01 -> z02
 * x02 AND y02 -> z01
 * x03 AND y03 -> z03
 * x04 AND y04 -> z04
 * x05 AND y05 -> z00
 *
 * However, in this example, two pairs of gates have had their output wires swapped, causing the system to produce
 * wrong answers. The first pair of gates with swapped outputs is x00 AND y00 -> z05 and x05 AND y05 -> z00; the second
 * pair of gates is x01 AND y01 -> z02 and x02 AND y02 -> z01. Correcting these two swaps results in this system that
 * works as intended for any set of initial values on wires that start with x or y:
 * x00 AND y00 -> z00
 * x01 AND y01 -> z01
 * x02 AND y02 -> z02
 * x03 AND y03 -> z03
 * x04 AND y04 -> z04
 * x05 AND y05 -> z05
 *
 * In this example, two pairs of gates have outputs that are involved in a swap. By sorting their output wires' names
 * and joining them with commas, the list of wires involved in swaps is z00,z01,z02,z05.
 * Of course, your actual system is much more complex than this, and the gates that need their outputs swapped could be
 * anywhere, not just attached to a wire starting with z. If you were to determine that you need to swap output wires
 * aaa with eee, ooo with z99, bbb with ccc, and aoc with z24, your answer would be aaa,aoc,bbb,ccc,eee,ooo,z24,z99.
 * Your system of gates and wires has four pairs of gates which need their output wires swapped - eight wires in total.
 * Determine which four pairs of gates need their outputs swapped so that your system correctly performs addition; what
 * do you get if you sort the names of the eight wires involved in a swap and then join those names with commas?
 *
 */
class MonitoringDevice(components: List<DeviceComponent>) {
    constructor(input: String) : this(input.trim()
        .lines()
        .filter { it.isNotEmpty() }
        .map(DeviceComponent::of))

    private val componentsByOutput = components.associateBy { it.output }

    private val gatesByInput by lazy {
        val gates = components.filterIsInstance<GateConnection>()
        val gatesByFirst = gates.groupBy { it.firstInput }
        val gatesBySecond = gates.groupBy { it.secondInput }
        gatesByFirst.merge(gatesBySecond) { first, second -> first + second }
    }

    private fun gatesByInput(input: String) = gatesByInput[input] ?: emptyList()

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

    private fun zWires() = componentsByOutput.keys.filter { it.startsWith("z") }

    fun zValue(): Long {
        val binary = zWires().sortedDescending().joinToString(separator = "") { valueByOutput[it].toString() }

        return binary.toLong(radix = 2)
    }

    fun examineBinaryAdder() {
        val maxNumber = zWires().max().findNumber(0)
        // There are five kinds of gate involved in this binary adder.
        // The zeroes place is quite easy, using only two of the types:
        // y00 XOR x00 -> z00
        // This is the sum, written directly into the output digit.
        // x00 AND y00 -> fgw (carry from 0)
        // This calculates a carry digit to roll forward into the calculation for the next digit.

        // Let's consider the ones place (x01 and y01).
        // We have a
        // x01 XOR y01 -> bgb (partial-sum-1)
        // This is a partial sum, just like the XOR before. Note we don't yet write it to output.
        //
        //  y01 AND x01 -> pjh (in-place carry from 1)
        // This is again a carry, but will only be used in calculations for this digit.
        //
        // fgw (carry from 0) XOR bgb (partial-sum-1) -> z01
        // This writes the output.
        //
        // fgw (carry from 0) AND bgb (partial-sum-1) -> gww (rolling-carry from 1)
        // This checks whether our previous carry causes us to rolling a carry forward.
        //
        //  pjh (in-place carry from 1) OR gww (rolling-carry from 1) -> wwp (carry from 1)
        // Then, finally, this rolls forward to the next digit. Note in particular that only wwp, this "carry from 1",
        // is then used in any further calculations.

        // So, we can audit the gates and see if they match this pattern.
        // It seems easiest to start by printing the gates, for each n, that have xn and yn as their inputs,
        // then those gates that have those gates' outputs as their inputs, and inspecting.

        for (i in 0 until maxNumber) {
            val numberString = i.toString().padStart(2, '0')
            val direct = gatesByInput("x$numberString")
            direct.sortedBy { it.operation }.forEach(::println)
            val indirect = direct.flatMap { gatesByInput(it.output) }
            indirect.sortedBy { it.operation }.forEach(::println)
            println()
        }
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
) : DeviceComponent(output) {
    override fun toString(): String {
        val (first, second) = listOf(firstInput, secondInput).sorted()
        return "$first $operation $second -> $output"
    }

}

enum class GateOperation(val operation: (Int, Int) -> Int) {
    XOR(Int::xor),
    AND(Int::and),
    OR(Int::or);

    operator fun invoke(first: Int, second: Int) = operation(first, second)
}

fun main() {
    val device = MonitoringDevice(readInput())

    println(device.zValue())

    // We're going to inspect the input ourselves and use our brain to determine the
    // wrong values. For a little help, we'll print the gates in a sorted, segmented
    // fashion. See examineBinaryAdder(), above, for more discussion of the structure.
    device.examineBinaryAdder()

    // Upon reading the output, we see:

    //    09s:
    //    qwf and cnk (the output from the XOR and the AND) are switched
    //
    //    14s:
    //    x14 AND y14 -> z14, but that is wrong; x14 AND y14 should be an in-place carry that is then ORed with the rolling carry from 13 to yield the new carry. The rolling carry from 13 is calculated by ndq AND rkm -> fgv; we have
    //            vhm OR fgv -> bbw
    //    If we switch vhm and z14, then we have
    //    x14 xor y14 -> rkm (partial sum)
    //    x14 and y14 -> vhm (in-place carry)
    //    ndq xor rkm -> z14 (output)
    //    ndq and rkm -> fgv (rolling carry)
    //    vhm OR fgv -> bbw (carry from 14)
    //    That looks good!
    //
    //    27s:
    //    kqj is the carry from 26.
    //    These are wrong:
    //    kqj XOR kqw -> mps
    //    jgq OR snv -> z27
    //    The OR should be building the next carry, and the XOR should be outputting to z27. So switch mps and z27. We can indeed see in the 28s that mps is the appropriate carry.
    //
    //    39s:
    //    These are wrong:
    //    gpm XOR trn -> msq
    //    gpm AND trn -> z39
    //    The XOR should be writing into the output, and the AND should be making an intermediate value that feeds into the OR:
    //    mgb OR msq -> cqt
    //    Switch msq and z39.

    // So our result is this:
    println(
        listOf("qwf", "cnk", "vhm", "z14", "mps", "z27", "msq", "z39")
            .sorted()
            .joinToString(separator = ",")
    )
}