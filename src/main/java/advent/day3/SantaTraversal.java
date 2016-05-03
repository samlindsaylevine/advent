package advent.day3;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class SantaTraversal {

	private Coordinate currentLocation;

	private Set<Coordinate> visited = new HashSet<>();

	public SantaTraversal() {
		this.currentLocation = new Coordinate(0, 0);
		this.visited.add(this.currentLocation);
	}

	public void traverse(String directions) {
		directions.chars() //
				.mapToObj(Direction::byCharacter) //
				.forEach(this::go);
	}

	private void go(char character) {
		this.go(Direction.byCharacter(character));
	}

	private void go(Direction direction) {
		this.currentLocation = direction.update.apply(this.currentLocation);
		this.visited.add(this.currentLocation);
	}

	public int visitedCount() {
		return this.visited.size();
	}

	public static void santaVistsHouses() throws IOException {
		String directions = Files.toString(new File("src/main/java/advent/day3/input.txt"), StandardCharsets.UTF_8);
		SantaTraversal traversal = new SantaTraversal();
		traversal.traverse(directions);
		System.out.println(traversal.visitedCount());
	}

	public static void santaAndRoboSantaVisitHouses() throws IOException {
		String directions = Files.toString(new File("src/main/java/advent/day3/input.txt"), StandardCharsets.UTF_8);

		SantaTraversal santa = new SantaTraversal();
		SantaTraversal roboSanta = new SantaTraversal();

		for (int i = 0; i < directions.length(); i++) {
			char character = directions.charAt(i);

			if (i % 2 == 0) {
				santa.go(character);
			} else {
				roboSanta.go(character);
			}
		}

		Set<Coordinate> visited = Sets.union(santa.visited, roboSanta.visited);

		System.out.println(visited.size());
	}

	public static void main(String[] args) throws IOException {
		santaAndRoboSantaVisitHouses();
	}

	private static enum Direction {
		UP('^', Coordinate::up), //
		DOWN('v', Coordinate::down), //
		LEFT('<', Coordinate::left), //
		RIGHT('>', Coordinate::right);

		private final char character;
		private final Function<Coordinate, Coordinate> update;

		private Direction(char character, Function<Coordinate, Coordinate> update) {
			this.character = character;
			this.update = update;
		}

		public static Direction byCharacter(int character) {
			return Arrays.stream(values()).filter(item -> item.character == character).findAny().get();
		}
	}

	private static class Coordinate {
		private final int x;
		private final int y;

		public Coordinate(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public Coordinate up() {
			return new Coordinate(this.x, this.y + 1);
		}

		public Coordinate down() {
			return new Coordinate(this.x, this.y - 1);
		}

		public Coordinate left() {
			return new Coordinate(this.x - 1, this.y);
		}

		public Coordinate right() {
			return new Coordinate(this.x + 1, this.y);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.x;
			result = prime * result + this.y;
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
			Coordinate other = (Coordinate) obj;
			if (this.x != other.x) {
				return false;
			}
			if (this.y != other.y) {
				return false;
			}
			return true;
		}

	}

}
