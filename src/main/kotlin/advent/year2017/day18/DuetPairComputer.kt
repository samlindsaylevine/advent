package advent.year2017.day18

import advent.year2017.day18.DuetPairComputer.DuetPair
import java.io.File
import java.util.*

/**
 * It's pretty brutal reimplementing this as not connected to the first part of the problem, but the instructions
 * are meaningfully different and the execution mode is very different.
 */
class DuetPairComputer private constructor(programId: Long) {

    class DuetPair {
        val first = DuetPairComputer(0)
        val second = DuetPairComputer(1)

        init {
            first.pairedComputer = second
            second.pairedComputer = first
        }

        fun execute(instructionStrings: List<String>) {
            val instructions = instructionStrings.map { DuetInstruction(it) }
            var current = first
            var other = second

            while (true) {
                val result = instructions[current.currentIndex].action(current)
                current.currentIndex += result.stepsToAdvance.toInt()

                if (current.blockedRegister != null || current.currentIndex < 0 || current.currentIndex >= instructions.size) {
                    if (other.blockedRegister != null || other.currentIndex < 0 || other.currentIndex >= instructions.size) return

                    val swap = current
                    current = other
                    other = swap
                }
            }
        }
    }

    private val registers: MutableMap<String, Long> = mutableMapOf("p" to programId)
    val messageQueue = LinkedList<Long>()
    lateinit var pairedComputer: DuetPairComputer
    private var currentIndex = 0
    var messageSentCount = 0
        private set
    private var blockedRegister: String? = null

    fun get(name: String): Long {
        return try {
            name.toLong()
        } catch (e: NumberFormatException) {
            registers.computeIfAbsent(name, { 0L })
        }
    }

    private data class DuetInstruction(val text: String) {

        companion object {
            private val SND_REGEX = "snd (\\w+)".toRegex()
            private val SET_REGEX = "set (\\w+) (-?\\w+)".toRegex()
            private val ADD_REGEX = "add (\\w+) (-?\\w+)".toRegex()
            private val MUL_REGEX = "mul (\\w+) (-?\\w+)".toRegex()
            private val MOD_REGEX = "mod (\\w+) (-?\\w+)".toRegex()
            private val RCV_REGEX = "rcv (-?\\w+)".toRegex()
            private val JGZ_REGEX = "jgz (\\w) (-?\\w+)".toRegex()
        }

        val action: (DuetPairComputer) -> InstructionResult by lazy { parse() }

        private fun parse(): (DuetPairComputer) -> InstructionResult {
            SND_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    val value = c.get(match.groupValues[1])
                    val blocked = c.pairedComputer.blockedRegister
                    if (blocked == null) {
                        c.pairedComputer.messageQueue.add(c.get(match.groupValues[1]))
                    } else {
                        c.pairedComputer.registers[blocked] = value
                        c.pairedComputer.blockedRegister = null
                    }
                    c.messageSentCount++
                    InstructionResult()
                }
            }

            SET_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.registers[match.groupValues[1]] = c.get(match.groupValues[2])
                    InstructionResult()
                }
            }

            ADD_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.registers[match.groupValues[1]] = c.get(match.groupValues[1]) + c.get(match.groupValues[2])
                    InstructionResult()
                }
            }

            MUL_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.registers[match.groupValues[1]] = c.get(match.groupValues[1]) * c.get(match.groupValues[2])
                    InstructionResult()
                }
            }

            MOD_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.registers[match.groupValues[1]] = c.get(match.groupValues[1]) % c.get(match.groupValues[2])
                    InstructionResult()
                }
            }

            RCV_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    val receivedValue = c.messageQueue.poll()
                    if (receivedValue == null) {
                        c.blockedRegister = match.groupValues[1]
                    } else {
                        c.registers[match.groupValues[1]] = receivedValue
                    }

                    InstructionResult()
                }
            }

            JGZ_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    InstructionResult(stepsToAdvance = if (c.get(match.groupValues[1]) > 0) c.get(match.groupValues[2]) else 1)
                }
            }

            throw IllegalArgumentException("Unrecognized instruction $text")
        }
    }

    private data class InstructionResult(val stepsToAdvance: Long = 1)
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day18/input.txt")
            .readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    val computers = DuetPair()
    computers.execute(input)

    println(computers.second.messageSentCount)
}