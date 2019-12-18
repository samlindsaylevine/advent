package advent.utils

class UnorderedPair<T>(private val a: T, private val b: T) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnorderedPair<*>

        return (elements == other.elements)
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }

    fun contains(t: T) = (a == t || b == t)
    val elements = setOf(a, b)
    operator fun component1() = a
    operator fun component2() = b
    fun <R> map(transform: (T) -> R) = UnorderedPair(transform(a), transform(b))
}

infix fun <T> T.with(other: T) = UnorderedPair(this, other)

fun <T> List<T>.allUnorderedPairs() = (0 until this.size).flatMap { i ->
    (0 until i).map { j -> UnorderedPair(this[i], this[j]) }
}