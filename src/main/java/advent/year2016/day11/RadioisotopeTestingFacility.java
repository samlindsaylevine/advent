package advent.year2016.day11;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * --- Day 11: Radioisotope Thermoelectric Generators ---
 * You come upon a column of four floors that have been entirely sealed off from the rest of the building except for a
 * small dedicated lobby.  There are some radiation warnings and a big sign which reads "Radioisotope Testing Facility".
 * According to the project status board, this facility is currently being used to experiment with Radioisotope
 * Thermoelectric Generators (RTGs, or simply "generators") that are designed to be paired with specially-constructed
 * microchips. Basically, an RTG is a highly radioactive rock that generates electricity through heat.
 * The experimental RTGs have poor radiation containment, so they're dangerously radioactive. The chips are prototypes
 * and don't have normal radiation shielding, but they do have the ability to generate an electromagnetic radiation
 * shield when powered.  Unfortunately, they can only be powered by their corresponding RTG. An RTG powering a
 * microchip is still dangerous to other microchips.
 * In other words, if a chip is ever left in the same area as another RTG, and it's not connected to its own RTG, the
 * chip will be fried. Therefore, it is assumed that you will follow procedure and keep chips connected to their
 * corresponding RTG when they're in the same room, and away from other RTGs otherwise.
 * These microchips sound very interesting and useful to your current activities, and you'd like to try to retrieve
 * them.  The fourth floor of the facility has an assembling machine which can make a self-contained, shielded computer
 * for you to take with you - that is, if you can bring it all of the RTGs and microchips.
 * Within the radiation-shielded part of the facility (in which it's safe to have these pre-assembly RTGs), there is an
 * elevator that can move between the four floors. Its capacity rating means it can carry at most yourself and two RTGs
 * or microchips in any combination. (They're rigged to some heavy diagnostic equipment - the assembling machine will
 * detach it for you.) As a security measure, the elevator will only function if it contains at least one RTG or
 * microchip. The elevator always stops on each floor to recharge, and this takes long enough that the items within it
 * and the items on that floor can irradiate each other. (You can prevent this if a Microchip and its Generator end up
 * on the same floor in this way, as they can be connected while the elevator is recharging.)
 * You make some notes of the locations of each component of interest (your puzzle input). Before you don a hazmat suit
 * and start moving things around, you'd like to have an idea of what you need to do.
 * When you enter the containment area, you and the elevator will start on the first floor.
 * For example, suppose the isolated area has the following arrangement:
 * The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.
 * The second floor contains a hydrogen generator.
 * The third floor contains a lithium generator.
 * The fourth floor contains nothing relevant.
 * 
 * As a diagram (F# for a Floor number, E for Elevator, H for Hydrogen, L for Lithium, M for Microchip, and G for
 * Generator), the initial state looks like this:
 * F4 .  .  .  .  .  
 * F3 .  .  .  LG .  
 * F2 .  HG .  .  .  
 * F1 E  .  HM .  LM 
 * 
 * Then, to get everything up to the assembling machine on the fourth floor, the following steps could be taken:
 * 
 * Bring the Hydrogen-compatible Microchip to the second floor, which is safe because it can get power from the
 * Hydrogen Generator:F4 .  .  .  .  .  
 * F3 .  .  .  LG .  
 * F2 E  HG HM .  .  
 * F1 .  .  .  .  LM 
 * 
 * Bring both Hydrogen-related items to the third floor, which is safe because the Hydrogen-compatible microchip is
 * getting power from its generator:F4 .  .  .  .  .  
 * F3 E  HG HM LG .  
 * F2 .  .  .  .  .  
 * F1 .  .  .  .  LM 
 * 
 * Leave the Hydrogen Generator on floor three, but bring the Hydrogen-compatible Microchip back down with you so you
 * can still use the elevator:F4 .  .  .  .  .  
 * F3 .  HG .  LG .  
 * F2 E  .  HM .  .  
 * F1 .  .  .  .  LM 
 * 
 * At the first floor, grab the Lithium-compatible Microchip, which is safe because Microchips don't affect each
 * other:F4 .  .  .  .  .  
 * F3 .  HG .  LG .  
 * F2 .  .  .  .  .  
 * F1 E  .  HM .  LM 
 * 
 * Bring both Microchips up one floor, where there is nothing to fry them:F4 .  .  .  .  .  
 * F3 .  HG .  LG .  
 * F2 E  .  HM .  LM 
 * F1 .  .  .  .  .  
 * 
 * Bring both Microchips up again to floor three, where they can be temporarily connected to their corresponding
 * generators while the elevator recharges, preventing either of them from being fried:F4 .  .  .  .  .  
 * F3 E  HG HM LG LM 
 * F2 .  .  .  .  .  
 * F1 .  .  .  .  .  
 * 
 * Bring both Microchips to the fourth floor:F4 E  .  HM .  LM 
 * F3 .  HG .  LG .  
 * F2 .  .  .  .  .  
 * F1 .  .  .  .  .  
 * 
 * Leave the Lithium-compatible microchip on the fourth floor, but bring the Hydrogen-compatible one so you can still
 * use the elevator; this is safe because although the Lithium Generator is on the destination floor, you can connect
 * Hydrogen-compatible microchip to the Hydrogen Generator there:F4 .  .  .  .  LM 
 * F3 E  HG HM LG .  
 * F2 .  .  .  .  .  
 * F1 .  .  .  .  .  
 * 
 * Bring both Generators up to the fourth floor, which is safe because you can connect the Lithium-compatible Microchip
 * to the Lithium Generator upon arrival:F4 E  HG .  LG LM 
 * F3 .  .  HM .  .  
 * F2 .  .  .  .  .  
 * F1 .  .  .  .  .  
 * 
 * Bring the Lithium Microchip with you to the third floor so you can use the elevator:F4 .  HG .  LG .  
 * F3 E  .  HM .  LM 
 * F2 .  .  .  .  .  
 * F1 .  .  .  .  .  
 * 
 * Bring both Microchips to the fourth floor:F4 E  HG HM LG LM 
 * F3 .  .  .  .  .  
 * F2 .  .  .  .  .  
 * F1 .  .  .  .  .  
 * 
 * 
 * In this arrangement, it takes 11 steps to collect all of the objects at the fourth floor for assembly. (Each
 * elevator stop counts as one step, even if nothing is added to or removed from it.)
 * In your situation, what is the minimum number of steps required to bring all of the objects to the fourth floor?
 * 
 * --- Part Two ---
 * You step into the cleanroom separating the lobby from the isolated area and put on the hazmat suit.
 * Upon entering the isolated containment area, however, you notice some extra parts on the first floor that weren't
 * listed on the record outside:
 * 
 * An elerium generator.
 * An elerium-compatible microchip.
 * A dilithium generator.
 * A dilithium-compatible microchip.
 * 
 * These work just like the other generators and microchips. You'll have to get them up to assembly as well.
 * What is the minimum number of steps required to bring all of the objects, including these four new ones, to the
 * fourth floor?
 * 
 */
public class RadioisotopeTestingFacility {

	private List<Floor> floors;
	private int humanFloorIndex;

	public RadioisotopeTestingFacility(Stream<String> lines) {
		// Assuming that all floors are presented to us in ascending order.
		this(lines.map(Floor::new).collect(toList()), 0);
	}

	private RadioisotopeTestingFacility(RadioisotopeTestingFacility other) {
		this(new ArrayList<>(other.floors), other.humanFloorIndex);
	}

	private RadioisotopeTestingFacility(List<Floor> floors, int humanFloorIndex) {
		this.floors = floors;
		this.humanFloorIndex = humanFloorIndex;
	}

	public int minimumNumberOfStepsToGetEverythingOntoTheTopFloor() {
		return new FacilitySolution(this).solve();
	}

	public Floor getFloor(int index) {
		return this.floors.get(index);
	}

	private boolean everythingIsOnTheTopFloor() {
		return this.floors.stream() //
				.limit(this.floors.size() - 1) //
				.allMatch(Floor::isEmpty);
	}

	boolean isLegalState() {
		return this.floors.stream().allMatch(Floor::isLegalState);
	}

	private Stream<RadioisotopeTestingFacility> possibleNextMoves() {
		return this.movableItemSets() //
				.flatMap(items -> this.adjacentFloors() //
						.mapToObj(newFloor -> this.moving(items, newFloor)));
	}

	Stream<RadioisotopeTestingFacility> legalNextMoves() {
		return this.possibleNextMoves().filter(RadioisotopeTestingFacility::isLegalState);
	}

	private IntStream adjacentFloors() {
		if (this.humanFloorIndex == 0) {
			return IntStream.of(this.humanFloorIndex + 1);
		} else if (this.humanFloorIndex == this.floors.size() - 1) {
			return IntStream.of(this.humanFloorIndex - 1);
		} else {
			return IntStream.of(this.humanFloorIndex - 1, this.humanFloorIndex + 1);
		}
	}

	private Stream<Set<FacilityItem>> movableItemSets() {
		Floor currentFloor = this.floors.get(this.humanFloorIndex);

		List<FacilityItem> allItems = new ArrayList<>(Sets.union(currentFloor.generators, currentFloor.microchips));

		Stream<Set<FacilityItem>> singleItemChoices = allItems.stream() //
				.map(ImmutableSet::of);

		Stream<Set<FacilityItem>> doubleItemChoices = IntStream.range(0, allItems.size()) //
				.boxed() //
				.flatMap(i -> IntStream.range(i + 1, allItems.size()) //
						.mapToObj(j -> ImmutableSet.of(allItems.get(i), allItems.get(j))));

		return Stream.concat(singleItemChoices, doubleItemChoices);
	}

	RadioisotopeTestingFacility moving(Set<FacilityItem> movedItems, int newFloor) {
		RadioisotopeTestingFacility output = new RadioisotopeTestingFacility(this);
		output.floors.set(this.humanFloorIndex, output.floors.get(this.humanFloorIndex).without(movedItems));
		output.floors.set(newFloor, output.floors.get(newFloor).with(movedItems));
		output.humanFloorIndex = newFloor;
		return output;
	}

	RadioisotopeTestingFacility withAdditionalItemsOnFirstFloor(FacilityItem... items) {
		RadioisotopeTestingFacility output = new RadioisotopeTestingFacility(this);
		output.floors.set(0, output.floors.get(0).with(Arrays.stream(items).collect(toSet())));
		return output;
	}

	/**
	 * A pure brute-force run is not efficient enough to be able to handle part
	 * 2 of the problem. For purposes of our solution, we don't particularly
	 * care which element is where - a state with A on floor 1 and B on floor 2
	 * is equivalent to one with B on floor 1 and A on floor 2.
	 * 
	 * So, we check only the 'canonical' forms of the states, where we are
	 * willing to permute around the elements as long as the replacements are
	 * one-to-one.
	 * 
	 * We'll make the canonical form one where the elements are in alphabetical
	 * order from lowest microchip to highest, tiebreaking on lowest generator.
	 */
	RadioisotopeTestingFacility canonicalize() {
		List<String> alphabeticalElements = this.allElements() //
				.sorted() //
				.collect(toList());

		Comparator<String> byFloorOrder = comparing(this::firstMicrochipIndex) //
				.thenComparing(this::firstGeneratorIndex) //
				.thenComparing(naturalOrder());

		List<String> currentOrderedElements = this.allElements() //
				.sorted(byFloorOrder) //
				.collect(toList());

		Map<String, String> renames = IntStream.range(0, currentOrderedElements.size()) //
				.boxed() //
				.collect(toMap(currentOrderedElements::get, alphabeticalElements::get));

		List<Floor> newFloors = this.floors.stream() //
				.map(floor -> floor.changingElements(renames)) //
				.collect(toList());

		return new RadioisotopeTestingFacility(newFloors, this.humanFloorIndex);
	}

	private Stream<String> allElements() {
		return this.floors.stream() //
				.map(floor -> floor.microchips) //
				.flatMap(Set::stream) //
				.map(microchip -> microchip.element) //
				.distinct();
	}

	private int firstMicrochipIndex(String element) {
		return IntStream.range(0, this.floors.size()) //
				.filter(i -> this.floors.get(i).microchips.stream().anyMatch(chip -> chip.element.equals(element))) //
				.findFirst() //
				.orElseThrow(() -> new NoSuchElementException("No chip for element " + element));
	}

	private int firstGeneratorIndex(String element) {
		return IntStream.range(0, this.floors.size()) //
				.filter(i -> this.floors.get(i).generators.stream().anyMatch(chip -> chip.element.equals(element))) //
				.findFirst() //
				.orElseThrow(() -> new NoSuchElementException("No chip for element " + element));
	}

	private static interface FacilityItem {
		public void addTo(Floor floor);
	}

	static class Microchip implements FacilityItem {
		final String element;

		public Microchip(String element) {
			this.element = element;
		}

		@Override
		public void addTo(Floor floor) {
			floor.microchips.add(this);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.element == null) ? 0 : this.element.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			Microchip other = (Microchip) obj;
			if (this.element == null) {
				if (other.element != null) {
					return false;
				}
			} else if (!this.element.equals(other.element)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "Microchip [element=" + this.element + "]";
		}
	}

	static class Generator implements FacilityItem {
		final String element;

		public Generator(String element) {
			this.element = element;
		}

		public boolean powers(Microchip microchip) {
			return this.element.equals(microchip.element);
		}

		@Override
		public void addTo(Floor floor) {
			floor.generators.add(this);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.element == null) ? 0 : this.element.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			Generator other = (Generator) obj;
			if (this.element == null) {
				if (other.element != null) {
					return false;
				}
			} else if (!this.element.equals(other.element)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "Generator [element=" + this.element + "]";
		}
	}

	static class Floor {
		public Set<Microchip> microchips;
		public Set<Generator> generators;

		private static final Pattern FLOOR_PATTERN = Pattern.compile("The (\\w+) floor contains (.*).");
		private static final String clauseSeparator = ", and | and |, ";
		private static final Pattern GENERATOR_PATTERN = Pattern.compile("a (\\w+) generator");
		private static final Pattern MICROCHIP_PATTERN = Pattern.compile("a (\\w+)-compatible microchip");

		public Floor(String representation) {
			Matcher floorMatcher = FLOOR_PATTERN.matcher(representation);
			Preconditions.checkArgument(floorMatcher.matches(), "Bad floor string %s", representation);
			String contents = floorMatcher.group(2);

			this.generators = new HashSet<>();
			this.microchips = new HashSet<>();
			for (String item : contents.split(clauseSeparator)) {
				this.addItem(item);
			}
		}

		public Floor(Floor other) {
			this.microchips = new HashSet<>(other.microchips);
			this.generators = new HashSet<>(other.generators);
		}

		Floor(Set<Microchip> microchips, Set<Generator> generators) {
			this.microchips = microchips;
			this.generators = generators;
		}

		private void addItem(String item) {
			if (item.equals("nothing relevant")) {
				return;
			}

			Matcher generatorMatcher = GENERATOR_PATTERN.matcher(item);
			if (generatorMatcher.matches()) {
				this.generators.add(new Generator(generatorMatcher.group(1)));
				return;
			}

			Matcher microchipMatcher = MICROCHIP_PATTERN.matcher(item);
			if (microchipMatcher.matches()) {
				this.microchips.add(new Microchip(microchipMatcher.group(1)));
				return;
			}

			throw new IllegalArgumentException("Unparseable item " + item);
		}

		public boolean isEmpty() {
			return this.microchips.isEmpty() && this.generators.isEmpty();
		}

		public boolean isLegalState() {
			return this.microchips.stream().allMatch(this::chipSurvives);
		}

		private boolean chipSurvives(Microchip microchip) {
			return this.generators.isEmpty() || //
					this.generators.stream() //
							.anyMatch(generator -> generator.powers(microchip));
		}

		private Floor without(Set<FacilityItem> removedItems) {
			Floor output = new Floor(this);
			output.generators.removeAll(removedItems);
			output.microchips.removeAll(removedItems);
			return output;
		}

		private Floor with(Set<FacilityItem> additionalItems) {
			Floor output = new Floor(this);
			additionalItems.forEach(item -> item.addTo(output));
			return output;
		}

		public Floor changingElements(Map<String, String> elementRenames) {
			Set<Microchip> newChips = this.microchips.stream() //
					.map(chip -> new Microchip(elementRenames.get(chip.element))) //
					.collect(toSet());
			Set<Generator> newGenerators = this.generators.stream() //
					.map(generator -> new Generator(elementRenames.get(generator.element))) //
					.collect(toSet());
			return new Floor(newChips, newGenerators);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.generators == null) ? 0 : this.generators.hashCode());
			result = prime * result + ((this.microchips == null) ? 0 : this.microchips.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			Floor other = (Floor) obj;
			if (this.generators == null) {
				if (other.generators != null) {
					return false;
				}
			} else if (!this.generators.equals(other.generators)) {
				return false;
			}
			if (this.microchips == null) {
				if (other.microchips != null) {
					return false;
				}
			} else if (!this.microchips.equals(other.microchips)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "Floor [microchips=" + this.microchips + ", generators=" + this.generators + "]";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.floors == null) ? 0 : this.floors.hashCode());
		result = prime * result + this.humanFloorIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		RadioisotopeTestingFacility other = (RadioisotopeTestingFacility) obj;
		if (this.floors == null) {
			if (other.floors != null) {
				return false;
			}
		} else if (!this.floors.equals(other.floors)) {
			return false;
		}
		if (this.humanFloorIndex != other.humanFloorIndex) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RadioisotopeTestingFacility [floors=" + this.floors + ", humanFloorIndex=" + this.humanFloorIndex + "]";
	}

	private static class FacilitySolution {
		private int stepsElapsed;
		private Set<RadioisotopeTestingFacility> visitedStates;
		private Set<RadioisotopeTestingFacility> currentStates;

		public FacilitySolution(RadioisotopeTestingFacility facility) {

			RadioisotopeTestingFacility canonical = facility.canonicalize();

			this.stepsElapsed = 0;
			this.visitedStates = new HashSet<>();
			this.visitedStates.add(canonical);
			this.currentStates = new HashSet<>();
			this.currentStates.add(canonical);
		}

		private boolean isSolved() {
			return this.currentStates.stream().anyMatch(RadioisotopeTestingFacility::everythingIsOnTheTopFloor);
		}

		public int solve() {
			while (!this.isSolved()) {
				if (this.currentStates.isEmpty()) {
					throw new IllegalStateException("Unsolvable, ran out of possiblities");
				}

				Set<RadioisotopeTestingFacility> nextStates = this.currentStates.parallelStream() //
						.flatMap(RadioisotopeTestingFacility::legalNextMoves) //
						.map(RadioisotopeTestingFacility::canonicalize) //
						.filter(state -> !this.visitedStates.contains(state)) //
						.collect(toSet());

				this.currentStates = nextStates;
				this.visitedStates.addAll(this.currentStates);

				this.stepsElapsed++;

				// Debug output / progress indicator if desired.
				// System.out.println(" " + this.stepsElapsed + " " +
				// this.currentStates.size());
			}

			return this.stepsElapsed;
		}
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day11/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			RadioisotopeTestingFacility facility = new RadioisotopeTestingFacility(lines);
			System.out.println(facility.minimumNumberOfStepsToGetEverythingOntoTheTopFloor());

			RadioisotopeTestingFacility partTwo = facility.withAdditionalItemsOnFirstFloor( //
					new Generator("elerium"), //
					new Microchip("elerium"), //
					new Generator("dilithium"), //
					new Microchip("dilithium"));
			System.out.println(partTwo.minimumNumberOfStepsToGetEverythingOntoTheTopFloor());

		}
	}

}