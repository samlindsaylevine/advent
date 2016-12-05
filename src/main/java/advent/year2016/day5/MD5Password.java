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

		output.ifPresent(out -> out.println(password));

		while (!password.isComplete()) {
			String nextHash = hexMD5Hash(this.doorId + i);
			if (isInterestingHash(nextHash)) {
				String position = nextHash.substring(5, 6);
				String character = nextHash.substring(6, 7);
				boolean updated = password.put(position, character);
				if (updated) {
					output.ifPresent(out -> out.println(password));
				}
			}
			i++;
		}

		output.ifPresent(out -> out.println("HACK THE PLANET"));
		return password.toString();

	}

	private static String hexMD5Hash(String input) {
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
