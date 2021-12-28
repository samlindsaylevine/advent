package advent.year2021.day24

import java.io.File

/**
 * Looking at our program, we can see that it has 14 separate blocks, each starting with a "inp w", that are mostly
 * identical. This suggests we should try to understand what the program is doing, instead of just running it.
 *
 * A few helpers exist in this class to verify that all the blocks are identical, and then to visualize where they
 * differ. That gives that the blocks are all:
 *
 * mul x 0
 * add x z
 * mod x 26
 * div z (1|1|1|26|1|1|1|26|1|26|26|26|26|26)
 * add x (11|11|15|-11|15|15|14|-7|12|-6|-10|-15|-9|0)
 * eql x w
 * eql x 0
 * mul y 0
 * add y 25
 * mul y x
 * add y 1
 * mul z y
 * mul y 0
 * add y w
 * add y (6|12|8|7|7|12|2|15|4|5|12|11|13|7)
 * mul y x
 * add z y
 *
 * Let's walk through this block and understand what it is doing, and see if we can summarize it at a higher level.
 * (Note that the code in this file only generates the above; all the rest of the solution is in this comment!)
 *
 * Each block starts with inputting a digit and storing it to W.
 *
 *   mul x 0
 *   add x z
 *   mod x 26
 *
 * Set X to the value of Z from the previous block, reduced mod 26. Note that X is not preserved across blocks.
 *
 *   div z (1|1|1|26|1|1|1|26|1|26|26|26|26|26)
 *
 * Possibly divide Z by 26, or just leave it as is.
 *
 *   add x (11|11|15|-11|15|15|14|-7|12|-6|-10|-15|-9|0)
 *
 * Add this number to X.
 *
 *   eql x w
 *   eql x 0
 *
 * If X (which is our previous Z, mod 26, plus an offset) is equal to our input digit W, set it to 0. If X is not equal
 * to our input digit W, set it to 1. X does not change after here.
 *
 *   mul y 0
 *   add y 25
 *   mul y x
 *   add y 1
 *
 * Set Y equal to 25X + 1: i.e., if the X comparison was equal, it becomes 1, if unequal, it becomes 26. Note that Y
 * is not preserved across blocks.
 *
 *   mul z y
 *
 * Multiply Z by Y - i.e., if the X comparison was equal, leave Z alone; if the X comparison was unequal, multiply it
 * by 26.
 *
 *   mul y 0
 *   add y w
 *   add y (6|12|8|7|7|12|2|15|4|5|12|11|13|7)
 *
 * Reset Y to be our input digit, plus an offset.
 *
 *   mul y x
 *   add z y
 *
 * If the X comparison was not equal, add Y to Z. (If the X comparison was equal, X is 0, and so Z is unaffected.)
 *
 *
 * Or, to summarize:
 *
 * See if Z from the previous block, reduced mod 26, and then summed with a unique offset (variable A), is equal to our
 * input digit.
 * Call this the "test".
 * Maybe divide Z by 26 (depending on the block; variable B). If the "test" was equal, then leave Z alone; if the "test"
 * was not equal, multiply Z by 26, then add our input digit to it, plus a second unique offset (variable C).
 *
 * Notes: Since variable C is always non-negative, then Z is always non-negative. Remember that our goal is for Z to be
 * 0 at the end.
 * Whenever we divide Z by 26 (i.e., variable B for the block is "true"/26), then the offset variable A is negative.
 * Whenever we do not divide Z by 26 (variable B is "false"/1), then the offset variable A is positive.
 * That's interesting. Also noteworthy is that all the positive variable A values are greater than 9. Since our input
 * digit can only be 1-9, that means that the "test" will always fail for positive A.
 *
 * That means the block boils down to:
 * if A is positive:
 *   Z <- Z * 26 + (input digit) + C
 * if A is negative or zero:
 *   Z <- Z / 26
 *   if (Z + A != (input digit)):
 *     Z <- Z * 26 + (input digit) + C
 *
 * This kind of looks like we're dealing with Z representing a set of numbers in powers of 26, i.e., base 26
 * representation. We're adding or removing digits to the base-26 number that Z is with each block. We'll only be able
 * to get Z to end up as 0 if we add and remove digits the same number of times: that means we want *every* check to
 * equal for A negative. (There are the same number of positive and non-positive As in the program, 7 of each.)
 *
 * If we think of the base-26 digits as a stack, we can consider ourselves to be adding or removing digits from the
 * stack:
 *
 * if A is positive:
 *   push (input digit + C) onto the stack
 * if A is negative or zero:
 *   pop the last digit off the stack and call it D
 *   if (D + A != (input digit)):
 *     push (input digit + C) onto the stack
 *
 * Note again that for our model number to work, we want to _never_ have that condition on line 114 push an extra thing
 * onto the stack.
 *
 * For our program, the A values are:
 * (11|11|15|-11|15|15|14|-7|12|-6|-10|-15|-9|0)
 * and the C values:
 * (6 |12|8 |7  |7 |12|2 |15|4 |5 |12 |11 |13|7)
 *
 * That means that as the first pop, we have on the stack:
 * (digit1 + 6)
 * (digit2 + 12)
 * (digit3 + 8)
 *
 * So, we pop off the final one, and want
 * digit3 + 8 - 11 = digit4
 *
 * Then, next time (when A is -7), we have on the stack
 * (digit1 + 6)
 * (digit2 + 12)
 * (digit5 + 7)
 * (digit6 + 12)
 * (digit7 + 2)
 *
 * That gives us
 * digit7 + 2 - 7 = digit8
 *
 * At the next pop, we have
 * (digit1 + 6)
 * (digit2 + 12)
 * (digit5 + 7)
 * (digit6 + 12)
 * (digit9 + 4)
 *
 * So
 * digit9 + 4 - 6 = digit10
 *
 * We then pop 4 more times in a row, giving
 *
 * digit6 + 12 - 10 = digit11
 * digit5 + 7 - 15 = digit12
 * digit2 + 12 - 9 = digit13
 * digit1 + 6 - 0 = digit14
 *
 * Bringing all those together:
 * digit3 + 8 - 11 = digit4
 * digit7 + 2 - 7 = digit8
 * digit9 + 4 - 6 = digit10
 * digit6 + 12 - 10 = digit11
 * digit5 + 7 - 15 = digit12
 * digit2 + 12 - 9 = digit13
 * digit1 + 6 - 0 = digit14
 *
 * Reducing them:
 * digit3 = digit4 + 3
 * digit7 = digit8 + 5
 * digit9 = digit10 + 2
 * digit6 + 2 = digit11
 * digit5 = digit12 + 8
 * digit2 + 3 = digit13
 * digit1 + 6 = digit14
 *
 * To find the largest possible input, we'll set the digit one the side without the plus to 9: i.e., digit3 = 9 so
 * digit4 = 6, etc. That gives the digits in order as
 * 36969794979199
 *
 * Likewise, to find the smallest possible input, we set the side with a plus to be 1: i.e., digit4 = 1 so digit3 = 4,
 * etc. That gives
 * 11419161313147
 *
 * Whew!
 *
 * Problem statement follows:
 *
 * --- Day 24: Arithmetic Logic Unit ---
 * Magic smoke starts leaking from the submarine's arithmetic logic unit (ALU). Without the ability to perform basic
 * arithmetic and logic functions, the submarine can't produce cool patterns with its Christmas lights!
 * It also can't navigate. Or run the oxygen system.
 * Don't worry, though - you probably have enough oxygen left to give you enough time to build a new ALU.
 * The ALU is a four-dimensional processing unit: it has integer variables w, x, y, and z. These variables all start
 * with the value 0. The ALU also supports six instructions:
 *
 * inp a - Read an input value and write it to variable a.
 * add a b - Add the value of a to the value of b, then store the result in variable a.
 * mul a b - Multiply the value of a by the value of b, then store the result in variable a.
 * div a b - Divide the value of a by the value of b, truncate the result to an integer, then store the result in
 * variable a. (Here, "truncate" means to round the value toward zero.)
 * mod a b - Divide the value of a by the value of b, then store the remainder in variable a. (This is also called the
 * modulo operation.)
 * eql a b - If the value of a and b are equal, then store the value 1 in variable a. Otherwise, store the value 0 in
 * variable a.
 *
 * In all of these instructions, a and b are placeholders; a will always be the variable where the result of the
 * operation is stored (one of w, x, y, or z), while b can be either a variable or a number. Numbers can be positive or
 * negative, but will always be integers.
 * The ALU has no jump instructions; in an ALU program, every instruction is run exactly once in order from top to
 * bottom. The program halts after the last instruction has finished executing.
 * (Program authors should be especially cautious; attempting to execute div with b=0 or attempting to execute mod with
 * a<0 or b<=0  will cause the program to crash and might even damage the ALU. These operations are never intended in
 * any serious ALU program.)
 * For example, here is an ALU program which takes an input number, negates it, and stores it in x:
 * inp x
 * mul x -1
 *
 * Here is an ALU program which takes two input numbers, then sets z to 1 if the second input number is three times
 * larger than the first input number, or sets z to 0 otherwise:
 * inp z
 * inp x
 * mul z 3
 * eql z x
 *
 * Here is an ALU program which takes a non-negative integer as input, converts it into binary, and stores the lowest
 * (1's) bit in z, the second-lowest (2's) bit in y, the third-lowest (4's) bit in x, and the fourth-lowest (8's) bit
 * in w:
 * inp w
 * add z w
 * mod z 2
 * div w 2
 * add y w
 * mod y 2
 * div w 2
 * add x w
 * mod x 2
 * div w 2
 * mod w 2
 *
 * Once you have built a replacement ALU, you can install it in the submarine, which will immediately resume what it
 * was doing when the ALU failed: validating the submarine's model number. To do this, the ALU will run the MOdel
 * Number Automatic Detector program (MONAD, your puzzle input).
 * Submarine model numbers are always fourteen-digit numbers consisting only of digits 1 through 9. The digit 0 cannot
 * appear in a model number.
 * When MONAD checks a hypothetical fourteen-digit model number, it uses fourteen separate inp instructions, each
 * expecting a single digit of the model number in order of most to least significant. (So, to check the model number
 * 13579246899999, you would give 1 to the first inp instruction, 3 to the second inp instruction, 5 to the third inp
 * instruction, and so on.) This means that when operating MONAD, each input instruction should only ever be given an
 * integer value of at least 1 and at most 9.
 * Then, after MONAD has finished running all of its instructions, it will indicate that the model number was valid by
 * leaving a 0 in variable z. However, if the model number was invalid, it will leave some other non-zero value in z.
 * MONAD imposes additional, mysterious restrictions on model numbers, and legend says the last copy of the MONAD
 * documentation was eaten by a tanuki. You'll need to figure out what MONAD does some other way.
 * To enable as many submarine features as possible, find the largest valid fourteen-digit model number that contains
 * no 0 digits. What is the largest model number accepted by MONAD?
 *
 * --- Part Two ---
 * As the submarine starts booting up things like the Retro Encabulator, you realize that maybe you don't need all
 * these submarine features after all.
 * What is the smallest model number accepted by MONAD?
 *
 */

