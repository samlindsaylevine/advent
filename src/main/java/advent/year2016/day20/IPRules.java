package advent.year2016.day20;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

public class IPRules {

	private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)-(\\d+)");

	private final long maxValue;
	/**
	 * Guaranteed to have no overlapping ranges and to be sorted in ascending
	 * order.
	 * 
	 * Because we canonicize them, they are also guaranteed to be [closed,
	 * open).
	 */
	private final List<Range<Long>> blockedRanges;

	public IPRules(long maxValue, Stream<String> blacklist) {
		this.maxValue = maxValue;
		this.blockedRanges = blacklist.map(IPRules::toRange) //
				.collect(toMergedRanges()) //
				.stream() //
				.sorted(comparing(Range::lowerEndpoint)) //
				.collect(toList());
	}

	private static Range<Long> toRange(String rangeStr) {
		Matcher matcher = RANGE_PATTERN.matcher(rangeStr);
		Preconditions.checkArgument(matcher.matches(), "Bad range string %s", rangeStr);
		long lower = Long.valueOf(matcher.group(1));
		long upper = Long.valueOf(matcher.group(2));
		return Range.closed(lower, upper).canonical(DiscreteDomain.longs());
	}

	@VisibleForTesting
	static <T extends Comparable<T>> Collector<Range<T>, ?, Set<Range<T>>> toMergedRanges() {
		return Collector.of(HashSet::new, IPRules::mergeRange, (l, r) -> {
			r.forEach(range -> mergeRange(l, range));
			return l;
		});
	}

	private static <T extends Comparable<T>> void mergeRange(Set<Range<T>> existing, Range<T> toAdd) {
		Set<Range<T>> overlaps = existing.stream() //
				.filter(toAdd::isConnected) //
				.collect(toSet());

		existing.removeAll(overlaps);

		Range<T> merged = overlaps.stream() //
				.reduce(toAdd, Range::span);

		existing.add(merged);
	}

	public Optional<Long> lowestValid() {

		if (blockedRanges.isEmpty()) {
			return Optional.of(0L);
		}

		Range<Long> first = this.blockedRanges.iterator().next();
		if (first.lowerEndpoint() > 0) {
			return Optional.of(0L);
		} else if (first.upperEndpoint() > maxValue) {
			return Optional.empty();
		} else {
			// If the blocked range is [0, 7) then 7 is allowed.
			return Optional.of(first.upperEndpoint());
		}
	}

	public long validCount() {
		long bannedCount = blockedRanges.parallelStream() //
				// e.g., the range [0, 2) blocks 2 values, 0 and 1.
				.mapToLong(r -> r.upperEndpoint() - r.lowerEndpoint()) //
				.sum();

		long totalNumbers = maxValue + 1;

		return totalNumbers - bannedCount;
	}

	public static void main(String[] args) throws IOException {
		long maxValue = 4294967295L;

		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day20/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			IPRules rules = new IPRules(maxValue, lines);
			System.out.println(rules.lowestValid());
			System.out.println(rules.validCount());
		}

	}

}
