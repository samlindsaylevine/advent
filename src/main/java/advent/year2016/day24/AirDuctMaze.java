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
import java.util.stream.IntStream;

import com.google.common.collect.Collections2;

import advent.utils.Pair;
import advent.year2016.day13.Maze;

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
		Set<Integer> nonZeroPoints = pointsOfInterest.keySet().stream() //
				.filter(i -> i != 0) //
				.collect(toSet());

		Collection<List<Integer>> possiblePointOrders = Collections2.permutations(nonZeroPoints);

		return possiblePointOrders.stream() //
				.map(nonZero -> cons(0, nonZero)) //
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
		System.out.println("Calculating distance from " + pointFrom + " (" + from + ") to " + pointTo + "(" + to + ")");

		return this.pathLength(from.x, from.y, to.x, to.y);
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day24/input.txt");
		List<String> lines = Files.readAllLines(inputFilePath);

		AirDuctMaze maze = new AirDuctMaze(lines);

		System.out.println(maze.shortestLengthToVisitAll());

	}

}
