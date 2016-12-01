package advent.year2015.day24;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

public class PackageGroupingTest {

	@Test
	public void powerSet() {
		Multiset<Integer> options = ImmutableMultiset.of(1, 2, 3, 3, 4, 5);
		List<Multiset<Integer>> powerSet = PackageGrouping.powerSet(options).collect(toList());

		assertEquals(64, powerSet.size());
	}

	@Test
	public void powerSetIsLazy() {
		AtomicInteger numSeen = new AtomicInteger(0);

		List<Integer> options = IntStream.of(1, 10000).boxed().collect(toList());
		Stream<Multiset<Integer>> powerSet = PackageGrouping.powerSet(options);
		powerSet.peek(item -> numSeen.incrementAndGet()) //
				.findAny();

		// Not completely sure why this is 2 and not 1, but I am sure that we
		// didn't go through all 2^10000, so I'm happy.
		assertEquals(2, numSeen.get());
	}

	@Test
	public void reference() {
		Multiset<Integer> packages = ImmutableMultiset.of(1, 2, 3, 4, 5, 7, 8, 9, 10, 11);

		long bestResult = PackageGrouping.validGroupings(packages) //
				.min(PackageGrouping.bestForSanta()) //
				.get() //
				.quantumEntanglementInFirstGroup();

		assertEquals(99, bestResult);
	}

	@Test
	public void splittableEvenly() {
		// Trivial case.
		assertTrue(PackageGrouping.isSplittableEvenly(ImmutableList.of()));

		// Can't split one thing.
		assertFalse(PackageGrouping.isSplittableEvenly(ImmutableList.of(1)));

		// Can be split.
		assertTrue(PackageGrouping.isSplittableEvenly(ImmutableList.of(1, 2, 3, 4)));

		// Odd sum, not possible.
		assertFalse(PackageGrouping.isSplittableEvenly(ImmutableList.of(1, 2, 3, 4, 5)));

		// A few larger numbers.
		assertTrue(PackageGrouping.isSplittableEvenly(ImmutableList.of(1, 2, 3, 4, 5, 6, 19)));
	}

}
