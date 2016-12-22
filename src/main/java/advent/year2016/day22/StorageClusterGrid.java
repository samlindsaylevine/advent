package advent.year2016.day22;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import advent.utils.Pair;

public class StorageClusterGrid {

	private final Map<Position, Node> nodes;
	private final Position importantDataPosition;

	public StorageClusterGrid(Stream<String> lines) {
		this(lines //
				.map(Node::tryParse) //
				.filter(Optional::isPresent) //
				.map(Optional::get) //
				.collect(toMap(n -> n.position, n -> n)));
	}

	private StorageClusterGrid(Map<Position, Node> nodes) {
		this.nodes = nodes;
		int maxX = max(nodes.values(), Position::getX);

		this.importantDataPosition = new Position(maxX, 0);
	}

	StorageClusterGrid(Map<Position, Node> nodes, Position importantDataPosition) {
		this.nodes = nodes;
		this.importantDataPosition = importantDataPosition;
	}

	private static int max(Collection<Node> nodes, ToIntFunction<Position> function) {
		return max(nodes.stream(), function);
	}

	private static int max(Stream<Node> nodes, ToIntFunction<Position> function) {
		return nodes //
				.map(n -> n.position) //
				.mapToInt(function) //
				.max() //
				.getAsInt();
	}

	/**
	 * The number of pairs of nodes that *could* be used as a source and target,
	 * if they were adjacent.
	 * 
	 * @return
	 */
	public long validPairCount() {
		return allMovablePairs().count();
	}

	// All fully legal moves: movable and adjacent.
	Stream<Pair<Node, Node>> legalMovePairs() {
		return adjacentPairs() //
				.filter(pair -> this.isMovable(pair.getFirst(), pair.getSecond()));
	}

	// The result of making a move.
	StorageClusterGrid moving(Node source, Node target) {

		Node newSource = new Node(source.position, 0, source.usedTerabytes + source.availableTerabytes);
		Node newTarget = new Node(target.position, source.usedTerabytes + target.usedTerabytes,
				target.availableTerabytes - source.usedTerabytes);

		Position newDataPosition = this.importantDataPosition.equals(source.position) ? target.position
				: this.importantDataPosition;

		Map<Position, Node> newNodes = new HashMap<>(nodes);
		newNodes.put(newSource.position, newSource);
		newNodes.put(newTarget.position, newTarget);

		return new StorageClusterGrid(newNodes, newDataPosition);
	}

	// All possible results from making a move from this position.
	private Stream<StorageClusterGrid> legalMoves() {
		return this.legalMovePairs() //
				.map(pair -> this.moving(pair.getFirst(), pair.getSecond()));
	}

	private boolean isMovable(Node source, Node target) {
		return !source.equals(target) && //
				source.usedTerabytes != 0 && //
				target.availableTerabytes >= source.usedTerabytes;
	}

	// All pairs of nodes (including self to self).
	private Stream<Pair<Node, Node>> allPairs() {
		return nodes.values().stream().flatMap(this::pairs);
	}

	Stream<Pair<Node, Node>> adjacentPairs() {
		int maxX = max(nodes.values(), Position::getX);
		int maxY = max(nodes.values(), Position::getY);

		return IntStream.rangeClosed(0, maxX) //
				.boxed() //
				.flatMap(x -> IntStream.rangeClosed(0, maxY) //
						.mapToObj(y -> new Position(x, y))) //
				.map(this::get) //
				.map(Optional::get) //
				.flatMap(source -> adjacent(source).map(target -> Pair.of(source, target)));
	}

	private Stream<Node> adjacent(Node node) {
		Position p = node.position;
		Stream<Position> positions = Stream.of( //
				new Position(p.getX() - 1, p.getY()), //
				new Position(p.getX() + 1, p.getY()), //
				new Position(p.getX(), p.getY() - 1), //
				new Position(p.getX(), p.getY() + 1));

		return positions.map(this::get) //
				.filter(Optional::isPresent) //
				.map(Optional::get);
	}

	private Stream<Pair<Node, Node>> pairs(Node source) {
		return nodes.values().stream() //
				.map(target -> Pair.of(source, target));
	}

	// All pairs that are theoretically movable (regardless of adjacency).
	private Stream<Pair<Node, Node>> allMovablePairs() {
		return allPairs() //
				.filter(p -> this.isMovable(p.getFirst(), p.getSecond()));
	}

	public Optional<Node> get(Position position) {
		return Optional.ofNullable(nodes.get(position));
	}

	// If true, we can access the important data.
	private boolean dataAccessible() {
		return importantDataPosition.equals(new Position(0, 0));
	}

	/**
	 * This general purpose breadth-first-search solution works correctly for a
	 * small data set but does not complete in a reasonable period of time for
	 * our problem input.
	 */
	public Optional<Integer> stepsToGetData() {

		Set<StorageClusterGrid> currentGrids = ImmutableSet.of(this);
		Set<StorageClusterGrid> visited = new HashSet<>(currentGrids);
		int stepsElapsed = 0;

		while (!currentGrids.isEmpty()) {
			if (currentGrids.stream().anyMatch(StorageClusterGrid::dataAccessible)) {
				return Optional.of(stepsElapsed);
			}

			currentGrids = currentGrids.parallelStream() //
					.flatMap(StorageClusterGrid::legalMoves) //
					.filter(grid -> !visited.contains(grid)) //
					.collect(toSet());

			stepsElapsed++;
			visited.addAll(currentGrids);
		}

		return Optional.empty();
	}

