package advent.year2016.day14;

import static advent.utils.CollectorUtils.flatMappingToMultimap;
import static advent.utils.CollectorUtils.toLinkedList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Multimap;

import advent.year2016.day5.MD5Password;

/**
 * --- Day 14: One-Time Pad ---
 * In order to communicate securely with Santa while you're on this mission, you've been using a one-time pad that you
 * generate using a pre-agreed algorithm. Unfortunately, you've run out of keys in your one-time pad, and so you need
 * to generate some more.
 * To generate keys, you first get a stream of random data by taking the MD5 of a pre-arranged salt (your puzzle input)
 * and an increasing integer index (starting with 0, and represented in decimal); the resulting MD5 hash should be
 * represented as a string of lowercase hexadecimal digits.
 * However, not all of these MD5 hashes are keys, and you need 64 new keys for your one-time pad.  A hash is a key only
 * if:
 * 
 * It contains three of the same character in a row, like 777. Only consider the first such triplet in a hash.
 * One of the next 1000 hashes in the stream contains that same character five times in a row, like 77777.
 * 
 * Considering future hashes for five-of-a-kind sequences does not cause those hashes to be skipped; instead,
 * regardless of whether the current hash is a key, always resume testing for keys starting with the very next hash.
 * For example, if the pre-arranged salt is abc:
 * 
 * The first index which produces a triple is 18, because the MD5 hash of abc18 contains ...cc38887a5.... However,
 * index 18 does not count as a key for your one-time pad, because none of the next thousand hashes (index 19 through
 * index 1018) contain 88888.
 * The next index which produces a triple is 39; the hash of abc39 contains eee. It is also the first key: one of the
 * next thousand hashes (the one at index 816) contains eeeee.
 * None of the next six triples are keys, but the one after that, at index 92, is: it contains 999 and index 200
 * contains 99999.
 * Eventually, index 22728 meets all of the criteria to generate the 64th key.
 * 
 * So, using our example salt of abc, index 22728 produces the 64th key.
 * Given the actual salt in your puzzle input, what index produces your 64th one-time pad key?
 * 
 * --- Part Two ---
 * Of course, in order to make this process even more secure, you've also implemented key stretching.
 * Key stretching forces attackers to spend more time generating hashes. Unfortunately, it forces everyone else to
 * spend more time, too.
 * To implement key stretching, whenever you generate a hash, before you use it, you first find the MD5 hash of that
 * hash, then the MD5 hash of that hash, and so on, a total of 2016 additional hashings. Always use lowercase
 * hexadecimal representations of hashes.
 * For example, to find the stretched hash for index 0 and salt abc:
 * 
 * Find the MD5 hash of abc0: 577571be4de9dcce85a041ba0410f29f.
 * Then, find the MD5 hash of that hash: eec80a0c92dc8a0777c619d9bb51e910.
 * Then, find the MD5 hash of that hash: 16062ce768787384c81fe17a7a60c7e3.
 * ...repeat many times...
 * Then, find the MD5 hash of that hash: a107ff634856bb300138cac6568c0f24.
 * 
 * So, the stretched hash for index 0 in this situation is a107ff.... In the end, you find the original hash (one use
 * of MD5), then find the hash-of-the-previous-hash 2016 times, for a total of 2017 uses of MD5.
 * The rest of the process remains the same, but now the keys are entirely different. Again for salt abc:
 * 
 * The first triple (222, at index 5) has no matching 22222 in the next thousand hashes.
 * The second triple (eee, at index 10) hash a matching eeeee at index 89, and so it is the first key.
 * Eventually, index 22551 produces the 64th key (triple fff with matching fffff at index 22859.
 * 
 * Given the actual salt in your puzzle input and using 2016 extra MD5 calls of key stretching, what index now produces
 * your 64th one-time pad key?
 * 
 */
public class OneTimePadKeyStream {

	private static final int FUTURE_HASH_DISTANCE = 1000;
	private static final int STRETCHED_HASH_ITERATIONS = 2017;

	private final String salt;

	private final int numberOfHashApplications;

	private OneTimePadKeyStream(String salt, int numberOfHashApplications) {
		this.salt = salt;
		this.numberOfHashApplications = numberOfHashApplications;
	}

