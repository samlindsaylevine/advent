package advent.year2016.day19;

import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;

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
				acrossElf = acrossElf.next.next;
			} else {
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
