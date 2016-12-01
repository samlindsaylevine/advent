package advent.year2016.day1;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class StreetGridWalk {

	public static void main(String[] args) throws IOException {
		File inputFile = new File("src/main/java/advent/year2016/day1/input.txt");
		String input = FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8);
		StreetGridWalk walk = StreetGridWalk.of(input);
		System.out.println(walk.finalLocation().distanceFromStart());
		System.out.println(walk.firstVisitedTwice //
				.orElseThrow(() -> new IllegalStateException("Never visited a location twice")) //
				.distanceFromStart());
	}

	private Direction direction;
	private Location location;
	private Set<Location> visited;
	private Optional<Location> firstVisitedTwice;

	private StreetGridWalk() {
		this.direction = Direction.NORTH;
		this.location = new Location();

		this.visited = new HashSet<>();
		this.visited.add(location);

		this.firstVisitedTwice = Optional.empty();
	}

	public static StreetGridWalk of(String instructions) {
		StreetGridWalk output = new StreetGridWalk();

		for (String instruction : instructions.split(",")) {
			output.walk(instruction.trim());
		}

		return output;
	}

	public Location finalLocation() {
		return this.location;
	}

	public Optional<Location> firstLocationVisitedTwice() {
		return this.firstVisitedTwice;
	}

	private void walk(String instruction) {
		String turnLetter = instruction.substring(0, 1);
		if (turnLetter.equals("L")) {
			this.direction = this.direction.left();
		} else if (turnLetter.equals("R")) {
			this.direction = this.direction.right();
		} else {
			throw new IllegalArgumentException("Bad turn direction " + turnLetter);
		}

		int steps = Integer.parseInt(instruction.substring(1));

		for (int i = 0; i < steps; i++) {
			Location nextLocation = new Location(this.location.x + direction.deltaX,
					this.location.y + direction.deltaY);
			boolean nextLocationIsNew = this.visited.add(nextLocation);
			if (!nextLocationIsNew && !firstVisitedTwice.isPresent()) {
				firstVisitedTwice = Optional.of(nextLocation);
			}
			this.location = nextLocation;
		}
	}

	private static enum Direction {

		NORTH(0, 1), //
		EAST(1, 0), //
		SOUTH(0, -1), //
		WEST(-1, 0);

		private final int deltaX;
		private final int deltaY;

		private Direction(int deltaX, int deltaY) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}

		public Direction right() {
			return this.rightNTimes(1);
		}

		public Direction left() {
			return this.rightNTimes(-1);
		}

		private Direction rightNTimes(int numTurns) {
			// Java's % operator doesn't obey standard math "reduce mod" and
			// gives negative value outputs...
			int newIndex = Math.floorMod(this.ordinal() + numTurns, Direction.values().length);
			return Direction.values()[newIndex];
		}

	}

	public static class Location {

		private final int x;
		private final int y;

		public Location() {
			this(0, 0);
		}

		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int distanceFromStart() {
			return Math.abs(this.x) + Math.abs(this.y);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Location other = (Location) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
}
