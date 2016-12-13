package advent.year2016.day13;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableSet;

public class CubicleMaze {

	private final int favoriteNumber;

	public CubicleMaze(int favoriteNumber) {
		this.favoriteNumber = favoriteNumber;
	}

	boolean isOpen(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}

		int simpleSystemCalculation = x * x + 3 * x + 2 * x * y + y + y * y + this.favoriteNumber;
		String binaryString = Integer.toBinaryString(simpleSystemCalculation);
		long numOneBits = binaryString.chars().filter(i -> i == '1').count();
		return numOneBits % 2 == 0;
	}

	private boolean isOpen(Address address) {
		return this.isOpen(address.x, address.y);
	}

	/**
	 * Returns a string representation of the first subsection of the maze, with
	 * the provided size.
	 */
	public String toString(int width, int height) {
		return IntStream.range(0, height) //
				.mapToObj(i -> this.row(width, i)) //
				.collect(joining("\n"));
	}

	private String row(int width, int y) {
		return IntStream.range(0, width) //
				.mapToObj(x -> this.isOpen(x, y)) //
				.map(open -> open ? "." : "#") //
				.collect(joining(""));
	}

	public int pathLength(int startX, int startY, int endX, int endY) {
		return new Traversal(this, new Address(startX, startY), new Address(endX, endY)).solve();
	}

	private static class Traversal {
		private CubicleMaze maze;
		private int stepsTaken;
		private Set<Address> visited;
		private Set<Address> current;
		private Address target;

		public Traversal(CubicleMaze maze, Address start, Address end) {
			this.maze = maze;
			this.stepsTaken = 0;
			this.visited = new HashSet<>();
			this.visited.add(start);
			this.current = ImmutableSet.of(start);
			this.target = end;
		}

		public int solve() {
			while (!this.current.contains(this.target)) {
				this.current = this.current.stream() //
						.map(Address::adjacent) //
						.flatMap(Set::stream) //
						.filter(address -> !this.visited.contains(address)) //
						.filter(this.maze::isOpen) //
						.collect(toSet());
				this.stepsTaken++;
			}

			return this.stepsTaken;
		}
	}

	private static class Address {
		private final int x;
		private final int y;

		public Address(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Set<Address> adjacent() {
			return ImmutableSet.of( //
					new Address(this.x - 1, this.y), //
					new Address(this.x, this.y + 1), //
					new Address(this.x + 1, this.y), //
					new Address(this.x, this.y - 1));
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
			Address other = (Address) obj;
			if (this.x != other.x) {
				return false;
			}
			if (this.y != other.y) {
				return false;
			}
			return true;
		}
	}

	public static void main(String[] args) {
		CubicleMaze maze = new CubicleMaze(1352);
		System.out.println(maze.pathLength(1, 1, 31, 39));
	}
}
