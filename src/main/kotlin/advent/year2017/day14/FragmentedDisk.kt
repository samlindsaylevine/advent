package advent.year2017.day14

import advent.year2017.day10.KnotHash
import advent.year2017.day10.leftPad
import java.math.BigInteger

class FragmentedDisk(val key: String, val width: Int = 128, val height: Int = 128) {

    fun usedSquares(): Int = (0 until height).map { rowBinaryString(it).count { it == '1' } }
            .sum()

    private fun rowBinaryString(height: Int): String = BigInteger(KnotHash("$key-$height").hex, 16)
            .toString(2)
            .leftPad('0', width)

    fun regions(): Int {
        TODO()
    }
}

fun main(args: Array<String>) {
    val disk = FragmentedDisk(key = "xlqgujun")
    println(disk.usedSquares())
}