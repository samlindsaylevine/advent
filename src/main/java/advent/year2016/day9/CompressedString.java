package advent.year2016.day9;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

public class CompressedString {

	private String compressedRepresentation;

	public CompressedString(String representation) {
		this.compressedRepresentation = representation;
	}

	public String decompress() {
		return this.compressedRepresentation.chars() //
				.collect(DecompressionResult::new, //
						DecompressionResult::add, //
						(l, r) -> {
							throw new UnsupportedOperationException();
						}) // Not implementing merging because we know this will
							// not be parallel.
				.toString();
	}

	private static class DecompressionResult {

		private final StringBuilder result;
		private ReadMode readMode;
		private StringBuilder tempBuffer;
		private int charactersLeftToRepeat;
		private int lastSectionRepeatCount;

		public DecompressionResult() {
			this.result = new StringBuilder();
			this.readMode = ReadMode.NORMAL;
			this.tempBuffer = new StringBuilder();
		}

		public void add(int i) {
			this.add((char) i);
		}

		public void add(char character) {
			if (this.readMode == ReadMode.NORMAL && character == '(') {
				this.tempBuffer = new StringBuilder();
				this.readMode = ReadMode.READING_SECTION_LENGTH;
			} else if (this.readMode == ReadMode.NORMAL) {
				this.result.append(character);
			} else if (this.readMode == ReadMode.READING_SECTION_LENGTH && character == 'x') {
				this.charactersLeftToRepeat = Integer.valueOf(this.tempBuffer.toString());
				this.tempBuffer = new StringBuilder();
				this.readMode = ReadMode.READING_SECTION_REPEAT_COUNT;
			} else if (this.readMode == ReadMode.READING_SECTION_LENGTH) {
				this.tempBuffer.append(character);
			} else if (this.readMode == ReadMode.READING_SECTION_REPEAT_COUNT && character == ')') {
				this.lastSectionRepeatCount = Integer.valueOf(this.tempBuffer.toString());
				this.tempBuffer = new StringBuilder();
				this.readMode = ReadMode.READING_SECTION;
			} else if (this.readMode == ReadMode.READING_SECTION_REPEAT_COUNT) {
				this.tempBuffer.append(character);
			} else if (this.readMode == ReadMode.READING_SECTION) {
				this.tempBuffer.append(character);
				this.charactersLeftToRepeat--;
				if (this.charactersLeftToRepeat == 0) {
					IntStream.range(0, this.lastSectionRepeatCount).forEach(i -> this.result.append(this.tempBuffer));
					this.readMode = ReadMode.NORMAL;
				}
			} else {
				throw new IllegalStateException("Unexpected state " + this.readMode + " and input " + character);
			}
		}

		@Override
		public String toString() {
			return this.result.toString();
		}

		private static enum ReadMode {
			NORMAL, //
			READING_SECTION_LENGTH, //
			READING_SECTION_REPEAT_COUNT, //
			READING_SECTION;
		}
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day9/input.txt");

		String input = Files.readAllLines(inputFilePath) //
				.stream() //
				.collect(joining(""));

		System.out.println(new CompressedString(input).decompress().length());
	}

}
