package advent.year2021.day19

import advent.utils.Point3D
import advent.utils.pairs
import java.io.File


class BeaconScanners(val scanners: List<BeaconScanner>) {
  constructor(input: String) : this(input.split("\n\n").map(::BeaconScanner))

  /**
   * Finds all the beacons by reconciling the scanners against each other. The coordinates are in the first scanner's
   * coordinate system.
   */
  fun beacons(): Set<Point3D> {
    return mappings.keys.fold(emptySet()) { points, scanner ->
      points + mapPoints(mappings, scanner.points.toSet(), scanner, scanners.first())
    }
  }

  /**
   * Finds the locations of all the scanners in the first scanner's coordinate system.
   */
  private fun scannerLocations(): Set<Point3D> {
    return mappings.keys.fold(emptySet()) { points, scanner ->
      points + mapPoints(mappings, setOf(Point3D(0, 0, 0)), scanner, scanners.first())
    }
  }

  fun largestScannerDistance() = scannerLocations().toList().pairs()
    .map { (first, second) -> first.distanceFrom(second) }
    .maxOrNull()

  private fun mapPoints(
    mappings: Map<BeaconScanner, ScannerMapping>,
    points: Set<Point3D>,
    from: BeaconScanner,
    to: BeaconScanner
  ): Set<Point3D> {
    val mapping = mappings[from] ?: throw IllegalArgumentException("No mapping for scanner $from")

    val nextStep = mapping.to

    val nextPoints = points.map { mapping.rotation(it) + mapping.translationOffset }.toSet()

    return if (nextStep == to) nextPoints else mapPoints(mappings, nextPoints, nextStep, to)
  }

  /**
   * Calculates a graph of mappings from scanner coordinates system to other scanners. This directed graph is acyclic
   * and has the first scanner at the "root"; i.e., if you keep following these mappings, you will always end up at the
   * first scanner. Every scanner will appear in this graph. The first scanner also appears with its (identity)
   * mapping to itself.
   */
  private val mappings: Map<BeaconScanner, ScannerMapping> by lazy {
    calculateMappings(
      mapOf(scanners.first() to ScannerMapping(scanners.first(), scanners.first(), NO_ROTATION, Point3D(0, 0, 0))),
      scanners.drop(1)
    )
  }

  private tailrec fun calculateMappings(
    done: Map<BeaconScanner, ScannerMapping>,
    stillToDo: List<BeaconScanner>
  ): Map<BeaconScanner, ScannerMapping> {
    if (stillToDo.isEmpty()) return done

    println("Solved ${done.size}, still ${stillToDo.size} to go")

    // Try each still-to-do scanner against each of the already-done scanners and see if we can match it up.
    val next = stillToDo.asSequence().flatMap { tryNext ->
      done.keys.map { tryExisting -> tryNext.mapTo(tryExisting) }
    }.filterNotNull()
      .first()

    return calculateMappings(done + (next.from to next), stillToDo - next.from)
  }
}

/**
 * The points are as in listed in the input - in this scanner's coordinate system.
 */
class BeaconScanner(val points: List<Point3D>) {
  constructor(input: String) : this(input.lines().drop(1).map { line ->
    val (x, y, z) = line.split(",").map { it.toInt() }
    Point3D(x, y, z)
  })

  /**
   * Attempt to find a mapping from this scanner's coordinates to the other scanner's. If null, there is no overlap
   * of the necessary number of beacons.
   */
  fun mapTo(other: BeaconScanner): ScannerMapping? {
    val rotatedPoints = allOrientations.map { rotation ->
      rotation to this.points.map { rotation(it) }
    }

    return rotatedPoints.firstNotNullOfOrNull { (rotation, rotatedPoints) ->
      findOverlap(rotation, rotatedPoints, other)
    }
  }

  /**
   * Given some already rotated points, check to see if they have the necessary overlap, with any possible translation
   * offset, with the points in the other scanner's coordinate system.
   */
  private fun findOverlap(rotation: Rotation, points: List<Point3D>, other: BeaconScanner): ScannerMapping? {
    // We'll consider each pairwise option for points from our list, and their list. Then we'll see what happens if
    // we translate our whole set such that our point overlaps theirs, and see if we get enough total overlap to call
    // it a match.
    val pairs = points.asSequence().flatMap { ours ->
      other.points.map { theirs -> ours to theirs }
    }

    val offsets = pairs.map { (ours, theirs) -> theirs - ours }

    val successfulOffset = offsets
      .firstNotNullOfOrNull { offset ->
        val translatedPoints = points.map { it + offset }
        val overlap = translatedPoints.toSet().intersect(other.points.toSet())
        if (overlap.size >= 12) offset else null
      }

    return successfulOffset?.let { ScannerMapping(this, other, rotation, it) }
  }
}

/**
 * This represents a successful mapping from one BeaconScanner to another. If this scanner's points are first
 * rotation, then the translationOffset added to them, they will line up with the other scanner's points.
 */
data class ScannerMapping(
  val from: BeaconScanner,
  val to: BeaconScanner,
  val rotation: Rotation,
  val translationOffset: Point3D
)

private typealias Rotation = (Point3D) -> Point3D

// Each of these rotations is 90 degrees around the respective axis.
private val X_ROTATION: Rotation = { (x, y, z) -> Point3D(x, z, -y) }
private val Y_ROTATION: Rotation = { (x, y, z) -> Point3D(-z, y, x) }
private val Z_ROTATION: Rotation = { (x, y, z) -> Point3D(-y, x, z) }

private val NO_ROTATION: Rotation = { it }

/**
 * Returns a new rotation that is the result of applying all the rotations in order.
 */
private fun compose(vararg rotations: Rotation): Rotation =
  { point -> rotations.fold(point) { it, rotation -> rotation(it) } }


/**
 * Returns a list of 4 rotations that start with the provided one, then spun by the provided axis 0-3 times.
 */
private fun Rotation.spun(axis: Rotation) = listOf(
  this,
  compose(this, axis),
  compose(this, axis, axis),
  compose(this, axis, axis, axis)
)

val allOrientations: List<Rotation> by lazy {
  val output =
    // Positive X
    NO_ROTATION.spun(X_ROTATION) +
        // Negative X
        compose(Z_ROTATION, Z_ROTATION).spun(X_ROTATION) +
        // Positive Y
        Z_ROTATION.spun(Y_ROTATION) +
        // Negative Y
        compose(Z_ROTATION, Z_ROTATION, Z_ROTATION).spun(Y_ROTATION) +
        // Positive Z
        compose(Y_ROTATION).spun(Z_ROTATION) +
        // Negative Z
        compose(Y_ROTATION, Y_ROTATION, Y_ROTATION).spun(Z_ROTATION)

  require(output.size == 24) { "Should be 24 total orientations but were ${output.size}" }

  output
}

fun main() {
  val report = File("src/main/kotlin/advent/year2021/day19/input.txt")
    .readText()
    .trim()
    .let(::BeaconScanners)

  println(report.beacons().size)
  println(report.largestScannerDistance())
}