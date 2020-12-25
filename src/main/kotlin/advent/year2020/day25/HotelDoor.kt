package advent.year2020.day25

import advent.utils.expMod

class HotelDoor(private val doorPublicKey: Long,
                private val cardPublicKey: Long) {
  companion object {
    private const val modulus = 20201227L
    private const val keySubject = 7L
  }

  private fun transform(subjectNumber: Long, loopSize: Long) = expMod(subjectNumber, loopSize, modulus)

  val doorLoopSize: Long by lazy {
    // This is solving a discrete logarithm; in a prime modulus, there's not any efficient way to do this - in fact the
    // inability to do it in polynomial time is actually the basis for some real cryptosystems!
    // With a small modulus like 20201227, we'll brute force it.
    var currentProduct = 1L
    var stepsTaken = 0L

    while (currentProduct != doorPublicKey) {
      currentProduct = (currentProduct * keySubject) % modulus
      stepsTaken++
    }

    stepsTaken
  }

  val encryptionKey by lazy {
    transform(cardPublicKey, doorLoopSize)
  }
}

fun main() {
  val door = HotelDoor(6930903, 19716708)
  println(door.encryptionKey)
}