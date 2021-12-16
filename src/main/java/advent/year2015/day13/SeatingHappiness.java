package advent.year2015.day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

/**
 * --- Day 13: Knights of the Dinner Table ---
 * In years past, the holiday feast with your family hasn't gone so well.  Not everyone gets along!  This year, you
 * resolve, will be different.  You're going to find the optimal seating arrangement and avoid all those awkward
 * conversations.
 * You start by writing up a list of everyone invited and the amount their happiness would increase or decrease if they
 * were to find themselves sitting next to each other person.  You have a circular table that will be just big enough
 * to fit everyone comfortably, and so each person will have exactly two neighbors.
 * For example, suppose you have only four attendees planned, and you calculate their potential happiness as follows:
 * Alice would gain 54 happiness units by sitting next to Bob.
 * Alice would lose 79 happiness units by sitting next to Carol.
 * Alice would lose 2 happiness units by sitting next to David.
 * Bob would gain 83 happiness units by sitting next to Alice.
 * Bob would lose 7 happiness units by sitting next to Carol.
 * Bob would lose 63 happiness units by sitting next to David.
 * Carol would lose 62 happiness units by sitting next to Alice.
 * Carol would gain 60 happiness units by sitting next to Bob.
 * Carol would gain 55 happiness units by sitting next to David.
 * David would gain 46 happiness units by sitting next to Alice.
 * David would lose 7 happiness units by sitting next to Bob.
 * David would gain 41 happiness units by sitting next to Carol.
 * 
 * Then, if you seat Alice next to David, Alice would lose 2 happiness units (because David talks so much), but David
 * would gain 46 happiness units (because Alice is such a good listener), for a total change of 44.
 * If you continue around the table, you could then seat Bob next to Alice (Bob gains 83, Alice gains 54).  Finally,
 * seat Carol, who sits next to Bob (Carol gains 60, Bob loses 7) and David (Carol gains 55, David gains 41).  The
 * arrangement looks like this:
 *      +41 +46
 * +55   David    -2
 * Carol       Alice
 * +60    Bob    +54
 *      -7  +83
 * 
 * After trying every other seating arrangement in this hypothetical scenario, you find that this one is the most
 * optimal, with a total change in happiness of 330.
 * What is the total change in happiness for the optimal seating arrangement of the actual guest list?
 * 
 * --- Part Two ---
 * In all the commotion, you realize that you forgot to seat yourself.  At this point, you're pretty apathetic toward
 * the whole thing, and your happiness wouldn't really go up or down regardless of who you sit next to.  You assume
 * everyone else would be just as ambivalent about sitting next to you, too.
 * So, add yourself to the list, and give all happiness relationships that involve you a score of 0.
 * What is the total change in happiness for the optimal seating arrangement that actually includes yourself?
 * 
 */
public class SeatingHappiness {
	private Map<String, Map<String, Integer>> preferences = new HashMap<>();

	public void addPreference(String representation) {
		Preference preference = new Preference(representation);
		this.addPreference(preference.from, preference.to, preference.happiness);
	}

	private void addPreference(String from, String to, int happiness) {
		Map<String, Integer> forFromPoint = this.preferences.computeIfAbsent(from, key -> new HashMap<>());
		forFromPoint.put(to, happiness);
	}

	public void addMe() {
		String me = "it's a me, mario";

		for (String otherPerson : ImmutableSet.copyOf(this.preferences.keySet())) {
			this.addPreference(me, otherPerson, 0);
			this.addPreference(otherPerson, me, 0);
		}
	}

	private IntStream happinessOptions() {
		return Collections2.permutations(this.preferences.keySet()) //
				.stream() //
				.mapToInt(this::totalHappiness);
	}

	public int optimalHappiness() {
		return this.happinessOptions().max().getAsInt();
	}

	private int happinessForAdjacency(String from, String to) {
		try {
			return this.preferences.get(from).get(to) + this.preferences.get(to).get(from);
		} catch (NullPointerException e) {
			throw new NoSuchElementException("Missing preference " + from + " to " + to);
		}
	}

	private int totalHappiness(List<String> seatOrdering) {
		int happiness = 0;

		for (int i = 0; i < seatOrdering.size() - 1; i++) {
			happiness += this.happinessForAdjacency(seatOrdering.get(i), seatOrdering.get(i + 1));
		}

		happiness += this.happinessForAdjacency(seatOrdering.get(0), seatOrdering.get(seatOrdering.size() - 1));

		return happiness;
	}

	private static class Preference {
		private final String from;
		private final String to;
		private final int happiness;

		private static final Pattern REGEX = Pattern
				.compile("(\\w+) would (\\w+) (\\d+) happiness units by sitting next to (\\w+).");

		public Preference(String representation) {
			Matcher matcher = REGEX.matcher(representation);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(representation);
			}

			this.from = matcher.group(1);

			String change = matcher.group(2);

			int absValue = Integer.valueOf(matcher.group(3));

			if (change.equals("gain")) {
				this.happiness = absValue;
			} else if (change.equals("lose")) {
				this.happiness = -absValue;
			} else {
				throw new IllegalArgumentException("Bad verb " + change);
			}

			this.to = matcher.group(4);
		}
	}

	private static SeatingHappiness seatingFromFile() throws IOException {
		SeatingHappiness seating = new SeatingHappiness();
		Files.lines(Paths.get("src/main/java/advent/year2015/day13/input.txt")) //
				.forEach(seating::addPreference);
		return seating;
	}

	public static void main(String[] args) throws IOException {
		SeatingHappiness seating = seatingFromFile();
		seating.addMe();
		System.out.println(seating.optimalHappiness());
	}

}