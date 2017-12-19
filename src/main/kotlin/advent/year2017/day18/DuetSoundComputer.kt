package advent.year2017.day18

import java.io.File

class DuetSoundComputer {

    private var lastPlayedSound: Long? = null
    private val registers: MutableMap<String, Long> = mutableMapOf()

    fun get(name: String): Long {
        return try {
            name.toLong()
        } catch (e: NumberFormatException) {
            registers.computeIfAbsent(name, { 0L })
        }
    }

    fun executeUntilRecover(instructionStrings: List<String>): Long? {
        val instructions = instructionStrings.map { DuetInstruction(it) }
        var index = 0

        while (index >= 0 && index < instructions.size) {
            val result = instructions[index].action(this)
            result.recoveredSound?.let { return it }
            index += result.stepsToAdvance.toInt()
        }

        return null
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

        val action: (DuetSoundComputer) -> InstructionResult by lazy { parse() }

        private fun parse(): (DuetSoundComputer) -> InstructionResult {
            SND_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.lastPlayedSound = c.get(match.groupValues[1])
                    InstructionResult()
                }
            }

            SET_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.registers.set(match.groupValues[1], c.get(match.groupValues[2]))
                    InstructionResult()
                }
            }

            ADD_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.registers.set(match.groupValues[1], c.get(match.groupValues[1]) + c.get(match.groupValues[2]))
                    InstructionResult()
                }
            }

            MUL_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.registers.set(match.groupValues[1], c.get(match.groupValues[1]) * c.get(match.groupValues[2]))
                    InstructionResult()
                }
            }

            MOD_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    c.registers.set(match.groupValues[1], c.get(match.groupValues[1]) % c.get(match.groupValues[2]))
                    InstructionResult()
                }
            }

            RCV_REGEX.matchEntire(text)?.let { match ->
                return { c ->
                    InstructionResult(recoveredSound = if (c.get(match.groupValues[1]) != 0L) c.lastPlayedSound else null)
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

    private data class InstructionResult(val stepsToAdvance: Long = 1, val recoveredSound: Long? = null)
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day18/input.txt")
            .readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    val computer = DuetSoundComputer()

    println(computer.executeUntilRecover(input))
}