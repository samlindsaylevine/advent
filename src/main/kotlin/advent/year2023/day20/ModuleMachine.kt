package advent.year2023.day20

import java.io.File

class ModuleMachine(var modules: List<Module> = emptyList()) {
  companion object {
    fun of(input: String): ModuleMachine {
      val machine = ModuleMachine()
      val modules = input.trim().lines().map { line -> Module.of(line, machine) }
      machine.modules = modules
      return machine
    }
  }

  private val modulesByName by lazy { modules.associateBy { it.name } }
  private val pulsesSent: MutableMap<PulseType, Long> = mutableMapOf(PulseType.LOW to 0, PulseType.HIGH to 0)

  fun pressButton(times: Int) = repeat(times) { this.pressButton() }

  private fun pressButton() {
    val pendingPulses = ArrayDeque<Pulse>()
    pendingPulses.add(Pulse(from = "button", to = "broadcaster", type = PulseType.LOW))

    while (pendingPulses.isNotEmpty()) {
      val pulse = pendingPulses.removeFirst()
      pulsesSent.merge(pulse.type, 1, Long::plus)
      val target = modulesByName[pulse.to]
      if (target != null) {
        val nextPulseType = target.receive(pulse)
        if (nextPulseType != null) {
          pendingPulses.addAll(target.destinationModuleNames.map { to ->
            Pulse(from = target.name, to = to, type = nextPulseType)
          })
        }
      }
    }
  }

  fun pulseSentProduct() = pulsesSent.values.reduce(Long::times)
}

enum class PulseType { LOW, HIGH }

data class Pulse(val from: String, val to: String, val type: PulseType)

sealed class Module(val name: String, val destinationModuleNames: List<String>) {
  companion object {
    fun of(input: String, machine: ModuleMachine): Module {
      val (nameWithPrefix, destinationString) = input.split(" -> ")
      val destinationModuleNames = destinationString.split(", ")
      return when {
        nameWithPrefix.startsWith("%") -> FlipFlopModule(nameWithPrefix.drop(1), destinationModuleNames)
        nameWithPrefix.startsWith("&") -> ConjunctionModule(nameWithPrefix.drop(1), destinationModuleNames, machine)
        else -> BroadcastModule(destinationModuleNames)
      }
    }
  }

  /**
   * Returns the pulse type, if any, send to each of its destinations. May also mutate state.
   */
  abstract fun receive(incoming: Pulse): PulseType?
}

class BroadcastModule(destinationModuleNames: List<String>) : Module("broadcaster", destinationModuleNames) {
  override fun receive(incoming: Pulse) = incoming.type
}

class FlipFlopModule(name: String,
                     destinationModuleNames: List<String>,
                     var state: Boolean = false) : Module(name, destinationModuleNames) {
  override fun receive(incoming: Pulse) = when (incoming.type) {
    PulseType.HIGH -> null
    PulseType.LOW -> {
      this.state = !state
      if (state) PulseType.HIGH else PulseType.LOW
    }
  }
}

class ConjunctionModule(
        name: String,
        destinationModuleNames: List<String>,
        machine: ModuleMachine) : Module(name, destinationModuleNames) {
  private val inputModules: List<String> by lazy {
    machine.modules.filter { this.name in it.destinationModuleNames }.map { it.name }
  }
  private val mostRecentReceived: MutableMap<String, PulseType> by lazy {
    inputModules
            .associateWith { PulseType.LOW }
            .toMutableMap()
  }

  override fun receive(incoming: Pulse): PulseType {
    mostRecentReceived[incoming.from] = incoming.type
    return if (mostRecentReceived.values.all { it == PulseType.HIGH }) PulseType.LOW else PulseType.HIGH
  }
}

fun main() {
  val input = File("src/main/kotlin/advent/year2023/day20/input.txt").readText().trim()
  val machine = ModuleMachine.of(input)

  machine.pressButton(1000)
  println(machine.pulseSentProduct())
}