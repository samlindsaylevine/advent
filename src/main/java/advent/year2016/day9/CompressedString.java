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

	public String singlyDecompress() {
		SingleDecompressProcessor processor = new SingleDecompressProcessor();
		this.compressedRepresentation.chars().forEachOrdered(processor::add);
		return processor.result();
	}

	public long recursivelyDecompressedLength() {
		RecursiveDecompressProcessor processor = new RecursiveDecompressProcessor();
		this.compressedRepresentation.chars().forEachOrdered(processor::add);
		return processor.result();
	}

	/**
	 * A state machine for processing the compressed string one character at a
	 * time.
	 * 
	 * @param <T>
	 *            The result type of the processor (since one wants to return
	 *            the string, the other simply a length).
	 */
	private static abstract class DecompressionProcessor<T> {

		private ReadMode readMode;
		private StringBuilder tempBuffer;
		private int charactersLeftToRepeat;
		private int lastSectionRepeatCount;

		public DecompressionProcessor() {
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
				this.addToOutput(1, Character.toString(character));
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
					this.addToOutput(this.lastSectionRepeatCount, this.tempBuffer.toString());
					this.readMode = ReadMode.NORMAL;
				}
			} else {
				throw new IllegalStateException("Unexpected state " + this.readMode + " and input " + character);
			}
		}

		protected abstract void addToOutput(int repeatNTimes, String section);

		protected abstract T result();

		private static enum ReadMode {
			NORMAL, //
			READING_SECTION_LENGTH, //
			READING_SECTION_REPEAT_COUNT, //
			READING_SECTION;
		}
	}

	/**
	 * Processor that makes only a single pass and returns the actual
	 * decompressed string.
	 */
	private static class SingleDecompressProcessor extends DecompressionProcessor<String> {

		private final StringBuilder result;

		public SingleDecompressProcessor() {
			super();
			this.result = new StringBuilder();
		}

		@Override
		protected void addToOutput(int repeatNTimes, String section) {
			IntStream.range(0, repeatNTimes).forEach(i -> this.result.append(section));
		}

		@Override
		protected String result() {
			return this.result.toString();
		}

	}

	/**
	 * Processor that continues decompressing every section as long as it still
	 * has compression markers, and only remembers the total output length, not
	 * the output itself.
	 */
	private static class RecursiveDecompressProcessor extends DecompressionProcessor<Long> {

		private long length;

		public RecursiveDecompressProcessor() {
			super();
			this.length = 0;
		}

		@Override
		protected void addToOutput(int repeatNTimes, String section) {
			if (section.contains("(")) {
				this.length += repeatNTimes * new CompressedString(section).recursivelyDecompressedLength();
			} else {
				this.length += repeatNTimes * section.length();
			}
		}

		@Override
		protected Long result() {
			return this.length;
		}

	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day9/input.txt");

		String input = Files.readAllLines(inputFilePath) //
				.stream() //
				.collect(joining(""));

		CompressedString compressedString = new CompressedString(input);
		System.out.println(compressedString.singlyDecompress().length());
		System.out.println(compressedString.recursivelyDecompressedLength());
	}

}
