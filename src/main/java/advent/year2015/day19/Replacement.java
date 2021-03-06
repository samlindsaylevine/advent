package advent.year2015.day19;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class Replacement {

	private String from;
	private String to;

	public static List<Replacement> fromFile() throws IOException {
		return Files.lines(Paths.get("src/main/java/advent/year2015/day19/input.txt")) //
				.map(Replacement::new) //
				.collect(toList());
	}

	private static Pattern REGEX = Pattern.compile("(\\w+) => (\\w+)");

	public Replacement(String representation) {
		Matcher matcher = REGEX.matcher(representation);
		Preconditions.checkArgument(matcher.matches(), representation);

		this.from = matcher.group(1);
		this.to = matcher.group(2);
	}

	public Replacement(String from, String to) {
		this.from = from;
		this.to = to;
	}

	public Replacement reversed() {
		return new Replacement(this.to, this.from);
	}

	/**
	 * Returns all possible molecules generated by applying this replacement to
	 * an input molecule.
	 */
	public Set<Molecule> apply(Molecule input) {
		Set<Molecule> output = new HashSet<>();

		int index = 0;
		String moleculeString = input.toString();

		while (index != -1) {
			index = moleculeString.indexOf(this.from, index);

			if (index != -1) {
				String replaced = moleculeString.substring(0, index) + this.to
						+ moleculeString.substring(index + this.from.length());
				output.add(new Molecule(replaced));
				index++;
			}
		}

		return output;
	}

	public static Set<Molecule> allGeneratable(Molecule input, Collection<Replacement> replacements) {
		return replacements.parallelStream() //
				.flatMap(r -> r.apply(input).stream()) //
				.collect(toSet());
	}

	public static Set<Molecule> allGeneratable(Collection<Molecule> molecules, Collection<Replacement> replacements) {
		return molecules.parallelStream() //
				.flatMap(m -> allGeneratable(m, replacements).stream()) //
				.collect(toSet());
	}

	public static int howManyCreatable() throws IOException {
		return allGeneratable(Molecule.REFERENCE_MOLECULE, fromFile()).size();
	}

	public static int stepsToCreate(Molecule desired, Collection<Replacement> replacements) {
		return stepsToCreate(Molecule.ELECTRON, desired, replacements);
	}

	public static int stepsToCreate(Molecule initial, Molecule desired, Collection<Replacement> replacements) {
		Set<Molecule> workingSet = ImmutableSet.of(initial);

		int stepsTaken = 0;

		while (!workingSet.contains(desired)) {
			workingSet = allGeneratable(workingSet, replacements);
			stepsTaken++;
			System.out.println(stepsTaken + ": " + workingSet.size());
		}

		return stepsTaken;
	}

	@Override
	public String toString() {
		return this.from + " => " + this.to;
	}

	public static int crappyStepsToCreate(Molecule initial, Molecule desired, Collection<Replacement> replacements) {
		Molecule workingMolecule = initial;

		int stepsTaken = 0;

		while (!workingMolecule.equals(desired)) {
			Set<Molecule> nextPossiblities = allGeneratable(workingMolecule, replacements);
			if (nextPossiblities.isEmpty()) {
				// Oops, start over and try again.
				return crappyStepsToCreate(initial, desired, replacements);
			}
			workingMolecule = randomElementFrom(nextPossiblities);
			stepsTaken++;
		}

		return stepsTaken;

	}

	private static <T> T randomElementFrom(Collection<T> items) {
		List<T> ordered = new ArrayList<>(items);
		int index = (new Random()).nextInt(items.size());
		return ordered.get(index);
	}

	public static int stepsToCreateMedicine() throws IOException {
		// Forward was too slow. Running in reverse instead.
		Collection<Replacement> reverseReplacements = fromFile().stream() //
				.map(Replacement::reversed) //
				.collect(toList());

		return crappyStepsToCreate(Molecule.REFERENCE_MOLECULE, Molecule.ELECTRON, reverseReplacements);
	}

	public static void main(String[] args) throws IOException {
		for (int i = 1; i < 100; i++) {
			System.out.println(stepsToCreateMedicine());
		}
	}

}
