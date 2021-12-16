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

/**
 * --- Day 4: Security Through Obscurity ---
 * Finally, you come across an information kiosk with a list of rooms.  Of course, the list is encrypted and full of
 * decoy data, but the instructions to decode the list are barely hidden nearby.  Better remove the decoy data first.
 * Each room consists of an encrypted name (lowercase letters separated by dashes) followed by a dash, a sector ID, and
 * a checksum in square brackets.
 * A room is real (not a decoy) if the checksum is the five most common letters in the encrypted name, in order, with
 * ties broken by alphabetization.  For example:
 * 
 * aaaaa-bbb-z-y-x-123[abxyz] is a real room because the most common letters are a (5), b (3), and then a tie between
 * x, y, and z, which are listed alphabetically.
 * a-b-c-d-e-f-g-h-987[abcde] is a real room because although the letters are all tied (1 of each), the first five are
 * listed alphabetically.
 * not-a-real-room-404[oarel] is a real room.
 * totally-real-room-200[decoy] is not.
 * 
 * Of the real rooms from the list above, the sum of their sector IDs is 1514.
 * What is the sum of the sector IDs of the real rooms?
 * 
 * --- Part Two ---
 * With all the decoy data out of the way, it's time to decrypt this list and get moving.
 * The room names are encrypted by a state-of-the-art shift cipher, which is nearly unbreakable without the right
 * software. However, the information kiosk designers at Easter Bunny HQ were not expecting to deal with a master
 * cryptographer like yourself.
 * To decrypt a room name, rotate each letter forward through the alphabet a number of times equal to the room's sector
 * ID.  A becomes B, B becomes C, Z becomes A, and so on. Dashes become spaces.
 * For example, the real name for qzmt-zixmtkozy-ivhz-343 is very encrypted name.
 * What is the sector ID of the room where North Pole objects are stored?
 * 
 */
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