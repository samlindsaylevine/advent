package advent.year2017.day17

class Spinlock(stepsPerInsert: Int, val numInsertions: Int = 2017) {

    val values: List<Int> by lazy {
        var current = mutableListOf(0)
        var currentIndex = 0

        (1..numInsertions).forEach {
            currentIndex = (currentIndex + stepsPerInsert) % current.size
            current.add(currentIndex + 1, it)
            currentIndex++
        }

        current
    }

    fun valueAfter(num: Int) = values[(values.indexOf(num) + 1) % values.size]

    fun valueAfterLastInsert(): Int = valueAfter(numInsertions)
}

fun main(args: Array<String>) {
    val input = 337

    val spinlock = Spinlock(input)
    println(spinlock.valueAfterLastInsert())

    val angrySpinlock = Spinlock(input, numInsertions = 50_000_000)
    println(angrySpinlock.valueAfter(0))
}