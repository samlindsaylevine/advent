package advent.year2016.day2;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

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
