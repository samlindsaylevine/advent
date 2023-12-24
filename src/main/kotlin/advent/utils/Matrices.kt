package advent.utils

fun List<List<Double>>.rref() = this.rref(0.0, 1.0, Double::div, Double::times, Double::minus)

fun rrefRational(matrix: List<List<Rational>>) = matrix.rref(Rational.of(0, 1), Rational.of(1, 1), Rational::div, Rational::times, Rational::minus)

fun <T : Number> List<List<T>>.rref(zero: T,
                                    one: T,
                                    div: (T, T) -> T,
                                    times: (T, T) -> T,
                                    minus: (T, T) -> T): List<List<T>> {
  val output = mutableListOf<MutableList<T>>()
  this.forEach { output.add(it.toMutableList()) }
  val width = this.first().size
  val height = this.size

  var x = 0
  var y = 0

  while (x < width && y < height) {
    // Start with the first column. If it has all entries equal to zero, move on to the next column to the right.
    while (output.all { it[x] == zero }) x++
    // Interchange rows, if necessary, to get a nonzero entry on top.
    if (output[y][x] == zero) {
      val targetInterchangeY = output.withIndex().drop(y).firstOrNull { (_, row) -> row[x] != zero }?.index
      if (targetInterchangeY != null) {
        val targetRow = output[targetInterchangeY]
        output[targetInterchangeY] = output[y]
        output[y] = targetRow
      }
    }
    // Change the top entry, if necessary, to make it a 1.
    val leadingValue = output[y][x]
    if (leadingValue != zero) {
      if (leadingValue != one) {
        (0 until width).forEach { i -> output[y][i] = div(output[y][i], leadingValue) }
      }
      // For any nonzero entry below the top one, use an elementary row operation to change it to zero.
      for (j in (y + 1) until height) {
        val entry = output[j][x]
        if (entry != zero) {
          (0 until width).forEach { i ->
            output[j][i] = minus(output[j][i], times(entry, output[y][i]))
          }
        }
      }
    }

    // Now consider the part of the matrix below the top row and to the right of the column under consideration:
    // if there are no such rows or columns, stop since the procedure is finished.
    // Otherwise, carry out the same procedure on the new matrix.
    x++
    y++
  }

  // We now have a matrix in row echelon form. We can get it to reduced row echelon form by taking every row, starting
  // from the bottom, and subtracting it from the ones above.
  (height - 1 downTo 0).forEach { j ->
    val leadingX = output[j].indexOfFirst { it != zero }
    if (leadingX > -1) {
      (0 until j).forEach { k ->
        val coefficient = output[k][leadingX]
        (0 until width).forEach { i -> output[k][i] = minus(output[k][i], times(coefficient, output[j][i])) }
      }
    }
  }

  return output
}