package advent.year2016.day13;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;

/**
 * Refactored to support both days 13 and 24.
 */
public abstract class Maze {

	protected abstract boolean isOpen(int x, int y);

	private boolean isOpen(Address address) {
		return this.isOpen(address.x, address.y);
	}

	public int pathLength(int startX, int startY, int endX, int endY) {
		Traversal traversal = Traversal.toTarget(this, new Address(startX, startY), new Address(endX, endY));
		return traversal.stepsTaken;
	}

	public int locationsReachable(int startX, int startY, int maxSteps) {
		Traversal traversal = Traversal.maxSteps(this, new Address(startX, startY), maxSteps);
		return traversal.visited.size();
	}

	private static class Traversal {
		private Maze maze;
		private int stepsTaken;
		private Set<Address> visited;
		private Set<Address> current;
		private final Predicate<Traversal> isDone;

		public Traversal(Maze maze, Address start, Predicate<Traversal> isDone) {
			this.maze = maze;
			this.stepsTaken = 0;
			this.visited = new HashSet<>();
			this.visited.add(start);
			this.current = ImmutableSet.of(start);
			this.isDone = isDone;
			this.solve();
		}

		public static Traversal toTarget(Maze maze, Address start, Address end) {
			return new Traversal(maze, start, traversal -> traversal.current.contains(end));
		}

		public static Traversal maxSteps(Maze maze, Address start, int maxSteps) {
			return new Traversal(maze, start, traversal -> traversal.stepsTaken == maxSteps);
		}

		private void solve() {
			while (!this.isDone.test(this)) {

				// Optional debug information.
				// System.out.println("Steps taken: " + stepsTaken);
				// System.out.println("Current: " + current.size());

				if (current.isEmpty()) {
					throw new IllegalStateException("Ran out of possiblities!");
				}

				this.current = this.current.stream() //
						.map(Address::adjacent) //
						.flatMap(Set::stream) //
						.filter(address -> !this.visited.contains(address)) //
						.filter(this.maze::isOpen) //
						.collect(toSet());
				this.visited.addAll(this.current);
				this.stepsTaken++;
			}

			return;
		}
	}

	protected static class Address {
		public final int x;
		public final int y;

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

		@Override
		public String toString() {
			return "Address [x=" + x + ", y=" + y + "]";
		}
	}

}
