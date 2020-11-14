package advent.year2018.day9

import java.util.*

class MarbleGame(private val numElves: Int, private val lastMarble: Int) {

    fun highScore(): Long {
        val ring = MarbleRing()

        val elfScores = mutableMapOf<Int, Long>()

        for (i in 1..lastMarble) {
            val pointsScored = takeStep(ring, i)
            elfScores.merge(i % numElves, pointsScored.toLong()) { a, b -> a + b }
        }

        return elfScores.values.maxOrNull() ?: 0
    }

    private fun takeStep(ring: MarbleRing, marbleNumber: Int): Int {
        return if (marbleNumber % 23 == 0) {
            val removed = ring.remove()
            marbleNumber + removed
        } else {
            ring.insert(marbleNumber)
            0
        }
    }

}

class MarbleRing(private val marbles: LinkedList<Int> = LinkedList(listOf(0))) {

    fun insert(marbleNumber: Int) {
        marbles.rotate(2)
        marbles.addFirst(marbleNumber)
    }

    fun remove(): Int {
        marbles.rotate(-7)
        return marbles.remove()
    }

    /**
     * Started out using an array list -- too slow for part 2. Instead using the built in doubly-linked
     * list and rotating it and always inserting & removing at the head.
     */
    private fun <T> LinkedList<T>.rotate(i: Int) {
        if (i < 0) {
            repeat(-i) { this.addFirst(this.removeLast()) }
        } else if (i > 0) {
            repeat(i) { this.add(this.remove()) }
        }
    }
}

fun main() {
    val game = MarbleGame(numElves = 479, lastMarble = 71035)
    println(game.highScore())

    val bigGame = MarbleGame(numElves = 479, lastMarble = 71035 * 100)
    println(bigGame.highScore())
}