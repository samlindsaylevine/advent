package advent.year2016.day13;

import static java.util.stream.Collectors.joining;

import java.util.stream.IntStream;

/**
 * --- Day 13: A Maze of Twisty Little Cubicles ---
 * You arrive at the first floor of this new building to discover a much less welcoming environment than the shiny
 * atrium of the last one.  Instead, you are in a maze of twisty little cubicles, all alike.
 * Every location in this area is addressed by a pair of non-negative integers (x,y). Each such coordinate is either a
 * wall or an open space. You can't move diagonally. The cube maze starts at 0,0 and seems to extend infinitely toward
 * positive x and y; negative values are invalid, as they represent a location outside the building. You are in a small
 * waiting area at 1,1.
 * While it seems chaotic, a nearby morale-boosting poster explains, the layout is actually quite logical. You can
 * determine whether a given x,y coordinate will be a wall or an open space using a simple system:
 * 
 * Find x*x + 3*x + 2*x*y + y + y*y.
 * Add the office designer's favorite number (your puzzle input).
 * Find the binary representation of that sum; count the number of bits that are 1.
 * 
 * If the number of bits that are 1 is even, it's an open space.
 * If the number of bits that are 1 is odd, it's a wall.
 * 
 * 
 * 
 * For example, if the office designer's favorite number were 10, drawing walls as # and open spaces as ., the corner
 * of the building containing 0,0 would look like this:
 *   0123456789
 * 0 .#.####.##
 * 1 ..#..#...#
 * 2 #....##...
 * 3 ###.#.###.
 * 4 .##..#..#.
 * 5 ..##....#.
 * 6 #...##.###
 * 
 * Now, suppose you wanted to reach 7,4. The shortest route you could take is marked as O:
 *   0123456789
 * 0 .#.####.##
 * 1 .O#..#...#
 * 2 #OOO.##...
 * 3 ###O#.###.
 * 4 .##OO#OO#.
 * 5 ..##OOO.#.
 * 6 #...##.###
 * 
 * Thus, reaching 7,4 would take a minimum of 11 steps (starting from your current location, 1,1).
 * What is the fewest number of steps required for you to reach 31,39?
 * 
 * --- Part Two ---
 * How many locations (distinct x,y coordinates, including your starting location) can you reach in at most 50 steps?
 * 
 */
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