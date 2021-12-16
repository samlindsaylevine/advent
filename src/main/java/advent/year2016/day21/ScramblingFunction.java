package advent.year2016.day21;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Chars;

import advent.utils.CollectorUtils;

/**
 * --- Day 21: Scrambled Letters and Hash ---
 * The computer system you're breaking into uses a weird scrambling function to store its passwords. It shouldn't be
 * much trouble to create your own scrambled password so you can add it to the system; you just have to implement the
 * scrambler.
 * The scrambling function is a series of operations (the exact list is provided in your puzzle input). Starting with
 * the password to be scrambled, apply each operation in succession to the string. The individual operations behave as
 * follows:
 * 
 * swap position X with position Y means that the letters at indexes X and Y (counting from 0) should be swapped.
 * swap letter X with letter Y means that the letters X and Y should be swapped (regardless of where they appear in the
 * string).
 * rotate left/right X steps means that the whole string should be rotated; for example, one right rotation would turn
 * abcd into dabc.
 * rotate based on position of letter X means that the whole string should be rotated to the right based on the index
 * of letter X (counting from 0) as determined before this instruction does any rotations.  Once the index is
 * determined, rotate the string to the right one time, plus a number of times equal to that index, plus one additional
 * time if the index was at least 4.
 * reverse positions X through Y means that the span of letters at indexes X through Y (including the letters at X and
 * Y) should be reversed in order.
 * move position X to position Y means that the letter which is at index X should be removed from the string, then
 * inserted such that it ends up at index Y.
 * 
 * For example, suppose you start with abcde and perform the following operations:
 * 
 * swap position 4 with position 0 swaps the first and last letters, producing the input for the next step, ebcda.
 * swap letter d with letter b swaps the positions of d and b: edcba.
 * reverse positions 0 through 4 causes the entire string to be reversed, producing abcde.
 * rotate left 1 step shifts all letters left one position, causing the first letter to wrap to the end of the string:
 * bcdea.
 * move position 1 to position 4 removes the letter at position 1 (c), then inserts it at position 4 (the end of the
 * string): bdeac.
 * move position 3 to position 0 removes the letter at position 3 (a), then inserts it at position 0 (the front of the
 * string): abdec.
 * rotate based on position of letter b finds the index of letter b (1), then rotates the string right once plus a
 * number of times equal to that index (2): ecabd.
 * rotate based on position of letter d finds the index of letter d (4), then rotates the string right once, plus a
 * number of times equal to that index, plus an additional time because the index was at least 4, for a total of 6
 * right rotations: decab.
 * 
 * After these steps, the resulting scrambled password is decab.
 * Now, you just need to generate a new scrambled password and you can access the system. Given the list of scrambling
 * operations in your puzzle input, what is the result of scrambling abcdefgh?
 * 
 * --- Part Two ---
 * You scrambled the password correctly, but you discover that you can't actually modify the password file on the
 * system. You'll need to un-scramble one of the existing passwords by reversing the scrambling process.
 * What is the un-scrambled version of the scrambled password fbgdceah?
 * 
 */
public class ScramblingFunction {

	private final List<ScramblingInstruction> instructions;

	public ScramblingFunction(Stream<String> lines) {
		this.instructions = lines.map(ScramblingInstruction::of).collect(toList());
	}

	public String scramble(String input) {
		String output = input;

		for (ScramblingInstruction instruction : instructions) {
			output = instruction.apply(output);
		}

		return output;
	}

	/**
	 * The sophisticated way to do this would be to identify the reverse
	 * operation of each instruction, and apply those, but the possiblity space
	 * for a 8 letter password is so small that we can just try 'em and see.
	 */
	public String unscramble(String scrambled) {
		return permutations(scrambled) //
				.filter(possibility -> this.scramble(possibility).equals(scrambled)) //
				.findAny() //
				.get();
	}

	private static Stream<String> permutations(String input) {
		List<Character> chars = Chars.asList(input.toCharArray());
		return Collections2.permutations(chars).stream() //
				.map(ScramblingFunction::charsToString);
	}

	private static String charsToString(List<Character> chars) {
		return chars.stream().collect(CollectorUtils.charsToString());
	}

	static class ScramblingInstruction {
		private final ScramblingOperation op;
		private final Matcher matcher;

