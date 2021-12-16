package advent.year2016.day18;

import static advent.utils.CollectorUtils.charsToString;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

/**
 * --- Day 18: Like a Rogue ---
 * As you enter this room, you hear a loud click! Some of the tiles in the floor here seem to be pressure plates for
 * traps, and the trap you just triggered has run out of... whatever it tried to do to you. You doubt you'll be so
 * lucky next time.
 * Upon closer examination, the traps and safe tiles in this room seem to follow a pattern. The tiles are arranged into
 * rows that are all the same width; you take note of the safe tiles (.) and traps (^) in the first row (your puzzle
 * input).
 * The type of tile (trapped or safe) in each row is based on the types of the tiles in the same position, and to
 * either side of that position, in the previous row. (If either side is off either end of the row, it counts as "safe"
 * because there isn't a trap embedded in the wall.)
 * For example, suppose you know the first row (with tiles marked by letters) and want to determine the next row (with
 * tiles marked by numbers):
 * ABCDE
 * 12345
 * 
 * The type of tile 2 is based on the types of tiles A, B, and C; the type of tile 5 is based on tiles D, E, and an
 * imaginary "safe" tile. Let's call these three tiles from the previous row the left, center, and right tiles,
 * respectively. Then, a new tile is a trap only in one of the following situations:
 * 
 * Its left and center tiles are traps, but its right tile is not.
 * Its center and right tiles are traps, but its left tile is not.
 * Only its left tile is a trap.
 * Only its right tile is a trap.
 * 
 * In any other situation, the new tile is safe.
 * Then, starting with the row ..^^., you can determine the next row by applying those rules to each new tile:
 * 
 * The leftmost character on the next row considers the left (nonexistent, so we assume "safe"), center (the first .,
 * which means "safe"), and right (the second ., also "safe") tiles on the previous row. Because all of the trap rules
 * require a trap in at least one of the previous three tiles, the first tile on this new row is also safe, ..
 * The second character on the next row considers its left (.), center (.), and right (^) tiles from the previous row.
 * This matches the fourth rule: only the right tile is a trap. Therefore, the next tile in this new row is a trap, ^.
 * The third character considers .^^, which matches the second trap rule: its center and right tiles are traps, but its
 * left tile is not. Therefore, this tile is also a trap, ^.
 * The last two characters in this new row match the first and third rules, respectively, and so they are both also
 * traps, ^.
 * 
 * After these steps, we now know the next row of tiles in the room: .^^^^. Then, we continue on to the next row, using
 * the same rules, and get ^^..^. After determining two new rows, our map looks like this:
 * ..^^.
 * .^^^^
 * ^^..^
 * 
 * Here's a larger example with ten tiles per row and ten rows:
 * .^^.^.^^^^
 * ^^^...^..^
 * ^.^^.^.^^.
 * ..^^...^^^
 * .^^^^.^^.^
 * ^^..^.^^..
 * ^^^^..^^^.
 * ^..^^^^.^^
 * .^^^..^.^^
 * ^^.^^^..^^
 * 
 * In ten rows, this larger example has 38 safe tiles.
 * Starting with the map in your puzzle input, in a total of 40 rows (including the starting row), how many safe tiles
 * are there?
 * 
 * --- Part Two ---
 * How many safe tiles are there in a total of 400000 rows?
 * 
 */
public class TrapRoom {

	private final Row firstRow;

	public TrapRoom(String firstRow) {
		this.firstRow = new Row(firstRow);
	}

	public String toString(int rows) {
		return rows() //
				.limit(rows) //
				.map(Row::toString) //
				.collect(joining("\n"));
	}

	public long safeTiles(int rows) {
		return rows() //
				.limit(rows) //
				.mapToLong(Row::safeTileCount) //
				.sum();
	}

	private Stream<Row> rows() {
		return Stream.iterate(firstRow, Row::next);
	}

	private static class Row {

		private static final char SAFE_CHAR = '.';
		private static final char TRAP_CHAR = '^';

		// True means is trap, false means is safe.
		private final List<Boolean> tiles;

		public Row(String repr) {
			this(repr.chars() //
					.mapToObj(Row::isTrap) //
					.collect(toList()));
		}

		private Row(List<Boolean> tiles) {
			this.tiles = tiles;
		}

		private static boolean isTrap(int c) {
			return isTrap((char) c);
		}

		private static boolean isTrap(char c) {
			if (SAFE_CHAR == c) {
				return false;
			} else if (TRAP_CHAR == c) {
				return true;
			} else {
				throw new IllegalArgumentException("Unrecognized character " + c);
			}
		}

		public Row next() {
			List<Boolean> newTiles = IntStream.range(0, tiles.size()) //
					.mapToObj(i -> newTileIsTrap(this.get(i - 1), this.get(i), this.get(i + 1))) //
					.collect(toList());

			return new Row(newTiles);
		}

		long safeTileCount() {
			return tiles.stream() //
					.filter(isTrap -> !isTrap) //
					.count();
		}

		/**
		 * Is there a trap at index i?
		 */
		private boolean get(int i) {
			if (i < 0 || i >= tiles.size()) {
				return false;
			}

			return tiles.get(i);
		}

		private static boolean newTileIsTrap(boolean left, boolean center, boolean right) {
			return (left && center && !right) || (center && right && !left) || (left && !center && !right)
					|| (right && !center && !left);

		}

		public String toString() {
			return tiles.stream() //
					.map(t -> t ? TRAP_CHAR : SAFE_CHAR) //
					.collect(charsToString());
		}

	}

	public static void main(String[] args) throws IOException {
		File inputFile = new File("src/main/java/advent/year2016/day18/input.txt");
		String input = FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8).trim();

		TrapRoom room = new TrapRoom(input);

		System.out.println(room.safeTiles(40));
		System.out.println(room.safeTiles(400000));
	}

}