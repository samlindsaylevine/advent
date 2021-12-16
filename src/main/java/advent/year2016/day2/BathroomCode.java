package advent.year2016.day2;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * --- Day 2: Bathroom Security ---
 * You arrive at Easter Bunny Headquarters under cover of darkness. However, you left in such a rush that you forgot to
 * use the bathroom! Fancy office buildings like this one usually have keypad locks on their bathrooms, so you search
 * the front desk for the code.
 * "In order to improve security," the document you find says, "bathroom codes will no longer be written down. 
 * Instead, please memorize and follow the procedure below to access the bathrooms."
 * The document goes on to explain that each button to be pressed can be found by starting on the previous button and
 * moving to adjacent buttons on the keypad: U moves up, D moves down, L moves left, and R moves right. Each line of
 * instructions corresponds to one button, starting at the previous button (or, for the first line, the "5" button);
 * press whatever button you're on at the end of each line. If a move doesn't lead to a button, ignore it.
 * You can't hold it much longer, so you decide to figure out the code as you walk to the bathroom. You picture a
 * keypad like this:
 * 1 2 3
 * 4 5 6
 * 7 8 9
 * 
 * Suppose your instructions are:
 * ULL
 * RRDDD
 * LURDL
 * UUUUD
 * 
 * 
 * You start at "5" and move up (to "2"), left (to "1"), and left (you can't, and stay on "1"), so the first button is
 * 1.
 * Starting from the previous button ("1"), you move right twice (to "3") and then down three times (stopping at "9"
 * after two moves and ignoring the third), ending up with 9.
 * Continuing from "9", you move left, up, right, down, and left, ending with 8.
 * Finally, you move up four times (stopping at "2"), then down once, ending with 5.
 * 
 * So, in this example, the bathroom code is 1985.
 * Your puzzle input is the instructions from the document you found at the front desk. What is the bathroom code?
 * 
 * --- Part Two ---
 * You finally arrive at the bathroom (it's a several minute walk from the lobby so visitors can behold the many fancy
 * conference rooms and water coolers on this floor) and go to punch in the code.  Much to your bladder's dismay, the
 * keypad is not at all like you imagined it.  Instead, you are confronted with the result of hundreds of man-hours of
 * bathroom-keypad-design meetings:
 *     1
 *   2 3 4
 * 5 6 7 8 9
 *   A B C
 *     D
 * 
 * You still start at "5" and stop when you're at an edge, but given the same instructions as above, the outcome is
 * very different:
 * 
 * You start at "5" and don't move at all (up and left are both edges), ending at 5.
 * Continuing from "5", you move right twice and down three times (through "6", "7", "B", "D", "D"), ending at D.
 * Then, from "D", you move five more times (through "D", "B", "C", "C", "B"), ending at B.
 * Finally, after five more moves, you end at 3.
 * 
 * So, given the actual keypad layout, the code would be 5DB3.
 * Using the same instructions in your puzzle input, what is the correct bathroom code?
 * 
 */
public class BathroomCode {

	private static final String STARTING_KEY = "5";

	/**
	 * A blank, or whitespace, string at a position on the keypad means it is
	 * inaccessible.
	 */

	private static final String[][] SQUARE_KEYS = { //
			{ "1", "2", "3" }, //
			{ "4", "5", "6" }, //
			{ "7", "8", "9" } //
	};

	private static final String[][] DIAMOND_KEYS = { //
			{ " ", " ", "1", " ", " " }, //
			{ " ", "2", "3", "4", " " }, //
			{ "5", "6", "7", "8", "9" }, //
			{ " ", "A", "B", "C", " " }, //
			{ " ", " ", "D", " ", " " } //
	};

	private List<String> keysInCode;

	private final String[][] keypad;

	private int x;
	private int y;

	private BathroomCode(String[][] keypad) {
		keysInCode = new LinkedList<>();
		this.keypad = keypad;
		this.setStartingKey();
	}

	private void setStartingKey() {
		for (int y = 0; y < this.keypad.length; y++) {
			String[] keyRow = this.keypad[y];
			for (int x = 0; x < keyRow.length; x++) {
				if (keyRow[x].equals(STARTING_KEY)) {
					this.x = x;
					this.y = y;
					return;
				}
			}
		}

		throw new IllegalStateException("Starting key " + STARTING_KEY + " not found!");
	}

	public static BathroomCode squareKeys() {
		return new BathroomCode(SQUARE_KEYS);
	}

	public static BathroomCode diamondKeys() {
		return new BathroomCode(DIAMOND_KEYS);
	}

	public void applyInstructionLine(String instructionLine) {
		for (String instruction : instructionLine.trim().split("")) {
			this.applyInstruction(instruction);
		}

		keysInCode.add(keypad[y][x]);
	}

	private void applyInstruction(String instruction) {
		this.applyInstruction(Instruction.valueOf(instruction));
	}

	/**
	 * Apply an instruction as long as it moves us to a legal space. (Off the
	 * keypad, or to an empty space, are not legal moves.)
	 */
	private void applyInstruction(Instruction instruction) {
		int newX = this.x + instruction.deltaX;
		int newY = this.y + instruction.deltaY;

		if (newY >= 0 && //
				newY < this.keypad.length && //
				newX >= 0 && //
				newX < this.keypad[y].length && //
				!this.keypad[newY][newX].trim().isEmpty()) {
			this.x = newX;
			this.y = newY;
		}
	}

	public String keyString() {
		return keysInCode.stream().collect(joining(""));
	}

	private static enum Instruction {

		U(0, -1), //
		D(0, 1), //
		L(-1, 0), //
		R(1, 0);

		private final int deltaX;
		private final int deltaY;

		private Instruction(int deltaX, int deltaY) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}
	}

	public static final void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day2/input.txt");

		BathroomCode square = BathroomCode.squareKeys();
		BathroomCode diamond = BathroomCode.diamondKeys();

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			lines.forEach(line -> {
				square.applyInstructionLine(line);
				diamond.applyInstructionLine(line);
			});
		}

		System.out.println(square.keyString());
		System.out.println(diamond.keyString());
	}
}