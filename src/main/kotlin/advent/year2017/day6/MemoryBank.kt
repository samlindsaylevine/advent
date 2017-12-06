package advent.year2017.day6

import java.io.File

data class MemoryBank(private val banks: List<Int>) {

    constructor(text: String) : this(text.trim().split("\\s+".toRegex()).map { it.toInt() })

    fun reallocate(): MemoryBank {
        val newBanks = banks.toMutableList()
        val max = newBanks.max()
        val mostBlocksIndex = banks.indexOfFirst { it == max }

        var toRedistribute = newBanks[mostBlocksIndex]
        var currentIndex = mostBlocksIndex
        newBanks[mostBlocksIndex] = 0

        while (toRedistribute > 0) {
            currentIndex = (currentIndex + 1) % newBanks.size
            newBanks[currentIndex] = newBanks[currentIndex] + 1
            toRedistribute--
        }

        return MemoryBank(newBanks)
    }

    fun redistributionsBeforeLoop(): Int {
        return solveForLoop().stepsBeforeLoop
    }

    fun sizeOfLoop(): Int {
        return solveForLoop().sizeOfLoop
    }


    fun solveForLoop(): LoopResult {
        val seen: MutableList<MemoryBank> = mutableListOf()
        var current = this
        var redistributions = 0

        do {
            seen.add(current)
            current = current.reallocate()
            redistributions++

        } while (!seen.contains(current))

        return LoopResult(stepsBeforeLoop = seen.size,
                sizeOfLoop = seen.size - seen.indexOf(current))
    }

    data class LoopResult(val stepsBeforeLoop: Int, val sizeOfLoop: Int)
}

fun main(args: Array<String>) {
    val input = File("src/main/kotlin/advent/year2017/day6/input.txt").readText()
    val memoryBank = MemoryBank(input)

    println(memoryBank.solveForLoop())
}