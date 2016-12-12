package advent.year2016.day11;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class RadioisotopeTestingFacility {

	private List<Floor> floors;
	private int humanFloorIndex;

	public RadioisotopeTestingFacility(Stream<String> lines) {
		// Assuming that all floors are presented to us in ascending order.
		this.floors = lines.map(Floor::new).collect(toList());
		this.humanFloorIndex = 0;
	}

	private RadioisotopeTestingFacility(RadioisotopeTestingFacility other) {
		this.floors = new ArrayList<>(other.floors);
		this.humanFloorIndex = other.humanFloorIndex;
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

		private Floor with(Set<FacilityItem> removedItems) {
			Floor output = new Floor(this);
			removedItems.forEach(item -> item.addTo(output));
			return output;
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
			this.stepsElapsed = 0;
			this.visitedStates = new HashSet<>();
			this.visitedStates.add(facility);
			this.currentStates = new HashSet<>();
			this.currentStates.add(facility);
		}

		private boolean isSolved() {
			return this.currentStates.stream().anyMatch(RadioisotopeTestingFacility::everythingIsOnTheTopFloor);
		}

		public int solve() {
			while (!this.isSolved()) {
				if (this.currentStates.isEmpty()) {
					throw new IllegalStateException("Unsolvable, ran out of possiblities");
				}

				Set<RadioisotopeTestingFacility> nextStates = this.currentStates.stream() //
						.flatMap(RadioisotopeTestingFacility::legalNextMoves) //
						.filter(state -> !this.visitedStates.contains(state)) //
						.collect(toSet());

				this.currentStates = nextStates;
				this.visitedStates.addAll(this.currentStates);

				this.stepsElapsed++;
			}

			return this.stepsElapsed;
		}
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day11/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			RadioisotopeTestingFacility facility = new RadioisotopeTestingFacility(lines);
			System.out.println(facility.minimumNumberOfStepsToGetEverythingOntoTheTopFloor());
		}
	}

}
