import java.io.File

enum class ChronalOpCode {

    addr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] + registers[inputB]
        }
    },

    addi {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] + inputB
        }
    },

    mulr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] * registers[inputB]
        }
    },

    muli {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] * inputB
        }
    },

    banr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] and registers[inputB]
        }
    },

    bani {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] and inputB
        }
    },

    borr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] or registers[inputB]
        }
    },

    borri {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA] or inputB
        }
    },

    setr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = registers[inputA]
        }
    },

    seti {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = inputA
        }
    },

    gtir {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (inputA > registers[inputB]) 1 else 0
        }
    },

    gtri {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (registers[inputA] > inputB) 1 else 0
        }
    },

    gtrr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (registers[inputA] > registers[inputB]) 1 else 0
        }
    },

    eqir {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (inputA == registers[inputB]) 1 else 0
        }
    },

    eqri {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (registers[inputA] == inputB) 1 else 0
        }
    },

    eqrr {
        override fun execute(registers: MutableList<Int>, inputA: Int, inputB: Int, output: Int) {
            registers[output] = if (registers[inputA] == registers[inputB]) 1 else 0
        }
    };

    abstract fun execute(registers: MutableList<Int>,
                         inputA: Int,
                         inputB: Int,
                         output: Int)
}

data class SampleComputation(val beforeRegisters: List<Int>,
                             val afterRegisters: List<Int>,
                             val opCode: Int,
                             val inputA: Int,
                             val inputB: Int,
                             val output: Int) {
    companion object {

        fun parse(input: String): SampleComputation {
            val lines = input.split("\n")
            val beforeRegisters = lines[0].substringBetween('[', ']').split(", ").map { it.toInt() }
            val execution = lines[1].split(" ").map { it.toInt() }
            val afterRegisters = lines[2].substringBetween('[', ']').split(", ").map { it.toInt() }

            return SampleComputation(beforeRegisters,
                    afterRegisters,
                    execution[0],
                    execution[1],
                    execution[2],
                    execution[3])
        }

        private fun String.substringBetween(start: Char, end: Char): String {
            val startIndex = this.indexOf(start)
            val endIndex = this.indexOf(end, startIndex = startIndex)
            return this.substring(startIndex + 1, endIndex)
        }
    }

    fun behavesLike(opCode: ChronalOpCode): Boolean {
        val registers = beforeRegisters.toMutableList()
        opCode.execute(registers, inputA, inputB, output)

        return registers == afterRegisters
    }

    fun behavesLikeCount() = ChronalOpCode.values().count { this.behavesLike(it) }
}

fun main() {
    val input = File("src/main/kotlin/advent/year2018/day16/input.txt")
            .readText()


    val parts = input.split("\n\n\n\n")

    val samples = parts[0].split("\n\n").map { SampleComputation.parse(it) }
    val testProgram = parts[1]

    print(samples.count { it.behavesLikeCount() >= 3 })
}