package advent.year2016.day20;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

public class IPRules {

	private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)-(\\d+)");

	private final long maxValue;
	private final Set<Range<Long>> blockedRanges;

	public IPRules(long maxValue, Stream<String> blacklist) {
		this.maxValue = maxValue;
		this.blockedRanges = blacklist.map(IPRules::toRange).collect(toSet());
	}

	private static Range<Long> toRange(String rangeStr) {
		Matcher matcher = RANGE_PATTERN.matcher(rangeStr);
		Preconditions.checkArgument(matcher.matches(), "Bad range string %s", rangeStr);
		long lower = Long.valueOf(matcher.group(1));
		long upper = Long.valueOf(matcher.group(2));
		return Range.closed(lower, upper);
	}

	private boolean isValid(long ip) {
		return !blockedRanges.parallelStream() //
				.anyMatch(range -> range.contains(ip));
	}

	// This approach is far too slow.
	// public OptionalLong naiveLowestValid() {
	// return LongStream.range(0, maxValue) //
	// .filter(this::isValid) //
	// .findFirst();
	// }

	public Optional<Long> lowestValid() {
		if (blockedRanges.isEmpty()) {
			return Optional.of(0L);
		}

		// We'll test each point that is one below a lower bound of one of our
		// blocked ranges, and see if it is valid.
		List<Long> possiblyValid = blockedRanges.stream() //
				.map(Range::lowerEndpoint) //
				.map(i -> i - 1) //
				.filter(i -> i >= 0 && i <= maxValue).sorted() //
				.collect(toList());

		return possiblyValid.stream() //
				.filter(this::isValid) //
				.findFirst();
	}

	public static void main(String[] args) throws IOException {
		long maxValue = 4294967295L;

		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day20/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			IPRules rules = new IPRules(maxValue, lines);
			System.out.println(rules.lowestValid());
		}

	}

}
