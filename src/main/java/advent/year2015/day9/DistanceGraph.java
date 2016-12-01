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
		Files.lines(Paths.get("src/main/java/advent/day9/input.txt")) //
				.forEach(graph::addEdge);
		return graph;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(graphFromFile().longestRouteLength());
	}

}
