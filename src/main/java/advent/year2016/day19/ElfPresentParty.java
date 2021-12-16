package advent.year2016.day19;

import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;

/**
 * --- Day 19: An Elephant Named Joseph ---
 * The Elves contact you over a highly secure emergency channel. Back at the North Pole, the Elves are busy
 * misunderstanding White Elephant parties.
 * Each Elf brings a present. They all sit in a circle, numbered starting with position 1. Then, starting with the
 * first Elf, they take turns stealing all the presents from the Elf to their left.  An Elf with no presents is removed
 * from the circle and does not take turns.
 * For example, with five Elves (numbered 1 to 5):
 *   1
 * 5   2
 *  4 3
 * 
 * 
 * Elf 1 takes Elf 2's present.
 * Elf 2 has no presents and is skipped.
 * Elf 3 takes Elf 4's present.
 * Elf 4 has no presents and is also skipped.
 * Elf 5 takes Elf 1's two presents.
 * Neither Elf 1 nor Elf 2 have any presents, so both are skipped.
 * Elf 3 takes Elf 5's three presents.
 * 
 * So, with five Elves, the Elf that sits starting in position 3 gets all the presents.
 * With the number of Elves given in your puzzle input, which Elf gets all the presents?
 * 
 * --- Part Two ---
 * Realizing the folly of their present-exchange rules, the Elves agree to instead steal presents from the Elf directly
 * across the circle. If two Elves are across the circle, the one on the left (from the perspective of the stealer) is
 * stolen from.  The other rules remain unchanged: Elves with no presents are removed from the circle entirely, and the
 * other elves move in slightly to keep the circle evenly spaced.
 * For example, with five Elves (again numbered 1 to 5):
 * 
 * The Elves sit in a circle; Elf 1 goes first:
 *   1
 * 5   2
 *  4 3
 * 
 * Elves 3 and 4 are across the circle; Elf 3's present is stolen, being the one to the left. Elf 3 leaves the circle,
 * and the rest of the Elves move in:
 *   1           1
 * 5   2  -->  5   2
 *  4 -          4
 * 
 * Elf 2 steals from the Elf directly across the circle, Elf 5:
 *   1         1 
 * -   2  -->     2
 *   4         4 
 * 
 * Next is Elf 4 who, choosing between Elves 1 and 2, steals from Elf 1:
 *  -          2  
 *     2  -->
 *  4          4
 * 
 * Finally, Elf 2 steals from Elf 4:
 *  2
 *     -->  2  
 *  -
 * 
 * 
 * So, with five Elves, the Elf that sits starting in position 2 gets all the presents.
 * With the number of Elves given in your puzzle input, which Elf now gets all the presents?
 * 
 */
public class ElfPresentParty {

	final int elfCount;
	private final StealingStrategy stealingStrategy;

	private ElfPresentParty(int elfCount, StealingStrategy stealingStrategy) {
		this.elfCount = elfCount;
		this.stealingStrategy = stealingStrategy;
	}

	public static ElfPresentParty stealingFromTheLeft(int elfCount) {
		return new ElfPresentParty(elfCount, (current, across) -> current.next);
	}

	public static ElfPresentParty stealingAcross(int elfCount) {
		return new ElfPresentParty(elfCount, (current, across) -> across);

	}

	public int elfThatGetsAllThePresents() {

		PartyElf currentElf = linkedElves(elfCount);

		// We have to keep a pointer to the elf across the table for performance
		// reasons - if we try to calculate them independently every time, we
		// are O(n^2)ing because each lookup in our linked list is O(n) as we
		// step forward n/2 times.
		// If we use an ArrayList instead to make these lookups fast, then the
		// remove action is O(n) and we are back in O(n^2) land for our whole
		// procedure.
		PartyElf acrossElf = currentElf.next(elfCount / 2);

		PartyElf result = currentElf;
		int elvesRemaining = elfCount;

		while (elvesRemaining > 1) {

			result = currentElf;

			PartyElf stealTarget = stealingStrategy.whoToStealFrom(currentElf, acrossElf);

			// Debug info.
			// System.out.println(currentElf.number + " takes " +
			// stealTarget.number
			// + "'s presents");

			stealTarget.remove();
			elvesRemaining--;

			currentElf = currentElf.next;

			if (elvesRemaining % 2 == 0) {
				// Suppose we started with (e.g.) 5 elves, starting with elf 1.
				// Then we removed elf 3. Our next elf will be elf 2, and we
				// want to move the "across" pointer so that it is pointing at
				// elf 5 - we move ahead twice.
				acrossElf = acrossElf.next.next;
			} else {
				// If we started with (e.g.) 6 elves, starting with elf 1, we
				// removed elf 4. Our next elf is 2, and we want the pointer to
				// be at elf 5 - ahead only once.
				acrossElf = acrossElf.next;
			}
		}

		return result.number;
	}

	/**
	 * We need to do some speed-up optimization for the second part of the
	 * problem. Removing from an ArrayList is giving us too slow performance;
	 * the interface of LinkedList is giving us bad performance because we have
	 * to get(index) to get through it. Instead we are going to build our own
	 * doubly linked list and use that.
	 * 
	 * This returns the first elf in a set of a provided number, all properly
	 * linked together and numbered.
	 */
	static PartyElf linkedElves(int elfCount) {
		PartyElf firstElf = new PartyElf(1);
		PartyElf previousElf = firstElf;

		for (int i = 2; i <= elfCount; i++) {
			PartyElf elf = new PartyElf(i);
			elf.previous = previousElf;
			previousElf.next = elf;

			if (i == elfCount) {
				firstElf.previous = elf;
				elf.next = firstElf;
			}

			previousElf = elf;
		}

		return firstElf;
	}

	@FunctionalInterface
	private static interface StealingStrategy {
		public PartyElf whoToStealFrom(PartyElf currentElf, PartyElf acrossElf);
	}

	@VisibleForTesting
	static class PartyElf {
		public final int number;
		private PartyElf previous;
		private PartyElf next;

		public PartyElf(int number) {
			this.number = number;
		}

		public PartyElf previous() {
			return previous;
		}

		public PartyElf next() {
			return next;
		}

		public PartyElf next(int steps) {
			return Stream.iterate(this, PartyElf::next) //
					.limit(steps + 1) //
					.reduce((l, r) -> r) //
					.get();
		}

		public void remove() {
			this.previous.next = next;
			this.next.previous = previous;
		}
	}

	public static void main(String[] args) {
		int elfCount = 3004953;
		ElfPresentParty left = ElfPresentParty.stealingFromTheLeft(elfCount);
		System.out.println(left.elfThatGetsAllThePresents());
		ElfPresentParty across = ElfPresentParty.stealingAcross(elfCount);
		System.out.println(across.elfThatGetsAllThePresents());
	}

}