package advent.utils

enum class Direction(val x: Int, val y: Int) {
    N(0, 1),
    E(1, 0),
    S(0, -1),
    W(-1, 0);

    fun toPoint() = Point(x, y)

    fun right() = when (this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }

    fun left() = when (this) {
        N -> W
        W -> S
        S -> E
        E -> N
    }
}