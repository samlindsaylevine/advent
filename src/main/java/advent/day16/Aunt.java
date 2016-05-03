package advent.day16;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Splitter;

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
		return Files.lines(Paths.get("src/main/java/advent/day16/input.txt")) //
				.map(Aunt::new);
	}

	public static void main(String[] args) throws IOException {
		auntsFromFile() //
				.filter(aunt -> aunt.consistentWith(retroencabulatedMfcsamResult())) //
				.forEach(aunt -> System.out.println(aunt.getName()));
	}

}