	/**
	 * The general purpose solution doesn't seem to work pleasantly for our data
	 * set - we never complete in a reasonable time. Let's try out an approach
	 * using a lot of simplifying assumptions!
	 * 
	 * Looking at our dataset, we see there is only one node that has > 30TB
	 * available - the node with 0 TB stored.
	 * 
	 * There are no nodes other than that node that have < 64 TB stored! Of
	 * those nodes, none of them have > 128 TB total capacity - so there is NO
	 * ability to move data from any node into anything other than the empty
	 * node!
	 * 
	 * So, for this specific data set, all of our moves are just to move the
	 * hole onto the important destination, and then to move it onto the upper
	 * left.
	 * 
	 * I think that the example in the 2nd half is leading us down this way,
	 * with its suggestion about the very large, very full node represented as
	 * "#" and the empty node as "_". We won't be able to move the empty node
	 * through the very large one so we'll need to find a path.
	 * 
	 * In practice when we print out our sample data it looks like:
	 * 
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......################################
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ......................................
	 * ................_.....................
	 * ......................................
	 * ......................................
	 */
	public String toStringUsingAssumptions() {
		int maxX = max(nodes.values(), Position::getX);
		int maxY = max(nodes.values(), Position::getY);

		return IntStream.rangeClosed(0, maxY) //
				.mapToObj(y -> this.getRow(y, maxX)) //
				.collect(joining("\n"));
	}

	private String getRow(int y, int maxX) {
		return IntStream.rangeClosed(0, maxX) //
				.mapToObj(x -> this.get(new Position(x, y))) //
				.map(Optional::get) //
				.map(this::visualRep) //
				.collect(joining(""));
	}

	private String visualRep(Node node) {
		if (node.usedTerabytes == 0) {
			return "_";
		} else if (node.usedTerabytes > 100) {
			return "#";
		} else {
			return ".";
		}
	}

	/**
	 * Steps taken to get our data where we want it using the above very
	 * simplified assumptions.
	 */
	public int stepsToGetDataUsingAssumptions() {
		// We're going to assume that we have a horizontal wall of "full nodes"
		// somewhere in the data set.
		int maxX = max(nodes.values(), Position::getX);

		Node emptyNode = nodes.values().stream() //
				.filter(node -> node.usedTerabytes == 0) //
				.findAny() //
				.get();

		int minWallX = nodes.values().stream() //
				.filter(node -> node.usedTerabytes > emptyNode.availableTerabytes) //
				.mapToInt(node -> node.position.getX()) //
				.min() //
				.getAsInt();
		int wallY = nodes.values().stream() //
				.filter(node -> node.usedTerabytes > emptyNode.availableTerabytes) //
				.findAny() //
				.get().position.getY();

		int stepsToUpperRight = (maxX - emptyNode.position.getX()) + emptyNode.position.getY();

		// If the empty node is below and to the right of the horizontal wall,
		// we have to dodge around it.
		if (emptyNode.position.getY() > wallY && emptyNode.position.getX() >= minWallX) {
			int stepsToDodge = emptyNode.position.getX() - minWallX + 1;
			stepsToUpperRight += 2 * stepsToDodge;
		}

		// Following the example steps, once the empty node is at the upper
		// right, it takes 5 moves every time we want to move the goal data one
		// space to the left.
		int spacesToMove = maxX - 1;

		int stepsToMoveHome = 5 * spacesToMove;

		return stepsToUpperRight + stepsToMoveHome;
	}

	static class Node {

		final Position position;

		final int usedTerabytes;
		final int availableTerabytes;

		private static Pattern PATTERN = Pattern.compile(".*node-x(\\d+)-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T.*");

		Node(Position position, int usedTerabytes, int availableTerabytes) {
			this.position = position;
			this.usedTerabytes = usedTerabytes;
			this.availableTerabytes = availableTerabytes;
		}

		public static Optional<Node> tryParse(String input) {
			Matcher matcher = PATTERN.matcher(input);

			if (matcher.matches()) {
				int x = Integer.valueOf(matcher.group(1));
				int y = Integer.valueOf(matcher.group(2));
				// Skipping the "size" group.
				int used = Integer.valueOf(matcher.group(4));
				int avail = Integer.valueOf(matcher.group(5));

				Position position = new Position(x, y);
				return Optional.of(new Node(position, used, avail));
			} else {
				return Optional.empty();
			}
		}

		@Override
		public String toString() {
			return "Node [position=" + position + ", usedTerabytes=" + usedTerabytes + ", availableTerabytes="
					+ availableTerabytes + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + availableTerabytes;
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			result = prime * result + usedTerabytes;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (availableTerabytes != other.availableTerabytes)
				return false;
			if (position == null) {
				if (other.position != null)
					return false;
			} else if (!position.equals(other.position))
				return false;
			if (usedTerabytes != other.usedTerabytes)
				return false;
			return true;
		}
	}

	static class Position extends Pair<Integer, Integer> {

		protected Position(Integer first, Integer second) {
			super(first, second);
		}

		public int getX() {
			return this.getFirst();
		}

		public int getY() {
			return this.getSecond();
		}

	}

	@Override
	public String toString() {
		return "StorageClusterGrid [nodes=" + nodes + ", importantDataPosition=" + importantDataPosition + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((importantDataPosition == null) ? 0 : importantDataPosition.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StorageClusterGrid other = (StorageClusterGrid) obj;
		if (importantDataPosition == null) {
			if (other.importantDataPosition != null)
				return false;
		} else if (!importantDataPosition.equals(other.importantDataPosition))
			return false;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		return true;
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day22/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			StorageClusterGrid grid = new StorageClusterGrid(lines);
			System.out.println(grid.validPairCount());
			System.out.println(grid.stepsToGetDataUsingAssumptions());
		}
	}
}
