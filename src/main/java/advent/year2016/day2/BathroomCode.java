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

	private static final String[][] KEYS = { //
			{ "1", "2", "3" }, //
			{ "4", "5", "6" }, //
			{ "7", "8", "9" } //
	};

	private List<String> keysInCode;

	private int x;
	private int y;

	private BathroomCode() {
		keysInCode = new LinkedList<>();
		x = 1;
		y = 1;
	}

	public static BathroomCode of(Stream<String> instructionLines) {
		BathroomCode output = new BathroomCode();

		instructionLines.forEach(output::applyInstructionLine);

		return output;
	}

	private void applyInstructionLine(String instructionLine) {
		for (String instruction : instructionLine.trim().split("")) {
			this.applyInstruction(instruction);
		}

		keysInCode.add(KEYS[y][x]);
	}

	private void applyInstruction(String instruction) {
		this.applyInstruction(Instruction.valueOf(instruction));
	}

	private void applyInstruction(Instruction instruction) {
		this.x += instruction.deltaX;
		this.y += instruction.deltaY;

		this.x = Math.max(0, this.x);
		this.x = Math.min(KEYS[0].length - 1, this.x);
		this.y = Math.max(0, this.y);
		this.y = Math.min(KEYS.length - 1, this.y);
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

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			BathroomCode code = BathroomCode.of(lines);
			System.out.println(code.keyString());
		}
	}
}
