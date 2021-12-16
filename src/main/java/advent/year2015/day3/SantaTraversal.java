package advent.year2015.day3;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

/**
 * --- Day 3: Perfectly Spherical Houses in a Vacuum ---
 * Santa is delivering presents to an infinite two-dimensional grid of houses.
 * He begins by delivering a present to the house at his starting location, and then an elf at the North Pole calls him
 * via radio and tells him where to move next.  Moves are always exactly one house to the north (^), south (v), east
 * (>), or west (<).  After each move, he delivers another present to the house at his new location.
 * However, the elf back at the north pole has had a little too much eggnog, and so his directions are a little off,
 * and Santa ends up visiting some houses more than once.  How many houses receive at least one present?
 * For example:
 * 
 * > delivers presents to 2 houses: one at the starting location, and one to the east.
 * ^>v< delivers presents to 4 houses in a square, including twice to the house at his starting/ending location.
 * ^v^v^v^v^v delivers a bunch of presents to some very lucky children at only 2 houses.
 * 
 * 
 * --- Part Two ---
 * The next year, to speed up the process, Santa creates a robot version of himself, Robo-Santa, to deliver presents
 * with him.
 * Santa and Robo-Santa start at the same location (delivering two presents to the same starting house), then take
 * turns moving based on instructions from the elf, who is eggnoggedly reading from the same script as the previous
 * year.
 * This year, how many houses receive at least one present?
 * For example:
 * 
 * ^v delivers presents to 3 houses, because Santa goes north, and then Robo-Santa goes south.
 * ^>v< now delivers presents to 3 houses, and Santa and Robo-Santa end up back where they started.
 * ^v^v^v^v^v now delivers presents to 11 houses, with Santa going one direction and Robo-Santa going the other.
 * 
 * 
 */
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
		String directions = Files.toString(new File("src/main/java/advent/year2015/day3/input.txt"), StandardCharsets.UTF_8);
		SantaTraversal traversal = new SantaTraversal();
		traversal.traverse(directions);
		System.out.println(traversal.visitedCount());
	}

	public static void santaAndRoboSantaVisitHouses() throws IOException {
		String directions = Files.toString(new File("src/main/java/advent/year2015/day3/input.txt"), StandardCharsets.UTF_8);

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