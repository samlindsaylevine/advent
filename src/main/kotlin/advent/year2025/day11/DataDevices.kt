package advent.year2025.day11

import advent.meta.readInput
import advent.utils.loadingCache

class DataDevices(devices: List<DataDevice>) {
  constructor(input: String) : this(input.trim().lines().map(DataDevice::of))

  // A map of devices to those devices that have an output leading to that device.
  private val inputs: Map<String, List<String>> = devices.flatMap { it.to.map { output -> it.from to output } }
    .groupBy(keySelector = { it.second }, valueTransform = { it.first })

  fun countPathsTo(device: String): Long = pathCountCache[device]
  private val pathCountCache = loadingCache(::calculatePathCountTo)
  private fun calculatePathCountTo(device: String): Long {
    if (device == "you") return 1
    val inputDevices = inputs[device] ?: return 0
    return inputDevices.sumOf(::countPathsTo)
  }
}

class DataDevice(val from: String, val to: List<String>) {
  companion object {
    fun of(input: String): DataDevice {
      val from = input.substringBefore(":")
      val to = input.substringAfter(": ").split(" ")
      return DataDevice(from, to)
    }
  }
}

fun main() {
  val devices = DataDevices(readInput())

  println(devices.countPathsTo("out"))
}