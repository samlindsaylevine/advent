package advent.year2016.day15;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

public class DiscSculpture {

	private final List<Disc> discs;

	public DiscSculpture(Stream<String> lines) {
		this(lines.map(Disc::new).collect(toList()));
	}

	private DiscSculpture(List<Disc> discs) {
		this.discs = discs;
	}

	public DiscSculpture withAdditionalDisc(Disc disc) {
		return new DiscSculpture(Stream.concat(this.discs.stream(), Stream.of(disc)).collect(toList()));
	}

	public boolean capsulePasses(int dropTime) {
		return IntStream.range(0, this.discs.size()) //
				.parallel() //
				.allMatch(i -> this.discs.get(i).isOpenAt(dropTime + i + 1));
	}

	public int firstSuccessfulDropTime() {
		// Naive implementation - try 'em.
		// Probably some elegant way we could do this symbolically with linear
		// algebra / modular arithmetic instead? But the naive implementation
		// runs fast enough for our problem.
		return IntStream.iterate(0, i -> i + 1) //
				.parallel() //
				.filter(this::capsulePasses) //
				.findFirst() //
				.getAsInt();
	}

	private static class Disc {

		private static final Pattern PATTERN = Pattern
				.compile("Disc #\\d+ has (\\d+) positions; at time=0, it is at position (\\d+).");

		private final int positionCount;
		private final int initialPosition;

		public Disc(String representation) {
			Matcher matcher = PATTERN.matcher(representation);
			Preconditions.checkArgument(matcher.matches(), "Bad disc %s", representation);
			this.positionCount = Integer.valueOf(matcher.group(1));
			this.initialPosition = Integer.valueOf(matcher.group(2));
		}

		public Disc(int positionCount, int initialPosition) {
			this.positionCount = positionCount;
			this.initialPosition = initialPosition;
		}

		public boolean isOpenAt(int time) {
			return (this.initialPosition + time) % this.positionCount == 0;
		}
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day15/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			DiscSculpture sculpture = new DiscSculpture(lines);
			System.out.println(sculpture.firstSuccessfulDropTime());

			Disc additionalDisc = new Disc(11, 0);
			DiscSculpture partTwo = sculpture.withAdditionalDisc(additionalDisc);
			System.out.println(partTwo.firstSuccessfulDropTime());
		}
	}
}
