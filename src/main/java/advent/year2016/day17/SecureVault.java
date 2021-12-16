package advent.year2016.day17;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import advent.utils.StreamUtils;
import advent.year2016.day5.MD5Password;

/**
 * --- Day 17: Two Steps Forward ---
 * You're trying to access a secure vault protected by a 4x4 grid of small rooms connected by doors. You start in the
 * top-left room (marked S), and you can access the vault (marked V) once you reach the bottom-right room:
 * #########
 * #S| | | #
 * #-#-#-#-#
 * # | | | #
 * #-#-#-#-#
 * # | | | #
 * #-#-#-#-#
 * # | | |  
 * ####### V
 * 
 * Fixed walls are marked with #, and doors are marked with - or |.
 * The doors in your current room are either open or closed (and locked) based on the hexadecimal MD5 hash of a
 * passcode (your puzzle input) followed by a sequence of uppercase characters representing the path you have taken so
 * far (U for up, D for down, L for left, and R for right).
 * Only the first four characters of the hash are used; they represent, respectively, the doors up, down, left, and
 * right from your current position. Any b, c, d, e, or f means that the corresponding door is open; any other
 * character (any number or a) means that the corresponding door is closed and locked.
 * To access the vault, all you need to do is reach the bottom-right room; reaching this room opens the vault and all
 * doors in the maze.
 * For example, suppose the passcode is hijkl. Initially, you have taken no steps, and so your path is empty: you
 * simply find the MD5 hash of hijkl alone. The first four characters of this hash are ced9, which indicate that up is
 * open (c), down is open (e), left is open (d), and right is closed and locked (9). Because you start in the top-left
 * corner, there are no "up" or "left" doors to be open, so your only choice is down.
 * Next, having gone only one step (down, or D), you find the hash of hijklD. This produces f2bc, which indicates that
 * you can go back up, left (but that's a wall), or right. Going right means hashing hijklDR to get 5745 - all doors
 * closed and locked. However, going up instead is worthwhile: even though it returns you to the room you started in,
 * your path would then be DU, opening a different set of doors.
 * After going DU (and then hashing hijklDU to get 528e), only the right door is open; after going DUR, all doors lock.
 * (Fortunately, your actual passcode is not hijkl).
 * Passcodes actually used by Easter Bunny Vault Security do allow access to the vault if you know the right path.  For
 * example:
 * 
 * If your passcode were ihgpwlah, the shortest path would be DDRRRD.
 * With kglvqrro, the shortest path would be DDUDRLRRUDRD.
 * With ulqzkmiv, the shortest would be DRURDRUDDLLDLUURRDULRLDUUDDDRR.
 * 
 * Given your vault's passcode, what is the shortest path (the actual path, not just the length) to reach the vault?
 * 
 * --- Part Two ---
 * You're curious how robust this security solution really is, and so you decide to find longer and longer paths which
 * still provide access to the vault. You remember that paths always end the first time they reach the bottom-right
 * room (that is, they can never pass through it, only end in it).
 * For example:
 * 
 * If your passcode were ihgpwlah, the longest path would take 370 steps.
 * With kglvqrro, the longest path would be 492 steps long.
 * With ulqzkmiv, the longest path would be 830 steps long.
 * 
 * 
 * What is the length of the longest path that reaches the vault?
 * 
 */
public class SecureVault {

	private static final int WIDTH = 4;
	private static final int HEIGHT = 4;

	private final String passcode;

	public SecureVault(String passcode) {
		this.passcode = passcode;
	}

	public Optional<Path> shortestPathToExit() {
		return pathsToExit().findFirst();
	}

	public Optional<Path> longestPathToExit() {
		return pathsToExit() //
				// Assuming that the stream is in shortest-to-longest order.
				.reduce((l, r) -> r);
	}

	public Optional<Integer> longestPathToExitLength() {
		return longestPathToExit().map(p -> p.steps.size());
	}

	private Stream<Path> pathsToExit() {
		return StreamUtils.iterateUntil(ImmutableSet.of(new Path(this)), SecureVault::nextSteps, Set::isEmpty) //
				.flatMap(Set::stream) //
				.filter(Path::isAtExit);
	}

	private static Set<Path> nextSteps(Set<Path> steps) {
		return steps.stream() //
				.flatMap(Path::nextSteps) //
				.collect(toSet());
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
		System.out.println(vault.longestPathToExitLength().get());
	}

}