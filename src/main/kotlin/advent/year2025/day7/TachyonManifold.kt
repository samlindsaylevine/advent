package advent.year2025.day7

import advent.meta.readInput

class TachyonManifold(val initialBeamLocation: Int, val splitterLocations: List<Set<Int>>) {
  companion object {
    fun of(input: String): TachyonManifold {
      val lines = input.trim().lines()
      val initialBeamLocation = lines.first().indexOf("S")
      val splitterLocations = lines.map { line ->
        line.flatMapIndexed { i, c -> if (c == '^') listOf(i) else emptyList() }.toSet()
      }
      return TachyonManifold(initialBeamLocation, splitterLocations)
    }
  }

  fun finalBeams() = splitterLocations.fold(BeamProgress(setOf(initialBeamLocation), 0), BeamProgress::advance)
}

data class BeamProgress(val beamLocations: Set<Int>, val numSplits: Int) {
  fun advance(splitters: Set<Int>): BeamProgress {
    val nextBeams = beamLocations.flatMap { if (it in splitters) listOf(it - 1, it + 1) else listOf(it) }
    val nextNumSplits = numSplits + nextBeams.size - beamLocations.size
    return BeamProgress(nextBeams.toSet(), numSplits + nextBeams.size - beamLocations.size)
  }
}

fun main() {
  val manifold = TachyonManifold.of(readInput())

  println(manifold.finalBeams().numSplits)
}