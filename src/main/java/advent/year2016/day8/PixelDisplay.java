package advent.year2016.day8;

import static advent.utils.CollectorUtils.toArrayList;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

/**
 * --- Day 8: Two-Factor Authentication ---
 * You come across a door implementing what you can only assume is an implementation of two-factor authentication after
 * a long game of requirements telephone.
 * To get past the door, you first swipe a keycard (no problem; there was one on a nearby desk). Then, it displays a
 * code on a little screen, and you type that code on a keypad. Then, presumably, the door unlocks.
 * Unfortunately, the screen has been smashed. After a few minutes, you've taken everything apart and figured out how
 * it works. Now you just have to work out what the screen would have displayed.
 * The magnetic strip on the card you swiped encodes a series of instructions for the screen; these instructions are
 * your puzzle input. The screen is 50 pixels wide and 6 pixels tall, all of which start off, and is capable of three
 * somewhat peculiar operations:
 * 
 * rect AxB turns on all of the pixels in a rectangle at the top-left of the screen which is A wide and B tall.
 * rotate row y=A by B shifts all of the pixels in row A (0 is the top row) right by B pixels. Pixels that would fall
 * off the right end appear at the left end of the row.
 * rotate column x=A by B shifts all of the pixels in column A (0 is the left column) down by B pixels. Pixels that
 * would fall off the bottom appear at the top of the column.
 * 
 * For example, here is a simple sequence on a smaller screen:
 * 
 * rect 3x2 creates a small rectangle in the top-left corner:###....
 * ###....
 * .......
 * rotate column x=1 by 1 rotates the second column down by one pixel:#.#....
 * ###....
 * .#.....
 * rotate row y=0 by 4 rotates the top row right by four pixels:....#.#
 * ###....
 * .#.....
 * rotate column x=1 by 1 again rotates the second column down by one pixel, causing the bottom pixel to wrap back to
 * the top:.#..#.#
 * #.#....
 * .#.....
 * 
 * As you can see, this display technology is extremely powerful, and will soon dominate the
 * tiny-code-displaying-screen market.  That's what the advertisement on the back of the display tries to convince you,
 * anyway.
 * There seems to be an intermediate check of the voltage used by the display: after you swipe your card, if the screen
 * did work, how many pixels should be lit?
 * 
 * --- Part Two ---
 * You notice that the screen is only capable of displaying capital letters; in the font it uses, each letter is 5
 * pixels wide and 6 tall.
 * After you swipe your card, what code is the screen trying to display?
 * 
 */
public class PixelDisplay {

	private List<List<Boolean>> pixels;

	private static final int WIDTH = 50;
	private static final int HEIGHT = 6;

	public PixelDisplay() {
		this(WIDTH, HEIGHT);
	}

	public PixelDisplay(int width, int height) {
		this.pixels = IntStream.range(0, height) //
				.mapToObj(any -> this.newRow(width)) //
				.collect(toArrayList());
	}

	private List<Boolean> newRow(int width) {
		return IntStream.range(0, width) //
				.mapToObj(any -> false) //
				.collect(toArrayList());
	}

	public long litPixelCount() {
		return this.pixels.stream() //
				.flatMap(List::stream) //
				.filter(i -> i) //
				.count();
	}

	@Override
	public String toString() {
		return this.pixels.stream() //
				.map(PixelDisplay::rowToString) //
				.collect(joining("\n"));
	}

	private static String rowToString(List<Boolean> row) {
		return row.stream() //
				.map(pixel -> pixel ? "#" : ".") //
				.collect(joining(""));
	}

	public void applyOperation(String operationString) {
		Operation operation = Arrays.stream(Operation.values()) //
				.filter(possibleOp -> possibleOp.matches(operationString)) //
				.findFirst() //
				.orElseThrow(() -> new IllegalArgumentException("Bad operation " + operationString));

		operation.apply(operationString, this);
	}

	private void rect(int width, int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				this.pixels.get(y).set(x, true);
			}
		}
	}

	private void rotateRow(int index, int amount) {
		Collections.rotate(this.pixels.get(index), amount);
	}

	private void rotateColumn(int index, int amount) {
		List<Boolean> column = this.getColumn(index);
		Collections.rotate(column, amount);
		this.setColumn(index, column);
	}

	private List<Boolean> getColumn(int index) {
		return IntStream.range(0, this.pixels.size()) //
				.mapToObj(i -> this.pixels.get(i).get(index)) //
				.collect(toArrayList());
	}

	private void setColumn(int index, List<Boolean> newColumn) {
		Preconditions.checkArgument(newColumn.size() == this.pixels.size());

		for (int i = 0; i < newColumn.size(); i++) {
			this.pixels.get(i).set(index, newColumn.get(i));
		}
	}

	private static enum Operation {
		RECT("rect (\\d+)x(\\d+)", //
				(display, a, b) -> display.rect(a, b)),

		ROTATE_ROW("rotate row y=(\\d+) by (\\d+)", //
				(display, a, b) -> display.rotateRow(a, b)),

		ROTATE_COLUMN("rotate column x=(\\d+) by (\\d+)", //
				(display, a, b) -> display.rotateColumn(a, b));

		private final Pattern pattern;
		private final OperationResult operate;

		private Operation(String regex, OperationResult operate) {
			this.pattern = Pattern.compile(regex);
			this.operate = operate;
		}

		public boolean matches(String operation) {
			return this.pattern.matcher(operation).matches();
		}

		public void apply(String operation, PixelDisplay display) {
			Matcher matcher = this.pattern.matcher(operation);
			Preconditions.checkArgument(matcher.matches());
			int a = Integer.valueOf(matcher.group(1));
			int b = Integer.valueOf(matcher.group(2));
			this.operate.execute(display, a, b);
		}

		// Taking a bit of a shortcut here because all of the instructions take
		// two integers.
		@FunctionalInterface
		private static interface OperationResult {
			public void execute(PixelDisplay display, int firstValue, int secondValue);
		}
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day8/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			PixelDisplay display = new PixelDisplay();
			lines.forEach(display::applyOperation);
			System.out.println(display.litPixelCount());
			System.out.println(display);
		}
	}

}