	public static OneTimePadKeyStream unstretched(String salt) {
		return new OneTimePadKeyStream(salt, 1);
	}

	public static OneTimePadKeyStream stretched(String salt) {
		return new OneTimePadKeyStream(salt, STRETCHED_HASH_ITERATIONS);
	}

	public String possibleKey(int index) {
		return Stream.iterate(this.salt + index, MD5Password::hexMD5Hash) //
				.limit(this.numberOfHashApplications + 1) //
				.reduce((first, second) -> second) //
				.get();
	}

	public List<Key> keys(int keyCount) {
		List<Key> output = new ArrayList<>();
		int currentIndex = 0;

		// Keeping two data structures for lookup speed.
		LinkedList<Key> lookForward = IntStream.range(0, 1 + FUTURE_HASH_DISTANCE) //
				.mapToObj(i -> new Key(i, this.possibleKey(i))) //
				.collect(toLinkedList());
		Multimap<Character, Key> lookForwardsByQuintuples = lookForward.stream() //
				.collect(flatMappingToMultimap(key -> key.quintupleChars.stream(), i -> i));

		while (output.size() < keyCount) {

			// Debug progress indicator.
			// System.out.println(currentIndex);

			Key possibleKey = lookForward.removeFirst();
			possibleKey.quintupleChars.forEach(c -> lookForwardsByQuintuples.remove(c, possibleKey));

			possibleKey.firstTriplet.ifPresent(repeated -> {
				if (lookForwardsByQuintuples.containsKey(repeated)) {
					output.add(possibleKey);
				}
			});

			currentIndex++;

			int nextLookForwardIndex = currentIndex + FUTURE_HASH_DISTANCE;
			Key nextLookForward = new Key(nextLookForwardIndex, this.possibleKey(nextLookForwardIndex));
			lookForward.add(nextLookForward);
			nextLookForward.quintupleChars.forEach(c -> lookForwardsByQuintuples.put(c, nextLookForward));
		}

		return output;
	}

	static boolean containsRepeatedChar(String input, char character, int repeatCount) {
		return IntStream.range(repeatCount - 1, input.length()) //
				.filter(i -> input.charAt(i) == character) //
				.anyMatch(i -> charIsPrecededByRepeats(input, i, repeatCount - 1));
	}

	static Optional<Character> firstRepeatedChar(String input, int repeatCount) {
		return repeatedChars(input, repeatCount).findFirst();
	}

	static Stream<Character> repeatedChars(String input, int repeatCount) {
		return IntStream.range(repeatCount - 1, input.length()) //
				.filter(i -> charIsPrecededByRepeats(input, i, repeatCount - 1)) //
				.mapToObj(i -> input.charAt(i));
	}

	private static boolean charIsPrecededByRepeats(String input, int index, int repeatCount) {
		return IntStream.range(index - repeatCount, index) //
				.map(i -> input.charAt(i)) //
				.allMatch(c -> c == input.charAt(index));
	}

	public static class Key {
		public final int index;
		public final String keyString;
		public final Optional<Character> firstTriplet;
		public final Set<Character> quintupleChars;

		public Key(int index, String keyString) {
			this.index = index;
			this.keyString = keyString;
			this.firstTriplet = firstRepeatedChar(keyString, 3);
			this.quintupleChars = repeatedChars(keyString, 5).collect(toSet());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.index;
			result = prime * result + ((this.keyString == null) ? 0 : this.keyString.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			Key other = (Key) obj;
			if (this.index != other.index) {
				return false;
			}
			if (this.keyString == null) {
				if (other.keyString != null) {
					return false;
				}
			} else if (!this.keyString.equals(other.keyString)) {
				return false;
			}
			return true;
		}

	}

	public static void main(String[] args) {
		String input = "cuanljph";

		{
			OneTimePadKeyStream stream = OneTimePadKeyStream.unstretched(input);
			List<Key> keys = stream.keys(64);
			System.out.println(keys.get(keys.size() - 1).index);
		}

		{
			OneTimePadKeyStream stretched = OneTimePadKeyStream.stretched(input);
			List<Key> stretchedKeys = stretched.keys(64);
			System.out.println(stretchedKeys.get(stretchedKeys.size() - 1).index);
		}
	}

}