package advent.day14;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Reindeer {

	private final int speedInKps;
	private final int enduranceInSeconds;
	private final int restPeriodInSeconds;

	private int points = 0;

	private static final Pattern REGEX = Pattern
			.compile("\\w+ can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds.");

	public Reindeer(String representation) {
		Matcher matcher = REGEX.matcher(representation);

		if (!matcher.matches()) {
			throw new IllegalArgumentException(representation);
		}

		this.speedInKps = Integer.valueOf(matcher.group(1));
		this.enduranceInSeconds = Integer.valueOf(matcher.group(2));
		this.restPeriodInSeconds = Integer.valueOf(matcher.group(3));
	}

	private int period() {
		return this.enduranceInSeconds + this.restPeriodInSeconds;
	}

	public int distanceTraveled(int elapsed) {
		int fullPeriods = elapsed / this.period();
		int partialPeriodTime = elapsed - fullPeriods * this.period();

		int fullPeriodDistance = fullPeriods * this.speedInKps * this.enduranceInSeconds;
		int partialPeriodDistance = this.speedInKps * Math.min(partialPeriodTime, this.enduranceInSeconds);

		return fullPeriodDistance + partialPeriodDistance;
	}

	private static Stream<Reindeer> reindeerFromFile() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/day14/input.txt")) //
				.map(Reindeer::new);
	}

	public static int maxDistance() throws IOException {
		int duration = 2503;
		return reindeerFromFile() //
				.mapToInt(r -> r.distanceTraveled(duration)) //
				.max().getAsInt();
	}

	public static int maxPoints() throws IOException {
		int duration = 2503;
		List<Reindeer> reindeer = reindeerFromFile().collect(toList());

		for (int t = 1; t <= duration; t++) {
			int currentTime = t;
			Reindeer leader = reindeer.stream().max(Comparator.comparing(r -> r.distanceTraveled(currentTime))).get();
			leader.points++;
		}

		return reindeer.stream().mapToInt(r -> r.points).max().getAsInt();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(maxPoints());
	}

}
