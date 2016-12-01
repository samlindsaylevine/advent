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
		Files.lines(Paths.get("src/main/java/advent/day13/input.txt")) //
				.forEach(seating::addPreference);
		return seating;
	}

	public static void main(String[] args) throws IOException {
		SeatingHappiness seating = seatingFromFile();
		seating.addMe();
		System.out.println(seating.optimalHappiness());
	}

}
