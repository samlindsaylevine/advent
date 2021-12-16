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

/**
 * --- Day 20: Firewall Rules ---
 * You'd like to set up a small hidden computer here so you can use it to get back into the network later. However, the
 * corporate firewall only allows communication with certain external IP addresses.
 * You've retrieved the list of blocked IPs from the firewall, but the list seems to be messy and poorly maintained,
 * and it's not clear which IPs are allowed. Also, rather than being written in dot-decimal notation, they are written
 * as plain 32-bit integers, which can have any value from 0 through 4294967295, inclusive.
 * For example, suppose only the values 0 through 9 were valid, and that you retrieved the following blacklist:
 * 5-8
 * 0-2
 * 4-7
 * 
 * The blacklist specifies ranges of IPs (inclusive of both the start and end value) that are not allowed. Then, the
 * only IPs that this firewall allows are 3 and 9, since those are the only numbers not in any range.
 * Given the list of blocked IPs you retrieved from the firewall (your puzzle input), what is the lowest-valued IP that
 * is not blocked?
 * 
 * --- Part Two ---
 * How many IPs are allowed by the blacklist?
 * 
 */
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
		// Canonicalize it over its appropriate domain, so that we can know that
		// the ranges "0-1" and "2-4" are contiguous and can be merged together.
		// This also puts all the ranges into [closed, open) format for
		// consistency.
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