package advent.year2015.day14;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * --- Day 14: Reindeer Olympics ---
 * This year is the Reindeer Olympics!  Reindeer can fly at high speeds, but must rest occasionally to recover their
 * energy.  Santa would like to know which of his reindeer is fastest, and so he has them race.
 * Reindeer can only either be flying (always at their top speed) or resting (not moving at all), and always spend
 * whole seconds in either state.
 * For example, suppose you have the following Reindeer:
 * 
 * Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
 * Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.
 * 
 * After one second, Comet has gone 14 km, while Dancer has gone 16 km.  After ten seconds, Comet has gone 140 km,
 * while Dancer has gone 160 km.  On the eleventh second, Comet begins resting (staying at 140 km), and Dancer
 * continues on for a total distance of 176 km.  On the 12th second, both reindeer are resting.  They continue to rest
 * until the 138th second, when Comet flies for another ten seconds.  On the 174th second, Dancer flies for another 11
 * seconds.
 * In this example, after the 1000th second, both reindeer are resting, and Comet is in the lead at 1120 km (poor
 * Dancer has only gotten 1056 km by that point).  So, in this situation, Comet would win (if the race ended at 1000
 * seconds).
 * Given the descriptions of each reindeer (in your puzzle input), after exactly 2503 seconds, what distance has the
 * winning reindeer traveled?
 * 
 * --- Part Two ---
 * Seeing how reindeer move in bursts, Santa decides he's not pleased with the old scoring system.
 * Instead, at the end of each second, he awards one point to the reindeer currently in the lead.  (If there are
 * multiple reindeer tied for the lead, they each get one point.)  He keeps the traditional 2503 second time limit, of
 * course, as doing otherwise would be entirely ridiculous.
 * Given the example reindeer from above, after the first second, Dancer is in the lead and gets one point.  He stays
 * in the lead until several seconds into Comet's second burst: after the 140th second, Comet pulls into the lead and
 * gets his first point.  Of course, since Dancer had been in the lead for the 139 seconds before that, he has
 * accumulated 139 points by the 140th second.
 * After the 1000th second, Dancer has accumulated 689 points, while poor Comet, our old champion, only has 312.  So,
 * with the new scoring system, Dancer would win (if the race ended at 1000 seconds).
 * Again given the descriptions of each reindeer (in your puzzle input), after exactly 2503 seconds, how many points
 * does the winning reindeer have?
 * 
 */
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
		return Files.lines(Paths.get("src/main/java/advent/year2015/day14/input.txt")) //
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