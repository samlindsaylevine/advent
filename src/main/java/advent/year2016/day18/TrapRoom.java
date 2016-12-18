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
	}

}
