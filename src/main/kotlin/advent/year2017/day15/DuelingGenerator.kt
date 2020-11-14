package advent.year2017.day15

class DuelingGenerator(val factor: Int,
                       val startingValue: Int,
                       val criterion: (Int) -> Boolean = { true }) {

    companion object {
        private val A_FACTOR = 16807
        private val B_FACTOR = 48271

        fun a(startingValue: Int) = DuelingGenerator(A_FACTOR, startingValue)
        fun b(startingValue: Int) = DuelingGenerator(B_FACTOR, startingValue)
        fun pickyA(startingValue: Int) = DuelingGenerator(A_FACTOR, startingValue, { it % 4 == 0 })
        fun pickyB(startingValue: Int) = DuelingGenerator(B_FACTOR, startingValue, { it % 8 == 0 })

        fun lowest16Bits(input: Int) = input.and(0b1111_1111_1111_1111)
    }

    val sequence = sequence {
        var current = startingValue
        while (true) {
            current = ((current * factor.toLong()) % 2147483647).toInt()
            if (criterion(current)) {
                yield(current)
            }
        }
    }

    fun matchCount(other: DuelingGenerator, numPairs: Int): Int {
        return sequence.zip(other.sequence)
                .take(numPairs)
                .filter { lowest16Bits(it.first) == lowest16Bits(it.second) }
                .count()
    }
}

fun main() {
    val startingA = 512
    val startingB = 191

    val generatorA = DuelingGenerator.a(startingA)
    val generatorB = DuelingGenerator.b(startingB)

    println(generatorA.matchCount(generatorB, 40_000_000))

    val pickyA = DuelingGenerator.pickyA(startingA)
    val pickyB = DuelingGenerator.pickyB(startingB)

    println(pickyA.matchCount(pickyB, 5_000_000))

}