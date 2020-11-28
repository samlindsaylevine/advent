package advent.year2019.day23

import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import java.util.concurrent.*

class IntcodeNetwork(val program: List<Long>) {
  private val computers = (0L..49L).associateWith { NetworkedComputer(it, program, this) }
  private val nat = NotAlwaysTransferring(this)

  fun send(address: Long, packet: Packet) {
    // println("Sending $packet to $address")
    if (address == 255L) {
      nat.send(packet)
    } else {
      val computer = computers[address]
      if (computer == null) {
        println("Dropping packet to invalid address $address")
      } else {
        computer.send(packet)
      }
    }

  }

  fun isIdle() = computers.values.all { it.isIdle() }

  fun run(until: NetworkMode): Long {
    val executor = Executors.newFixedThreadPool(computers.size + 1) { runnable ->
      Thread(runnable).apply { isDaemon = true }
    }

    executor.submit(nat::start)

    computers.values.forEach {
      executor.submit(it::start)
    }

    val resultFuture = when (until) {
      NetworkMode.FIRST_PACKET_TO_NAT -> nat.firstReceived
      NetworkMode.FIRST_REPEATED_PACKET_FROM_NAT -> nat.firstSentTwice
    }
    val result = resultFuture.get()

    executor.shutdownNow()

    return result.y
  }
}

class NetworkedComputer(private val networkAddress: Long,
                        private val program: List<Long>,
                        private val network: IntcodeNetwork) {
  val queue: BlockingQueue<Packet> = LinkedBlockingQueue()

  private var everGotNetworkAddress = false
  private var partialInput: Long? = null
  private val partialOutput = mutableListOf<Long>()

  @Volatile
  var seekingInput: Boolean = false

  fun isIdle() = queue.isEmpty() && seekingInput

  fun send(packet: Packet) = queue.put(packet)

  fun start() {
    val computer = IntcodeComputer()

    computer.execute(program, this::getInput, this::sendOutput)
  }

  private fun getInput(): Long {
    if (!everGotNetworkAddress) {
      everGotNetworkAddress = true
      return networkAddress
    }

    val existing = partialInput
    if (existing != null) {
      partialInput = null
      return existing
    }

    val fromQueue = queue.poll()

    if (fromQueue == null) {
      seekingInput = true
      return -1
    }

    seekingInput = false
    partialInput = fromQueue.y
    return fromQueue.x
  }

  private fun sendOutput(output: Long) {
    if (partialOutput.size == 2) {
      network.send(partialOutput[0], Packet(partialOutput[1], output))
      partialOutput.clear()
    } else {
      partialOutput.add(output)
    }
  }
}


data class Packet(val x: Long, val y: Long)

class NotAlwaysTransferring(val network: IntcodeNetwork) {
  val firstReceived = CompletableFuture<Packet>()

  private var lastReceived: Packet? = null
  private var lastSent: Packet? = null

  val firstSentTwice = CompletableFuture<Packet>()

  private val queue: BlockingQueue<Packet> = LinkedBlockingQueue()

  fun send(packet: Packet) = queue.put(packet)

  fun start() {
    // println("NAT starting")
    while (true) {
      val packet = queue.poll()
      if (packet != null) {
        // println("NAT received $packet")
        if (!firstReceived.isDone) firstReceived.complete(packet)
        lastReceived = packet
      } else if (network.isIdle() && queue.isEmpty()) {
        val toSend = lastReceived
        if (toSend == null) {
          // println("Network is idle but NAT never got a packet")
        } else {
          if (lastSent == toSend) firstSentTwice.complete(toSend)
          lastSent = toSend
          println("NAT sending $toSend")
          network.send(0, toSend)
        }
      }
    }
  }
}

enum class NetworkMode {
  FIRST_PACKET_TO_NAT,
  FIRST_REPEATED_PACKET_FROM_NAT
}

fun main() {
  val program = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day23/input.txt")

  val part1 = IntcodeNetwork(program).run(NetworkMode.FIRST_PACKET_TO_NAT)
  println("Part 1: $part1")

  val part2 = IntcodeNetwork(program).run(NetworkMode.FIRST_REPEATED_PACKET_FROM_NAT)
  println("Part 2: $part2")
}