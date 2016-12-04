package advent.year2016.day4;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Room {

	private static final Pattern ROOM_PATTERN = Pattern.compile("(.*)-(\\d+)\\[(.*)\\]");
	private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]");

	private final String encryptedName;
	private final int sectorId;
	private final String checksum;

	public Room(String stringRep) {
		Matcher matcher = ROOM_PATTERN.matcher(stringRep);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Bad room " + stringRep);
		}

		this.encryptedName = matcher.group(1);
		this.sectorId = Integer.valueOf(matcher.group(2));
		this.checksum = matcher.group(3);
	}

	public boolean isReal() {
		Map<String, Long> characterFrequency = Arrays.stream(this.encryptedName.split("")) //
				.filter(character -> LETTER_PATTERN.matcher(character).matches()) //
				.collect(groupingBy(Function.identity(), counting()));

		Comparator<String> lowestFrequencyFirst = comparing(characterFrequency::get);
		Comparator<String> checksumOrdering = lowestFrequencyFirst //
				.reversed() //
				.thenComparing(naturalOrder());

		String expectedChecksum = characterFrequency.keySet() //
				.stream() //
				.sorted(checksumOrdering) //
				.limit(5) //
				.collect(joining(""));

		return expectedChecksum.equals(this.checksum);
	}

	public String decryptedName() {
		return this.encryptedName.chars() //
				.map(this::decrypt) //
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) //
				.toString();
	}

	@Override
	public String toString() {
		return this.decryptedName() + " " + this.sectorId;
	}

	private int decrypt(int input) {
		if (input == '-') {
			return ' ';
		}

		int output = input + (this.sectorId % 26);
		if (output > 'z') {
			output -= 26;
		}
		return output;
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day4/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			long sectorIdSum = lines //
					.map(Room::new) //
					.filter(Room::isReal) //
					.mapToInt(room -> room.sectorId) //
					.sum();
			System.out.println(sectorIdSum);
		}

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			lines //
					.map(Room::new) //
					.filter(Room::isReal) //
					// Checked all the names first as exploration, before
					// putting this filter in place.
					.filter(room -> room.decryptedName().contains("north")) //
					.map(Room::toString) //
					.forEach(System.out::println);
		}
	}
}
