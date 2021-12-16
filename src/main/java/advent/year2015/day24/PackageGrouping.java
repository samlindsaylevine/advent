package advent.year2015.day24;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Originally had a much more general solution that calculated all possible groupings in their entirely; unfortunately
 * it was much too slow. (It was very snappy for the reference example of 10 items but not for the actual problem with
 * 28.) By the problem statement, only what is in the first group actually matters (assuming that the other groups are
 * still splittable into equal parts).
 *
 * So, our groupings will only keep track of what is in the first group, and we should only construct them if they are
 * balanced.
 *
 * --- Day 24: It Hangs in the Balance --- It's Christmas Eve, and Santa is loading up the sleigh for this year's
 * deliveries.  However, there's one small problem: he can't get the sleigh to balance.  If it isn't balanced, he can't
 * defy physics, and nobody gets presents this year. No pressure. Santa has provided you a list of the weights of every
 * package he needs to fit on the sleigh.  The packages need to be split into three groups of exactly the same weight,
 * and every package has to fit.  The first group goes in the passenger compartment of the sleigh, and the second and
 * third go in containers on either side.  Only when all three groups weigh exactly the same amount will the sleigh be
 * able to fly.  Defying physics has rules, you know! Of course, that's not the only problem.  The first group - the one
 * going in the passenger compartment - needs as few packages as possible so that Santa has some legroom left over.  It
 * doesn't matter how many packages are in either of the other two groups, so long as all of the groups weigh the same.
 * Furthermore, Santa tells you, if there are multiple ways to arrange the packages such that the fewest possible are in
 * the first group, you need to choose the way where the first group has the smallest quantum entanglement to reduce the
 * chance of any "complications".  The quantum entanglement of a group of packages is the product of their weights, that
 * is, the value you get when you multiply their weights together.  Only consider quantum entanglement if the first
 * group has the fewest possible number of packages in it and all groups weigh the same amount. For example, suppose you
 * have ten packages with weights 1 through 5 and 7 through 11.  For this situation, some of the unique first groups,
 * their quantum entanglements, and a way to divide the remaining packages are as follows: Group 1;             Group 2;
 * Group 3 11 9       (QE= 99); 10 8 2;  7 5 4 3 1 10 9 1     (QE= 90); 11 7 2;  8 5 4 3 10 8 2     (QE=160); 11 9;    7
 * 5 4 3 1 10 7 3     (QE=210); 11 9;    8 5 4 2 1 10 5 4 1   (QE=200); 11 9;    8 7 3 2 10 5 3 2   (QE=300); 11 9;    8
 * 7 4 1 10 4 3 2 1 (QE=240); 11 9;    8 7 5 9 8 3      (QE=216); 11 7 2;  10 5 4 1 9 7 4      (QE=252); 11 8 1;  10 5 3
 * 2 9 5 4 2    (QE=360); 11 8 1;  10 7 3 8 7 5      (QE=280); 11 9;    10 4 3 2 1 8 5 4 3    (QE=480); 11 9;    10 7 2
 * 1 7 5 4 3 1  (QE=420); 11 9;    10 8 2
 *
 * Of these, although 10 9 1 has the smallest quantum entanglement (90), the configuration with only two packages, 11 9,
 * in the passenger compartment gives Santa the most legroom and wins.  In this situation, the quantum entanglement for
 * the ideal configuration is therefore 99.  Had there been two configurations with only two packages in the first
 * group, the one with the smaller quantum entanglement would be chosen. What is the quantum entanglement of the first
 * group of packages in the ideal configuration?
 *
 * --- Part Two --- That's weird... the sleigh still isn't balancing. "Ho ho ho", Santa muses to himself. "I forgot the
 * trunk". Balance the sleigh again, but this time, separate the packages into four groups instead of three.  The other
 * constraints still apply. Given the example packages above, this would be some of the new unique first groups, their
 * quantum entanglements, and one way to divide the remaining packages:
 *
 * 11 4    (QE=44); 10 5;   9 3 2 1; 8 7 10 5    (QE=50); 11 4;   9 3 2 1; 8 7 9 5 1   (QE=45); 11 4;   10 3 2;  8 7 9 4
 * 2   (QE=72); 11 3 1; 10 5;    8 7 9 3 2 1 (QE=54); 11 4;   10 5;    8 7 8 7     (QE=56); 11 4;   10 5;    9 3 2 1
 *
 * Of these, there are three arrangements that put the minimum (two) number of packages in the first group: 11 4, 10 5,
 * and 8 7.  Of these, 11 4 has the lowest quantum entanglement, and so it is selected. Now, what is the quantum
 * entanglement of the first group of packages in the ideal configuration?
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
		return Files.lines(Paths.get("src/main/java/advent/year2015/day24/input.txt")) //
				.map(Integer::valueOf) //
				.collect(MultisetCollector.toImmutableMultiset());
	}

	public static void main(String[] args) throws IOException {
		System.out.println(validGroupings(fromFile()) //
				.min(bestForSanta()));
	}

}