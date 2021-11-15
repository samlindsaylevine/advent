package advent.year2016.day20;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

public class IPRulesTest {

	@Test
	public void reference() {
		int maxValue = 9;
		Stream<String> blacklist = Stream.of( //
				"5-8", //
				"0-2", //
				"4-7");

		IPRules rules = new IPRules(maxValue, blacklist);
		assertEquals(Optional.of(3L), rules.lowestValid());

		assertEquals(2L, rules.validCount());
	}

	@Test
	public void toMergedRangesDisjoint() {
		Stream<Range<Integer>> ranges = Stream.of( //
				Range.closed(1, 2), //
				Range.closed(4, 5), //
				Range.closed(7, 8));

		Set<Range<Integer>> expected = ImmutableSet.of(//
				Range.closed(1, 2), //
				Range.closed(4, 5), //
				Range.closed(7, 8));

		assertEquals(expected, ranges.collect(IPRules.toMergedRanges()));
	}

	@Test
	public void toMergedRangesSingleOverlap() {
		Stream<Range<Integer>> ranges = Stream.of( //
				Range.closed(1, 4), //
				Range.closed(4, 5), //
				Range.closed(7, 8));

		Set<Range<Integer>> expected = ImmutableSet.of(//
				Range.closed(1, 5), //
				Range.closed(7, 8));

		assertEquals(expected, ranges.collect(IPRules.toMergedRanges()));
	}

	@Test
	public void toMergedRangesAllOverlap() {
		Stream<Range<Integer>> ranges = Stream.of( //
				Range.closed(1, 4), //
				Range.closed(3, 7), //
				Range.closed(6, 8));

		Set<Range<Integer>> expected = ImmutableSet.of(//
				Range.closed(1, 8));

		assertEquals(expected, ranges.collect(IPRules.toMergedRanges()));
	}

}
