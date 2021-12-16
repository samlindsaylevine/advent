package advent.year2016.day24;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import com.google.common.collect.Collections2;

import advent.utils.Pair;
import advent.year2016.day13.Maze;

/**
 * --- Day 24: Air Duct Spelunking ---
 * You've finally met your match; the doors that provide access to the roof are locked tight, and all of the controls
 * and related electronics are inaccessible. You simply can't reach them.
 * The robot that cleans the air ducts, however, can.
 * It's not a very fast little robot, but you reconfigure it to be able to interface with some of the exposed wires
 * that have been routed through the HVAC system. If you can direct it to each of those locations, you should be able
 * to bypass the security controls.
 * You extract the duct layout for this area from some blueprints you acquired and create a map with the relevant
 * locations marked (your puzzle input). 0 is your current location, from which the cleaning robot embarks; the other
 * numbers are (in no particular order) the locations the robot needs to visit at least once each. Walls are marked as
 * #, and open passages are marked as .. Numbers behave like open passages.
 * For example, suppose you have a map like the following:
 * ###########
 * #0.1.....2#
 * #.#######.#
 * #4.......3#
 * ###########
 * 
 * To reach all of the points of interest as quickly as possible, you would have the robot take the following path:
 * 
 * 0 to 4 (2 steps)
 * 4 to 1 (4 steps; it can't move diagonally)
 * 1 to 2 (6 steps)
 * 2 to 3 (2 steps)
 * 
 * Since the robot isn't very fast, you need to find it the shortest route. This path is the fewest steps (in the above
 * example, a total of 14) required to start at 0 and then visit every other location at least once.
 * Given your actual map, and starting from location 0, what is the fewest number of steps required to visit every
 * non-0 number marked on the map at least once?
 * 
 * --- Part Two ---
 * Of course, if you leave the cleaning robot somewhere weird, someone is bound to notice.
 * What is the fewest number of steps required to start at 0, visit every non-0 number marked on the map at least once,
 * and then return to 0?
 * 
 */
public class AirDuctMaze extends Maze {

	// True if open, false if closed.
	private List<List<Boolean>> spaces = new ArrayList<>();
	private Map<Integer, Address> pointsOfInterest = new HashMap<>();

	// Cached distances between points of interest - keys are a (from ID, to ID)
	// pair; values are the # of steps.
	private Map<Pair<Integer, Integer>, Integer> cachedDistances = new HashMap<>();

	public AirDuctMaze(List<String> rows) {

		for (int y = 0; y < rows.size(); y++) {
			String row = rows.get(y);
			List<Boolean> spaceRow = new ArrayList<>();
			for (int x = 0; x < row.length(); x++) {
				char c = row.charAt(x);
				if (c == '.') {
					spaceRow.add(true);
				} else if (c == '#') {
					spaceRow.add(false);
				} else {
					spaceRow.add(true);
					pointsOfInterest.put(Integer.parseInt(String.valueOf(c)), new Address(x, y));
				}
			}
			spaces.add(spaceRow);
		}
	}

	@Override
	protected boolean isOpen(int x, int y) {
		if (x < 0 || y < 0 || y >= spaces.size()) {
			return false;
		}

		List<Boolean> row = spaces.get(y);
		if (x >= row.size()) {
			return false;
		}

		return row.get(x);
	}

	public int shortestLengthToVisitAll() {
		return shortestLength(nonZeros -> cons(0, nonZeros));
	}

	public int shortestLengthToVistAllAndReturn() {
		return shortestLength(nonZeros -> cons(0, append(nonZeros, 0)));
	}

	/**
	 * Finds the shortest length path, starting at 0.
	 * 
	 * @param nonZerosToAll
	 *            Given a permutation of the non-zero points of interest, what
	 *            is our total path including 0?
	 */
	private int shortestLength(UnaryOperator<List<Integer>> nonZerosToAll) {
		Set<Integer> nonZeroPoints = pointsOfInterest.keySet().stream() //
				.filter(i -> i != 0) //
				.collect(toSet());

		Collection<List<Integer>> possiblePointOrders = Collections2.permutations(nonZeroPoints);

		return possiblePointOrders.stream() //
				.map(nonZerosToAll::apply) //
				.mapToInt(this::pathDistance) //
				.min() //
				.getAsInt();
	}

	private static <T> List<T> cons(T element, List<T> rest) {
		ArrayList<T> output = new ArrayList<>();
		output.add(element);
		output.addAll(rest);
		return output;
	}

	private static <T> List<T> append(List<T> list, T element) {
		ArrayList<T> output = new ArrayList<>();
		output.addAll(list);
		output.add(element);
		return output;
	}

	private int pathDistance(List<Integer> points) {
		return IntStream.range(0, points.size() - 1) //
				.map(i -> loadDistance(points.get(i), points.get(i + 1))) //
				.sum();
	}

	/**
	 * Loads the distance between two points of interest, using cached values if
	 * this distance has previously been calculated.
	 */
	private int loadDistance(int pointFrom, int pointTo) {
		Pair<Integer, Integer> key = Pair.of(pointFrom, pointTo);

		return cachedDistances.computeIfAbsent(key, pair -> this.calculateDistance(pair.getFirst(), pair.getSecond()));
	}

	/**
	 * Calculates the distance between two points of interest.
	 */
	private int calculateDistance(int pointFrom, int pointTo) {

		Address from = pointsOfInterest.get(pointFrom);
		Address to = pointsOfInterest.get(pointTo);

		// Optional debug information.
		// System.out.println("Calculating distance from " + pointFrom + " (" +
		// from + ") to " + pointTo + "(" + to + ")");

		return this.pathLength(from.x, from.y, to.x, to.y);
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day24/input.txt");
		List<String> lines = Files.readAllLines(inputFilePath);

		AirDuctMaze maze = new AirDuctMaze(lines);

		System.out.println(maze.shortestLengthToVisitAll());
		System.out.println(maze.shortestLengthToVistAllAndReturn());
	}

}