		private ScramblingInstruction(ScramblingOperation op, Matcher matcher) {
			this.op = op;
			Preconditions.checkArgument(matcher.matches());
			this.matcher = matcher;
		}

		public static ScramblingInstruction of(String input) {
			return Arrays.stream(ScramblingOperation.values()) //
					.filter(op -> op.pattern.matcher(input).matches()) //
					.map(op -> new ScramblingInstruction(op, op.pattern.matcher(input))) //
					.findFirst() //
					.orElseThrow(() -> new IllegalArgumentException("Unrecognized instruction " + input));
		}

		public int getInt(int oneBasedIndex) {
			return Integer.parseInt(this.getString(oneBasedIndex));
		}

		public String getString(int oneBasedIndex) {
			return matcher.group(oneBasedIndex);
		}

		public String apply(String input) {
			return op.apply(input, this);
		}
	}

	private static enum ScramblingOperation {
		SWAP_POSITION("swap position (\\d+) with position (\\d+)") {
			@Override
			public String apply(String input, ScramblingInstruction instruction) {
				return swap(input, instruction.getInt(1), instruction.getInt(2));
			}
		}, //

		SWAP_LETTER("swap letter (\\w+) with letter (\\w+)") {
			@Override
			public String apply(String input, ScramblingInstruction instruction) {
				int indexOne = input.indexOf(instruction.getString(1));
				int indexTwo = input.indexOf(instruction.getString(2));
				return swap(input, indexOne, indexTwo);
			}
		}, //

		ROTATE_LEFT("rotate left (\\d+) steps?") {
			@Override
			public String apply(String input, ScramblingInstruction instruction) {
				return rotateLeft(input, instruction.getInt(1));
			}
		}, //

		ROTATE_RIGHT("rotate right (\\d+) steps?") {
			@Override
			public String apply(String input, ScramblingInstruction instruction) {
				return rotateRight(input, instruction.getInt(1));
			}
		}, //

		ROTATE_BASED_ON_LETTER("rotate based on position of letter (\\w+)") {
			@Override
			public String apply(String input, ScramblingInstruction instruction) {
				int index = input.indexOf(instruction.getString(1));
				int steps = 1 + index + (index >= 4 ? 1 : 0);
				return rotateRight(input, steps);
			}
		}, //

		REVERSE_POSITIONS("reverse positions (\\d+) through (\\d+)") {
			@Override
			public String apply(String input, ScramblingInstruction instruction) {
				int min = instruction.getInt(1);
				int max = instruction.getInt(2);

				return input.substring(0, min) + //
				reverse(input.substring(min, max + 1)) + //
				input.substring(max + 1);
			}
		}, //

		MOVE_POSITION("move position (\\d+) to position (\\d+)") {
			@Override
			public String apply(String input, ScramblingInstruction instruction) {
				int source = instruction.getInt(1);
				int target = instruction.getInt(2);
				if (source < target) {
					return input.substring(0, source) + input.substring(source + 1, target + 1) + input.charAt(source)
							+ input.substring(target + 1);
				} else {
					return input.substring(0, target) + input.charAt(source) + input.substring(target, source)
							+ input.substring(source + 1);
				}
			}
		}; //

		private final Pattern pattern;

		ScramblingOperation(String regex) {
			this.pattern = Pattern.compile(regex);
		}

		public abstract String apply(String input, ScramblingInstruction instruction);
	}

	private static String swap(String input, int indexOne, int indexTwo) {
		int min = Math.min(indexOne, indexTwo);
		int max = Math.max(indexOne, indexTwo);

		return input.substring(0, min) + //
				input.charAt(max) + //
				input.substring(min + 1, max) + //
				input.charAt(min) + //
				input.substring(max + 1);
	}

	private static String rotateLeft(String input, int steps) {
		int effectiveSteps = Math.floorMod(steps, input.length());
		return input.substring(effectiveSteps) + input.substring(0, effectiveSteps);
	}

	private static String rotateRight(String input, int steps) {
		return rotateLeft(input, -steps);
	}

	private static String reverse(String input) {
		return new StringBuilder(input).reverse().toString();
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day21/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			ScramblingFunction function = new ScramblingFunction(lines);
			System.out.println(function.scramble("abcdefgh"));
			System.out.println(function.unscramble("fbgdceah"));
		}
	}
}