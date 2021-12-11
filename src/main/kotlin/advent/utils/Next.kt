package advent.utils

fun <T> T.next(steps: Int, advance: (T) -> T): T = nextRecursive(this, steps, advance)

private tailrec fun <T> nextRecursive(current: T, steps: Int, advance: (T) -> T): T =
  if (steps == 0) current else nextRecursive(advance(current), steps - 1, advance)