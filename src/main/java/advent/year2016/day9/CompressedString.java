package advent.year2016.day9;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

/**
 * --- Day 9: Explosives in Cyberspace ---
 * Wandering around a secure area, you come across a datalink port to a new part of the network. After briefly scanning
 * it for interesting files, you find one file in particular that catches your attention. It's compressed with an
 * experimental format, but fortunately, the documentation for the format is nearby.
 * The format compresses a sequence of characters. Whitespace is ignored. To indicate that some sequence should be
 * repeated, a marker is added to the file, like (10x2). To decompress this marker, take the subsequent 10 characters
 * and repeat them 2 times. Then, continue reading the file after the repeated data.  The marker itself is not included
 * in the decompressed output.
 * If parentheses or other characters appear within the data referenced by a marker, that's okay - treat it like normal
 * data, not a marker, and then resume looking for markers after the decompressed section.
 * For example:
 * 
 * ADVENT contains no markers and decompresses to itself with no changes, resulting in a decompressed length of 6.
 * A(1x5)BC repeats only the B a total of 5 times, becoming ABBBBBC for a decompressed length of 7.
 * (3x3)XYZ becomes XYZXYZXYZ for a decompressed length of 9.
 * A(2x2)BCD(2x2)EFG doubles the BC and EF, becoming ABCBCDEFEFG for a decompressed length of 11.
 * (6x1)(1x3)A simply becomes (1x3)A - the (1x3) looks like a marker, but because it's within a data section of another
 * marker, it is not treated any differently from the A that comes after it. It has a decompressed length of 6.
 * X(8x2)(3x3)ABCY becomes X(3x3)ABC(3x3)ABCY (for a decompressed length of 18), because the decompressed data from the
 * (8x2) marker (the (3x3)ABC) is skipped and not processed further.
 * 
 * What is the decompressed length of the file (your puzzle input)? Don't count whitespace.
 * 
 * --- Part Two ---
 * Apparently, the file actually uses version two of the format.
 * In version two, the only difference is that markers within decompressed data are decompressed. This, the
 * documentation explains, provides much more substantial compression capabilities, allowing many-gigabyte files to be
 * stored in only a few kilobytes.
 * For example:
 * 
 * (3x3)XYZ still becomes XYZXYZXYZ, as the decompressed section contains no markers.
 * X(8x2)(3x3)ABCY becomes XABCABCABCABCABCABCY, because the decompressed data from the (8x2) marker is then further
 * decompressed, thus triggering the (3x3) marker twice for a total of six ABC sequences.
 * (27x12)(20x12)(13x14)(7x10)(1x12)A decompresses into a string of A repeated 241920 times.
 * (25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN becomes 445 characters long.
 * 
 * Unfortunately, the computer you brought probably doesn't have enough memory to actually decompress the file; you'll
 * have to come up with another way to get its decompressed length.
 * What is the decompressed length of the file using this improved format?
 * 
 */
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