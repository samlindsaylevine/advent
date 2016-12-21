package advent.year2016.day21;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;

import advent.year2016.day21.ScramblingFunction.ScramblingInstruction;

public class ScramblingFunctionTest {

	@Test
	public void swapNumbers() {
		String input = "abcde";
		String output = ScramblingInstruction.of("swap position 4 with position 0").apply(input);
		String expected = "ebcda";
		assertEquals(expected, output);
	}

	@Test
	public void swapLetters() {
		String input = "ebcda";
		String output = ScramblingInstruction.of("swap letter d with letter b").apply(input);
		String expected = "edcba";
		assertEquals(expected, output);
	}

	@Test
	public void reversePositions() {
		String input = "edcba";
		String output = ScramblingInstruction.of("reverse positions 0 through 4").apply(input);
		String expected = "abcde";
		assertEquals(expected, output);
	}

	@Test
	public void rotateLeft() {
		String input = "abcde";
		String output = ScramblingInstruction.of("rotate left 1 step").apply(input);
		String expected = "bcdea";
		assertEquals(expected, output);
	}

	@Test
	public void movePosition1to4() {
		String input = "bcdea";
		String output = ScramblingInstruction.of("move position 1 to position 4").apply(input);
		String expected = "bdeac";
		assertEquals(expected, output);
	}

	@Test
	public void movePosition3to0() {
		String input = "bdeac";
		String output = ScramblingInstruction.of("move position 3 to position 0").apply(input);
		String expected = "abdec";
		assertEquals(expected, output);
	}

	@Test
	public void rotateNoBonus() {
		String input = "abdec";
		String output = ScramblingInstruction.of("rotate based on position of letter b").apply(input);
		String expected = "ecabd";
		assertEquals(expected, output);
	}

	@Test
	public void rotateWithBonus() {
		String input = "abdec";
		String output = ScramblingInstruction.of("rotate based on position of letter d").apply(input);
		String expected = "decab";
		assertEquals(expected, output);
	}

	@Test
	public void wholeFunction() {
		Stream<String> lines = Stream.of("swap position 4 with position 0", //
				"swap letter d with letter b", //
				"reverse positions 0 through 4", //
				"rotate left 1 step", //
				"move position 1 to position 4", //
				"move position 3 to position 0", //
				"rotate based on position of letter b", //
				"rotate based on position of letter d");

		ScramblingFunction function = new ScramblingFunction(lines);

		assertEquals("decab", function.scramble("abcde"));
	}
}
