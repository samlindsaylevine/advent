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

public class OneTimePadKeyStream {

	private static final int FUTURE_HASH_DISTANCE = 1000;

	private final String salt;

	public OneTimePadKeyStream(String salt) {
		this.salt = salt;
	}

	public String possibleKey(int index) {
		return MD5Password.hexMD5Hash(this.salt + index);
	}

	public List<Key> keys(int keyCount) {
		List<Key> output = new ArrayList<>();
		int currentIndex = 0;

		// Keeping two data structures for lookup speed.
		LinkedList<Key> lookForward = IntStream.range(1, 1 + FUTURE_HASH_DISTANCE) //
				.mapToObj(i -> new Key(i, this.possibleKey(i))) //
				.collect(toLinkedList());
		Multimap<Character, Key> lookForwardsByQuintuples = lookForward.stream() //
				.collect(flatMappingToMultimap(key -> key.quintupleChars.stream(), i -> i));

		while (output.size() < keyCount) {

			String possibleKey = this.possibleKey(currentIndex);

			int i = currentIndex;
			firstRepeatedChar(possibleKey, 3).ifPresent(repeated -> {
				if (lookForwardsByQuintuples.containsKey(repeated)) {
					output.add(new Key(i, possibleKey));
				}
			});

			currentIndex++;

			int nextLookForwardIndex = currentIndex + FUTURE_HASH_DISTANCE;
			Key nextLookForward = new Key(nextLookForwardIndex, this.possibleKey(nextLookForwardIndex));
			lookForward.add(nextLookForward);
			nextLookForward.quintupleChars.forEach(c -> lookForwardsByQuintuples.put(c, nextLookForward));

			Key previousLookForward = lookForward.removeFirst();
			previousLookForward.quintupleChars.forEach(c -> lookForwardsByQuintuples.remove(c, previousLookForward));
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
		OneTimePadKeyStream stream = new OneTimePadKeyStream("cuanljph");
		List<Key> keys = stream.keys(64);
		System.out.println(keys.get(keys.size() - 1).index);
	}

}
