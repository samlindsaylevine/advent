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
