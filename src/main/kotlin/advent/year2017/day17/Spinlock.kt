package advent.year2017.day17

abstract class Spinlock(private val stepsPerInsert: Int, protected val numInsertions: Int = 2017) {

    abstract fun insert(index: Int, value: Int, currentSize: Int)

    protected fun spin() {
        var size = 1
        var currentIndex = 0

        (1..numInsertions).forEach {
            currentIndex = (currentIndex + stepsPerInsert) % size
            this.insert(currentIndex + 1, it, size)
            size++
            currentIndex++
        }
    }

}

/**
 * Retains all values in memory.
 */
class AllValuesSpinlock(stepsPerInsert: Int, numInsertions: Int = 2017) : Spinlock(stepsPerInsert, numInsertions) {

    private val mutableValues = mutableListOf(0)

    val values: List<Int>
        get() = mutableValues

    init {
        spin()
    }

    override fun insert(index: Int, value: Int, currentSize: Int) = mutableValues.add(index, value)

    private fun valueAfter(num: Int) = values[(values.indexOf(num) + 1) % values.size]

    fun valueAfterLastInsert(): Int = valueAfter(numInsertions)
}

/**
 * Only tracks where zero is and what value comes after zero.
 */
class ZeroTrackingSpinlock(stepsPerInsert: Int, numInsertions: Int) : Spinlock(stepsPerInsert, numInsertions) {

    private var indexOfZero = 0
    var numberAfterZero: Int? = null
        private set

    init {
        spin()
    }

    override fun insert(index: Int, value: Int, currentSize: Int) {
        when {
            index == (indexOfZero + 1) -> numberAfterZero = value
            index <= indexOfZero -> indexOfZero++
        }
    }
}


fun main(args: Array<String>) {
    val input = 337

    val spinlock = AllValuesSpinlock(input)
    println(spinlock.valueAfterLastInsert())

    val angrySpinlock = ZeroTrackingSpinlock(input, numInsertions = 50_000_000)
    println(angrySpinlock.numberAfterZero)
}