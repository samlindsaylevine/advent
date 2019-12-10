package advent.utils

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(this.x + other.x, this.y + other.y)
    operator fun minus(other: Point) = Point(this.x - other.x, this.y - other.y)
    operator fun Int.times(point: Point) = Point(this * point.x, this * point.y)
    operator fun div(divisor: Int) = Point(this.x / divisor, this.y / divisor)

    /**
     * Manhattan distance.
     */
    fun distanceFrom(other: Point) = Math.abs(this.x - other.x) + Math.abs(this.y - other.y)
}