package advent.utils

/**
 * Use the Euclidean algorithm to calculate the multiplicative inverse of x mod m.
 */
fun multiplicativeInverse(x: Long, m: Long): Long {
  val (g, n, _) = extendedEuclideanAlgorithm(x, m)
  if (g != 1L) throw IllegalArgumentException("No multiplicative inverse, x and m are not coprime, GCD $g")
  val output = Math.floorMod(n, m)

  // Test check.
  if ((output * x) % m != 1L) throw IllegalStateException("Calculated $output as the inverse of $x mod $m but failed double-check")

  return output
}

/**
 * Returns the triple (g, x, y) such that a*x + b*y = g - the GCD of a and b.
 */
private fun extendedEuclideanAlgorithm(a: Long, b: Long): Triple<Long, Long, Long> = when (a) {
  0L -> Triple(b, 0, 1)
  else -> {
    val quotient = Math.floorDiv(b, a)
    val remainder = Math.floorMod(b, a)
    val (g, x, y) = extendedEuclideanAlgorithm(remainder, a)
    // The "back-substitution" to get the answer we want.
    Triple(g, y - quotient * x, x)
  }
}

/**
 * Calculate x^n mod m.
 */
tailrec fun expMod(x: Long, n: Long, m: Long, current: Long = 1): Long = when {
  n == 0L -> current
  n % 2 == 0L -> expMod((x * x) % m, n / 2, m, current)
  else -> expMod(x, n - 1, m, (x * current) % m)
}


/**
 * Returns the unique solution x, modulo p*q, of the two equations
 *
 * x === a mod p
 * x === b mod q
 *
 * See https://crypto.stanford.edu/pbc/notes/numbertheory/crt.html for the proof that this is the solution.
 *
 * p and q must be prime or this will not return the right solution. (This function works fine as long as p and q are
 * merely coprime, but it relies on our [multiplicativeInverse] which currently requires the modulus to be prime.)
 */
fun chineseRemainderSolution(first: ModularConstraint, second: ModularConstraint): Long {
  val (a, p) = first
  val (b, q) = second

  val qInverse = multiplicativeInverse(q, p)
  val pInverse = multiplicativeInverse(p, q)

  // This product and sum can overflow a long sometimes, so we convert everything to BigIntegers...
  val result = (a.toBigInteger() * q.toBigInteger() * qInverse.toBigInteger() +
          b.toBigInteger() * p.toBigInteger() * pInverse.toBigInteger())
  val reducedResult = result % (p.toBigInteger() * q.toBigInteger())

  return reducedResult.toLong()
}

/**
 * Represents a modular constraint equation of the form
 *
 * x === remainder mod modulus
 */
data class ModularConstraint(val remainder: Long, val modulus: Long)

/**
 * Solves the Chinese Remainder Problem for any number of constraints, by recursively solving the first pair of
 * equations, and turning it into a single equation in the modulo that is the product of the individual ones.
 *
 * All of the moduluses (moduli?) must be prime. For the purposes of this function, they only have to be pairwise
 * coprime, but we rely on [multiplicativeInverse] which currently demands primality.
 */
fun chineseRemainderSolution(constraints: List<ModularConstraint>) = constraints
  .reduce { first, second ->
    ModularConstraint(
      remainder = chineseRemainderSolution(first, second),
      modulus = StrictMath.multiplyExact(first.modulus, second.modulus)
    )
  }
  .remainder

/**
 * Returns all the factors of this number, starting with 1, up to and including itself.
 */
fun Int.factors() = (1..this).filter { this % it == 0 }