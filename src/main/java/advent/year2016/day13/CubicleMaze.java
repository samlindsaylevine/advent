package advent.year2016.day13;

import static java.util.stream.Collectors.joining;

import java.util.stream.IntStream;

public class CubicleMaze extends Maze {

	private final int favoriteNumber;

	public CubicleMaze(int favoriteNumber) {
		this.favoriteNumber = favoriteNumber;
	}

	@Override
	protected boolean isOpen(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}

		int simpleSystemCalculation = x * x + 3 * x + 2 * x * y + y + y * y + this.favoriteNumber;
		String binaryString = Integer.toBinaryString(simpleSystemCalculation);
		long numOneBits = binaryString.chars().filter(i -> i == '1').count();
		return numOneBits % 2 == 0;
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

	public static void main(String[] args) {
		CubicleMaze maze = new CubicleMaze(1352);
		System.out.println(maze.pathLength(1, 1, 31, 39));
		System.out.println(maze.locationsReachable(1, 1, 50));
	}
}
