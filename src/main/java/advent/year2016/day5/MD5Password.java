package advent.year2016.day5;

import static java.util.stream.Collectors.joining;

import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.codec.binary.Hex;

/**
 * --- Day 5: How About a Nice Game of Chess? ---
 * You are faced with a security door designed by Easter Bunny engineers that seem to have acquired most of their
 * security knowledge by watching hacking movies.
 * The eight-character password for the door is generated one character at a time by finding the MD5 hash of some Door
 * ID (your puzzle input) and an increasing integer index (starting with 0).
 * A hash indicates the next character in the password if its hexadecimal representation starts with five zeroes. If it
 * does, the sixth character in the hash is the next character of the password.
 * For example, if the Door ID is abc:
 * 
 * The first index which produces a hash that starts with five zeroes is 3231929, which we find by hashing abc3231929;
 * the sixth character of the hash, and thus the first character of the password, is 1.
 * 5017308 produces the next interesting hash, which starts with 000008f82..., so the second character of the password
 * is 8.
 * The third time a hash starts with five zeroes is for abc5278568, discovering the character f.
 * 
 * In this example, after continuing this search a total of eight times, the password is 18f47a30.
 * Given the actual Door ID, what is the password?
 * 
 * --- Part Two ---
 * As the door slides open, you are presented with a second door that uses a slightly more inspired security mechanism.
 * Clearly unimpressed by the last version (in what movie is the password decrypted in order?!), the Easter Bunny
 * engineers have worked out a better solution.
 * Instead of simply filling in the password from left to right, the hash now also indicates the position within the
 * password to fill. You still look for hashes that begin with five zeroes; however, now, the sixth character
 * represents the position (0-7), and the seventh character is the character to put in that position.
 * A hash result of 000001f means that f is the second character in the password. Use only the first result for each
 * position, and ignore invalid positions.
 * For example, if the Door ID is abc:
 * 
 * The first interesting hash is from abc3231929, which produces 0000015...; so, 5 goes in position 1: _5______.
 * In the previous method, 5017308 produced an interesting hash; however, it is ignored, because it specifies an
 * invalid position (8).
 * The second interesting hash is at index 5357525, which produces 000004e...; so, e goes in position 4: _5__e___.
 * 
 * You almost choke on your popcorn as the final character falls into place, producing the password 05ace8e3.
 * Given the actual Door ID and this new method, what is the password? Be extra proud of your solution if it uses a
 * cinematic "decrypting" animation.
 * 
 */
public class MD5Password {

	private final String doorId;

	public MD5Password(String doorId) {
		this.doorId = doorId;
	}

	public String getOrderedPassword() {
		return IntStream.iterate(0, i -> i + 1) //
				.mapToObj(i -> hexMD5Hash(this.doorId + i)) //
				.filter(MD5Password::isInterestingHash) //
				// Password char is the 6th - strings are 0-indexed.
				.map(hash -> hash.substring(5, 6)) //
				.limit(8) //
				.collect(joining(""));
	}

	public String getCinematicPassword() {
		return this.getCinematicPassword(Optional.empty());
	}

	public String getCinematicPasswordWithAnimation() {
		return this.getCinematicPassword(Optional.of(System.out));
	}

	private String getCinematicPassword(Optional<PrintStream> output) {
		CinematicPasswordInProgress password = new CinematicPasswordInProgress();
		int i = 0;

		output.ifPresent(out -> out.print(password));

		while (!password.isComplete()) {
			String nextHash = hexMD5Hash(this.doorId + i);
			if (isInterestingHash(nextHash)) {
				String position = nextHash.substring(5, 6);
				String character = nextHash.substring(6, 7);
				boolean updated = password.put(position, character);
				if (updated) {
					output.ifPresent(out -> out.print("\r" + password));
				}
			}
			i++;
		}

		output.ifPresent(PrintStream::println);
		output.ifPresent(out -> out.println("HACK THE PLANET"));
		return password.toString();

	}

	public static String hexMD5Hash(String input) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// Never actually happens.
			throw new RuntimeException(e);
		}

		md5.reset();
		md5.update(input.getBytes());
		byte[] hash = md5.digest();

		return Hex.encodeHexString(hash);
	}

	private static boolean isInterestingHash(String hash) {
		return hash.startsWith("00000");
	}

	private static class CinematicPasswordInProgress {

		private List<String> password = new ArrayList<String>(Collections.nCopies(8, null));

		public boolean isComplete() {
			return this.password.stream().allMatch(Objects::nonNull);
		}

		@Override
		public String toString() {
			return this.password.stream() //
					.map(c -> c == null ? "_" : c) //
					.collect(joining(""));
		}

		/**
		 * Sets a value only if it is not already set; otherwise, returns
		 * without taking action.
		 * 
		 * Returns true if action was taken and a place was set.
		 */
		public boolean put(String indexStr, String value) {
			int index;

			try {
				index = Integer.valueOf(indexStr);
			} catch (NumberFormatException e) {
				return false;
			}

			if (index < this.password.size() && this.password.get(index) == null) {
				this.password.set(index, value);
				return true;
			} else {
				return false;
			}
		}
	}

	public static void main(String[] args) {
		MD5Password password = new MD5Password("cxdnnyjw");
		System.out.println(password.getOrderedPassword());
		password.getCinematicPasswordWithAnimation();
	}

}