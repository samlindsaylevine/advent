package advent.year2015.day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.google.common.collect.Collections2;

/**
 * --- Day 9: All in a Single Night ---
 * Every year, Santa manages to deliver all of his presents in a single night.
 * This year, however, he has some new locations to visit; his elves have provided him the distances between every pair
 * of locations.  He can start and end at any two (different) locations he wants, but he must visit each location
 * exactly once.  What is the shortest distance he can travel to achieve this?
 * For example, given the following distances:
 * London to Dublin = 464
 * London to Belfast = 518
 * Dublin to Belfast = 141
 * 
 * The possible routes are therefore:
 * Dublin -> London -> Belfast = 982
 * London -> Dublin -> Belfast = 605
 * London -> Belfast -> Dublin = 659
 * Dublin -> Belfast -> London = 659
 * Belfast -> Dublin -> London = 605
 * Belfast -> London -> Dublin = 982
 * 
 * The shortest of these is London -> Dublin -> Belfast = 605, and so the answer is 605 in this example.
 * What is the distance of the shortest route?
 * 
 * --- Part Two ---
 * The next year, just to show off, Santa decides to take the route with the longest distance instead.
 * He can still start and end at any two (different) locations he wants, and he still must visit each location exactly
 * once.
 * For example, given the distances above, the longest route would be 982 via (for example) Dublin -> London -> Belfast.
 * What is the distance of the longest route?
 * 
 */
public class DistanceGraph {

	private Map<String, Map<String, Integer>> distances = new HashMap<>();

	public void addEdge(String representation) {
		Edge edge = new Edge(representation);

		this.addOneWayEdge(edge.from, edge.to, edge.distance);
		this.addOneWayEdge(edge.to, edge.from, edge.distance);
	}

	private void addOneWayEdge(String from, String to, int distance) {
		Map<String, Integer> forFromPoint = this.distances.computeIfAbsent(from, key -> new HashMap<>());
		forFromPoint.put(to, distance);
	}

	private IntStream routeLengths() {
		return Collections2.permutations(this.distances.keySet()) //
				.stream() //
				.mapToInt(this::routeLength);
	}

	public int shortestRouteLength() {
		return this.routeLengths().min().getAsInt();
	}

	public int longestRouteLength() {
		return this.routeLengths().max().getAsInt();
	}

	private int edgeLength(String from, String to) {
		return this.distances.get(from).get(to);
	}

	private int routeLength(List<String> route) {
		int length = 0;

		for (int i = 0; i < route.size() - 1; i++) {
			length += this.edgeLength(route.get(i), route.get(i + 1));
		}

		return length;
	}

	private static class Edge {
		private final String from;
		private final String to;
		private final int distance;

		private static final Pattern REGEX = Pattern.compile("(\\w+) to (\\w+) = (\\d+)");

		public Edge(String representation) {
			Matcher matcher = REGEX.matcher(representation);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(representation);
			}

			this.from = matcher.group(1);
			this.to = matcher.group(2);
			this.distance = Integer.valueOf(matcher.group(3));
		}
	}

	private static DistanceGraph graphFromFile() throws IOException {
		DistanceGraph graph = new DistanceGraph();
		Files.lines(Paths.get("src/main/java/advent/year2015/day9/input.txt")) //
				.forEach(graph::addEdge);
		return graph;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(graphFromFile().longestRouteLength());
	}

}