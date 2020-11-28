package advent.year2019.day23

import advent.year2019.day13.parseIntcodeFromFile
import advent.year2019.day5.IntcodeComputer
import java.util.concurrent.*

class IntcodeNetwork(val program: List<Long>) {
  private var finalPacket = CompletableFuture<Packet>()
  private val computers = (0L..49L).associateWith { NetworkedComputer(it, program, this) }

  fun send(address: Long, packet: Packet) {
    println("Sending $packet to $address")
    if (address == 255L) {
      finalPacket.complete(packet)
    } else {
      val computer = computers[address]
      if (computer == null) {
        println("Dropping packet to invalid address $address")
      } else {
        computer.send(packet)
      }
    }

  }

  fun run(): Long {
    finalPacket = CompletableFuture()

    val executor = Executors.newFixedThreadPool(computers.size) { runnable ->
      Thread(runnable).apply { isDaemon = true }
    }

    computers.values.forEach {
      executor.submit(it::start)
    }

    val result = finalPacket.get()

    executor.shutdownNow()

    return result.y
  }
}

class NetworkedComputer(private val networkAddress: Long,
                        private val program: List<Long>,
                        private val network: IntcodeNetwork) {
  private val queue: BlockingQueue<Packet> = LinkedBlockingQueue()

  private var everGotNetworkAddress = false
  private var partialInput: Long? = null
  private val partialOutput = mutableListOf<Long>()

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

    val fromQueue = queue.poll() ?: return -1

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

fun main() {
  val program = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day23/input.txt")

  val network = IntcodeNetwork(program)

  val result = network.run()

  println(result)
}