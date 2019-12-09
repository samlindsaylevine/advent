package advent.year2018.day21

import advent.year2015.day24.Ticker
import advent.year2018.day19.ChronalComputer
import advent.year2018.day19.ChronalProgram
import java.io.File

// OK, time to analyze another Chronal program...
//    #ip 4

// SECTION: Bitwise AND check
//    0     seti 123 0 5        Store 123 in register 5
//    1     bani 5 456 5        Set register 5 to (123 & 456) = 72
//    2     eqri 5 72 5         Since register 5 is 72, set it to 1
//    3     addr 5 4 4          Jump to line 5
//    4     seti 0 0 4          If bani was mis-implemented, reset back to line 0 and infinite loop

// SECTION: Program start
// After this, all registers will be 0 again (except for the input, register 0).
// Register 0 is never changed and its value is only used in the test in SECTION: Check for completion.
//    5     seti 0 9 5          Set register 5 to 0

// SECTION: Main loop
// Set register 3 to (register 5 | 65536). Reset register 5 to 10828530.
//    6     bori 5 65536 3      Set register 3 to (register 5 | 65536)
//    7     seti 10828530 0 5   Set register 5 to 10828530

// SECTION: Subloop
// Set register 5 to ((((register 3 & 255) + register 5) & 167777215) * 658999) & 16777215
// If register 3 < 256, jump to SECTION: Check for completion.
// Otherwise, jump to SECTION: Find a new value for register 3.
//    8     bani 3 255 2        Set register 2 to (register 3 & 255)
//    9     addr 5 2 5          Set register 5 to (register 5 + register 2)
//    10    bani 5 16777215 5   Set register 5 to (register 5 & 16777215)
//    11    muli 5 65899 5      Set register 5 to (register 5 * 65899)
//    12    bani 5 16777215 5   Set register 5 to (register 5 & 16777215)
//    13    gtir 256 3 2        Set register 2 to 1 if (256 > register 3) else 0
//    14    addr 2 4 4          If (256 was > register 3), jump to line 16
//    15    addi 4 1 4          Jump to line 17
//    16    seti 27 4 4         Jump to line 28

// SECTION: Find a new value for register 3.
// This is a loop on register 2. Register 2 takes on all values 0, 1, 2...
// We find the smallest value X of register 2 such that (1 + X) * 256 > register 3.
// Then we set register 3 to that.
// Finally, return to SECTION: Subloop.
//
// This is equivalent to: divide register 3 by 256, discarding any remainder.
//
//    17    seti 0 4 2          Set register 2 to 0
//    18    addi 2 1 1          Set register 1 to register 2 + 1
//    19    muli 1 256 1        Multiply register 1 by 256
//    20    gtrr 1 3 1          Set register 1 to 1 if (register 1 > register 3) else 0
//    21    addr 1 4 4          If (register 1 was > register 3), jump to line 26 (by way of line 23)
//    22    addi 4 1 4          Else... Jump to line 24
//    23    seti 25 9 4         Jump to line 26

//    24    addi 2 1 2          Add 1 to register 2
//    25    seti 17 9 4         Jump to line 18

// Finish the section.
//    26    setr 2 8 3          Set register 3 to the value of register 2
//    27    seti 7 9 4          Jump to line 8

// SECTION: Check for completion
// If register 5 == register 0, end the program.
// Otherwise, return to SECTION: Main loop.
//    28    eqrr 5 0 2          Set register 2 to 1 if (register 5 = register 0)
//    29    addr 2 4 4          If (register 5 was = register 0), halt (by jumping to line 31)
//    30    seti 5 5 4          Jump to line 6

// So our program looks like this:

// Define weirdFun(x, y) = ((((x & 255) + y) & 167777215) * 658999) & 16777215

// While register 5 != register 0:
//  Set register 3 to (register 5 | 65536).
//  Set register 5 to 10828530.
//  While register 3 >= 256:
//      register 5 = weirdFun(register 3, register 5)
//      register 3 = register 3 / 256
//  register 5 = weirdFun(register 3, register 5)
//
// In short, we can consider this a loop where we repeatedly transform the value of register 5, within the
// outermost while loop, until it hits the input value of register 0.

private fun weirdFun(x: Int, y: Int) = ((((x and 255) + y) and 16777215) * 65899) and 16777215

private fun transform(input: Int): Int {
    var reg3 = input or 65536
    var reg5 = 10828530

    while (reg3 >= 256) {
        reg5 = weirdFun(reg3, reg5)
        reg3 /= 256
    }
    reg5 = weirdFun(reg3, reg5)

    return reg5
}

private fun solvePartOne(): Int {
    // In order to get the fewest number of iterations, we want to input the value that register 5 has the first time
    // we make it to SECTION: Check for completion; i.e., the first time we exit SECTION: Subloop; i.e., the first
    // result of the transform function when given an input of register 5's initial state, 0.
    return transform(0)
}

private fun solvePartTwo(): Int {
    // As we go through values of register 5, eventually we will hit a loop. The final value of register 5 before we
    // reach the loop is the one that will cause the most instructions to be executed.
    var reg5 = 0
    val visited = mutableSetOf<Int>()
    while (true) {
        visited.add(reg5)
        val next = transform(reg5)
        if (next in visited) return reg5
        reg5 = next
    }
}

fun main() {
    println(solvePartOne())
    println(solvePartTwo())
}