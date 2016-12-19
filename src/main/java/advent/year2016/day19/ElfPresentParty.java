package advent.year2016.day19;

import static advent.utils.CollectorUtils.toArrayList;

import java.util.List;
import java.util.stream.IntStream;

import com.google.common.annotations.VisibleForTesting;

public class ElfPresentParty {

	final int elfCount;

	public ElfPresentParty(int elfCount) {
		this.elfCount = elfCount;
	}

	public int elfThatGetsAllThePresents() {
		List<Boolean> stillHasPresents = IntStream.range(0, elfCount) //
				.mapToObj(i -> true) //
				.collect(toArrayList());

		int currentIndex = 0;

		// We keep track of number of elves remaining as a counter so that we
		// don't have to check the whole list every time.
		for (int elvesRemaining = elfCount; elvesRemaining > 1; elvesRemaining--) {
			int stolenFrom = nextTrueValue(stillHasPresents, currentIndex);
			stillHasPresents.set(stolenFrom, false);
			// Debug info.
			// System.out.println((currentIndex + 1) + " takes " + (stolenFrom +
			// 1) + "'s presents");
			currentIndex = nextTrueValue(stillHasPresents, currentIndex);
		}

		return currentIndex + 1;
	}

	/**
	 * Finds the index of the next true value in the list, cycling to the front
	 * if one is not found in the remainder of the list.
	 */
	@VisibleForTesting
	static int nextTrueValue(List<Boolean> list, int currentIndex) {
		return IntStream.range(currentIndex + 1, list.size()) //
				.filter(i -> list.get(i)) //
				.findFirst() //
				.orElseGet(() -> nextTrueValue(list, -1));
	}

	public static void main(String[] args) {
		ElfPresentParty party = new ElfPresentParty(3004953);
		System.out.println(party.elfThatGetsAllThePresents());
	}

}
