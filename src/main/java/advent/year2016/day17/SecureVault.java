package advent.year2016.day17;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import advent.year2016.day5.MD5Password;

public class SecureVault {

	private static final int WIDTH = 4;
	private static final int HEIGHT = 4;

	private final String passcode;

	public SecureVault(String passcode) {
		this.passcode = passcode;
	}

	public Optional<Path> shortestPathToExit() {
		return shortestPathToExit(ImmutableSet.of(new Path(this)));
	}

	public OptionalInt longestPathToExitLength() {
		return longestPathToExit(OptionalInt.empty(), ImmutableSet.of(new Path(this)));
	}

	private Optional<Path> shortestPathToExit(Set<Path> current) {
		if (current.isEmpty()) {
			return Optional.empty();
		}

		Optional<Path> solution = current.stream() //
				.filter(Path::isAtExit) //
				.findAny();

		if (solution.isPresent()) {
			return solution;
		}

		Set<Path> nextSteps = current.stream() //
				.flatMap(Path::nextSteps) //
				.collect(toSet());

		return shortestPathToExit(nextSteps);
	}

	private OptionalInt longestPathToExit(OptionalInt longestSoFar, Set<Path> current) {
		if (current.isEmpty()) {
			return longestSoFar;
		}

		Optional<Integer> currentSuccessSize = current.stream() //
				.filter(Path::isAtExit) //
				.findAny() //
				.map(p -> p.steps.size());

		OptionalInt nextLongest = currentSuccessSize.isPresent() ? //
				OptionalInt.of(currentSuccessSize.get()) : //
				longestSoFar;

		Set<Path> nextSteps = current.stream() //
				.flatMap(Path::nextSteps) //
				.collect(toSet());

		return longestPathToExit(nextLongest, nextSteps);

	}

	public static class Path {

		private static final List<Direction> DOOR_HASH_ORDER = ImmutableList.of(Direction.U, Direction.D, Direction.L,
				Direction.R);

		private final SecureVault vault;
		private final List<Direction> steps;

		public Path(SecureVault vault) {
			this(vault, ImmutableList.of());
		}

		private Path(SecureVault vault, List<Direction> steps) {
			this.vault = vault;
			this.steps = steps;
		}

		private int x() {
			return steps.stream().mapToInt(d -> d.deltaX).sum();
		}

		private int y() {
			return steps.stream().mapToInt(d -> d.deltaY).sum();
		}

		public boolean isAtExit() {
			return this.x() == SecureVault.WIDTH - 1 && //
					this.y() == SecureVault.HEIGHT - 1;
		}

		private boolean isWithinVault() {
			return this.x() >= 0 && //
					this.x() < SecureVault.WIDTH && //
					this.y() >= 0 && this.y() < SecureVault.WIDTH;
		}

		public String toString() {
			return steps.stream() //
					.map(Direction::toString) //
					.collect(joining(""));
		}

		public Stream<Path> nextSteps() {
			if (this.isAtExit()) {
				return Stream.empty();
			}

			return legalDirections() //
					.map(this::moving) //
					.filter(Path::isWithinVault);
		}

		private Stream<Direction> legalDirections() {
			String doorString = doorString();

			return IntStream.range(0, DOOR_HASH_ORDER.size()) //
					.filter(i -> doorIsOpen(doorString.charAt(i))) //
					.mapToObj(DOOR_HASH_ORDER::get);
		}

		private String doorString() {
			return MD5Password.hexMD5Hash(vault.passcode + this.toString());
		}

		private boolean doorIsOpen(char c) {
			return c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f';
		}

		private Path moving(Direction direction) {
			List<Direction> newSteps = ImmutableList.<Direction>builder() //
					.addAll(steps) //
					.add(direction) //
					.build();

			return new Path(vault, newSteps);
		}
	}

	private static enum Direction {
		U(0, -1), //
		D(0, 1), //
		L(-1, 0), //
		R(1, 0);

		// By convention these are both positive towards the goal.
		private final int deltaX;
		private final int deltaY;

		private Direction(int deltaX, int deltaY) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}
	}

	public static void main(String[] args) {
		SecureVault vault = new SecureVault("rrrbmfta");
		System.out.println(vault.shortestPathToExit().get());
		System.out.println(vault.longestPathToExitLength().getAsInt());
	}

}