/**
 * Given a list of strings, returns the longest possible prefix that they all share in common, and then
 */
private fun List<String>.commonPrefix(): Pair<String, List<String>> = (0..this.first().length).asSequence()
  .map { this.first().substring(0, it) }
  .last { prefix -> this.all { it.startsWith(prefix) } }
  .let { prefix ->
    if (this.all { it == prefix }) {
      prefix to emptyList()
    } else {
      prefix to this.map { it.substringAfter(prefix) }
    }
  }

private fun Pair<String, List<String>>.prefixVisualized() = if (this.second.isEmpty()) {
  this.first
} else {
  this.first + this.second.joinToString(prefix = "(", separator = "|", postfix = ")")
}

fun instructionBlocks(input: String): String {
  val blocks = input.split("inp w").map { it.trim() }.filter { it.isNotBlank() }
  val blockLines: List<List<String>> = blocks.map { it.split("\n") }

  val distinctSizes = blockLines.map { it.size }.toSet()
  require(distinctSizes.size == 1) { "Expected all blocks to have the same number of instructions but found $distinctSizes" }

  val blockSize = distinctSizes.first()
  val lineGroups = (0 until blockSize).map { index -> blockLines.map { block -> block[index] } }
  return lineGroups.joinToString(separator = "\n") { it.commonPrefix().prefixVisualized() }
}

fun main() {
  val program = File("src/main/kotlin/advent/year2021/day24/input.txt")
    .readText()
    .trim()

  println(instructionBlocks(program))
}