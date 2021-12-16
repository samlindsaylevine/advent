package advent.year2015.day16;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Splitter;

/**
 * --- Day 16: Aunt Sue ---
 * Your Aunt Sue has given you a wonderful gift, and you'd like to send her a thank you card.  However, there's a small
 * problem: she signed it "From, Aunt Sue".
 * You have 500 Aunts named "Sue".
 * So, to avoid sending the card to the wrong person, you need to figure out which Aunt Sue (which you conveniently
 * number 1 to 500, for sanity) gave you the gift.  You open the present and, as luck would have it, good ol' Aunt Sue
 * got you a My First Crime Scene Analysis Machine!  Just what you wanted.  Or needed, as the case may be.
 * The My First Crime Scene Analysis Machine (MFCSAM for short) can detect a few specific compounds in a given sample,
 * as well as how many distinct kinds of those compounds there are. According to the instructions, these are what the
 * MFCSAM can detect:
 * 
 * children, by human DNA age analysis.
 * cats.  It doesn't differentiate individual breeds.
 * Several seemingly random breeds of dog: samoyeds, pomeranians, akitas, and vizslas.
 * goldfish.  No other kinds of fish.
 * trees, all in one group.
 * cars, presumably by exhaust or gasoline or something.
 * perfumes, which is handy, since many of your Aunts Sue wear a few kinds.
 * 
 * In fact, many of your Aunts Sue have many of these.  You put the wrapping from the gift into the MFCSAM.  It beeps
 * inquisitively at you a few times and then prints out a message on ticker tape:
 * children: 3
 * cats: 7
 * samoyeds: 2
 * pomeranians: 3
 * akitas: 0
 * vizslas: 0
 * goldfish: 5
 * trees: 3
 * cars: 2
 * perfumes: 1
 * 
 * You make a list of the things you can remember about each Aunt Sue.  Things missing from your list aren't zero - you
 * simply don't remember the value.
 * What is the number of the Sue that got you the gift?
 * 
 * --- Part Two ---
 * As you're about to send the thank you note, something in the MFCSAM's instructions catches your eye.  Apparently, it
 * has an outdated retroencabulator, and so the output from the machine isn't exact values - some of them indicate
 * ranges.
 * In particular, the cats and trees readings indicates that there are greater than that many (due to the unpredictable
 * nuclear decay of cat dander and tree pollen), while the pomeranians and goldfish readings indicate that there are
 * fewer than that many (due to the modial interaction of magnetoreluctance).
 * What is the number of the real Aunt Sue?
 * 
 */
public class Aunt {

	private final String name;

	private final Map<String, Integer> knownPossessions;

	public Aunt(String representation) {
		String[] parts = representation.split(":", 2);

		this.name = parts[0];
		String possessionString = parts[1].trim();

		this.knownPossessions = Splitter.on(", ") //
				.withKeyValueSeparator(": ") //
				.split(possessionString) //
				.entrySet() //
				.stream() //
				.collect(toMap(Map.Entry::getKey, e -> Integer.valueOf(e.getValue())));
	}

	public String getName() {
		return this.name;
	}

	public boolean consistentWithValues(Map<String, Integer> possessions) {
		Map<String, Predicate<Integer>> predicates = possessions.entrySet() //
				.stream() //
				.collect(toMap(e -> e.getKey(), e -> (i -> i == e.getValue())));

		return this.consistentWith(predicates);
	}

	public boolean consistentWith(Map<String, Predicate<Integer>> possessions) {
		return possessions.entrySet() //
				.stream() //
				.allMatch(entry -> this.consistentWith(entry.getKey(), entry.getValue()));
	}

	private boolean consistentWith(String possession, Predicate<Integer> test) {
		return !this.knownPossessions.containsKey(possession) || test.test(this.knownPossessions.get(possession));
	}

	public static Map<String, Integer> mfcsamResult() {
		Map<String, Integer> output = new HashMap<>();

		output.put("children", 3);
		output.put("cats", 7);
		output.put("samoyeds", 2);
		output.put("pomeranians", 3);
		output.put("akitas", 0);
		output.put("vizslas", 0);
		output.put("goldfish", 5);
		output.put("trees", 3);
		output.put("cars", 2);
		output.put("perfumes", 1);

		return output;
	}

	public static Map<String, Predicate<Integer>> retroencabulatedMfcsamResult() {
		Map<String, Predicate<Integer>> output = new HashMap<>();

		output.put("cats", i -> i > 7);
		output.put("trees", i -> i > 3);

		output.put("pomeranians", i -> i < 3);
		output.put("goldfish", i -> i < 5);

		output.put("children", i -> i == 3);
		output.put("samoyeds", i -> i == 2);
		output.put("akitas", i -> i == 0);
		output.put("vizslas", i -> i == 0);
		output.put("cars", i -> i == 2);
		output.put("perfumes", i -> i == 1);

		return output;
	}

	private static Stream<Aunt> auntsFromFile() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/year2015/day16/input.txt")) //
				.map(Aunt::new);
	}

	public static void main(String[] args) throws IOException {
		auntsFromFile() //
				.filter(aunt -> aunt.consistentWith(retroencabulatedMfcsamResult())) //
				.forEach(aunt -> System.out.println(aunt.getName()));
	}

}