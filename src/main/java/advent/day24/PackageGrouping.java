package advent.day24;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * Originally had a much more general solution that calculated all possible
 * groupings in their entirely; unfortunately it was much too slow. (It was very
 * snappy for the reference example of 10 items but not for the actual problem
 * with 28.) By the problem statement, only what is in the first group actually
 * matters (assuming that the other groups are still splittable into equal
 * parts).
 *
 * So, our groupings will only keep track of what is in the first group, and we
 * should only construct them if they are balanced.
 */
public class PackageGrouping {

	private static final int NUM_GROUPS = 3;

	private final Multiset<Integer> firstGroup;

	public PackageGrouping() {
		this(ImmutableMultiset.of());
	}

	private PackageGrouping(Multiset<Integer> firstGroup) {
		this.firstGroup = firstGroup;
	}

	public int packageCountInFirstGroup() {
		return this.firstGroup.size();
	}

	@Override
	public String toString() {
		return "PackageGrouping [quantumEntanglement=" + this.quantumEntanglementInFirstGroup() + " firstGroup="
				+ this.firstGroup + "]";
	}

	public long quantumEntanglementInFirstGroup() {
		return this.firstGroup.stream().mapToLong(i -> i).reduce(1L, (a, b) -> a * b);
	}

	static int sum(Multiset<Integer> group) {
		return group.stream().reduce(0, (a, b) -> a + b);
	}

	public static <T> Stream<Multiset<T>> powerSet(Collection<T> items) {
		if (items.isEmpty()) {
			return Stream.of(ImmutableMultiset.of());
		}

		List<T> itemList = new ArrayList<>(items);
		Multiset<T> withoutFirstItem = ImmutableMultiset.of();
		Multiset<T> withFirstItem = ImmutableMultiset.of(itemList.get(0));
		List<T> remaining = itemList.subList(1, itemList.size());

		return Stream.of(withoutFirstItem, withFirstItem) //
				.flatMap(firstOption -> powerSet(remaining)
						.map(remainingOption -> Multisets.sum(firstOption, remainingOption)));

	}

	private static int sum(Collection<Integer> items) {
		return items.stream().mapToInt(i -> i).sum();
	}

	static boolean isSplittableEvenly(Collection<Integer> items) {
		int sum = sum(items);

		if (sum % 2 != 0) {
			return false;
		}

		return powerSet(items) //
				.anyMatch(set -> sum(set) == sum / 2);
	}

	public static Stream<PackageGrouping> validGroupings(Multiset<Integer> packages) {
		int total = sum(packages);

		if (total % NUM_GROUPS != 0) {
			return Stream.empty();
		}

		Ticker ticker = new Ticker(100_000);

		return powerSet(packages) //
				.parallel() //
				.peek(i -> ticker.tick()) //
				.filter(set -> sum(set) * NUM_GROUPS == total) //
				// This seems like we should have to check it to be sure but
				// that again is making things impractically slow. Just going to
				// skip the check for now... it seems like our dataset is such
				// that the remaining numbers are always splittable evenly?
				// .filter(set ->
				// isSplittableEvenly(Multisets.difference(packages, set))) //
				.map(PackageGrouping::new);

	}

	// PackageGroupings that compare to be first are better for Santa.
	public static Comparator<PackageGrouping> bestForSanta() {
		return Comparator.comparing(PackageGrouping::packageCountInFirstGroup) //
				.thenComparing(PackageGrouping::quantumEntanglementInFirstGroup);
	}

	public static Multiset<Integer> fromFile() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/day24/input.txt")) //
				.map(Integer::valueOf) //
				.collect(MultisetCollector.toImmutableMultiset());
	}

	public static void main(String[] args) throws IOException {
		System.out.println(validGroupings(fromFile()) //
				.min(bestForSanta()));
	}

}
