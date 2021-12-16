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

/**
 * --- Day 22: Grid Computing ---
 * You gain access to a massive storage cluster arranged in a grid; each storage node is only connected to the four
 * nodes directly adjacent to it (three if the node is on an edge, two if it's in a corner).
 * You can directly access data only on node /dev/grid/node-x0-y0, but you can perform some limited actions on the
 * other nodes:
 * 
 * You can get the disk usage of all nodes (via df). The result of doing this is in your puzzle input.
 * You can instruct a node to move (not copy) all of its data to an adjacent node (if the destination node has enough
 * space to receive the data). The sending node is left empty after this operation.
 * 
 * Nodes are named by their position: the node named node-x10-y10 is adjacent to nodes node-x9-y10, node-x11-y10,
 * node-x10-y9, and node-x10-y11.
 * Before you begin, you need to understand the arrangement of data on these nodes. Even though you can only move data
 * between directly connected nodes, you're going to need to rearrange a lot of the data to get access to the data you
 * need.  Therefore, you need to work out how you might be able to shift data around.
 * To do this, you'd like to count the number of viable pairs of nodes.  A viable pair is any two nodes (A,B),
 * regardless of whether they are directly connected, such that:
 * 
 * Node A is not empty (its Used is not zero).
 * Nodes A and B are not the same node.
 * The data on node A (its Used) would fit on node B (its Avail).
 * 
 * How many viable pairs of nodes are there?
 * 
 * --- Part Two ---
 * Now that you have a better understanding of the grid, it's time to get to work.
 * Your goal is to gain access to the data which begins in the node with y=0 and the highest x (that is, the node in
 * the top-right corner).
 * For example, suppose you have the following grid:
 * Filesystem            Size  Used  Avail  Use%
 * /dev/grid/node-x0-y0   10T    8T     2T   80%
 * /dev/grid/node-x0-y1   11T    6T     5T   54%
 * /dev/grid/node-x0-y2   32T   28T     4T   87%
 * /dev/grid/node-x1-y0    9T    7T     2T   77%
 * /dev/grid/node-x1-y1    8T    0T     8T    0%
 * /dev/grid/node-x1-y2   11T    7T     4T   63%
 * /dev/grid/node-x2-y0   10T    6T     4T   60%
 * /dev/grid/node-x2-y1    9T    8T     1T   88%
 * /dev/grid/node-x2-y2    9T    6T     3T   66%
 * 
 * In this example, you have a storage grid 3 nodes wide and 3 nodes tall.  The node you can access directly,
 * node-x0-y0, is almost full. The node containing the data you want to access, node-x2-y0 (because it has y=0 and the
 * highest x value), contains 6 terabytes of data - enough to fit on your node, if only you could make enough space to
 * move it there.
 * Fortunately, node-x1-y1 looks like it has enough free space to enable you to move some of this data around.  In
 * fact, it seems like all of the nodes have enough space to hold any node's data (except node-x0-y2, which is much
 * larger, very full, and not moving any time soon). So, initially, the grid's capacities and connections look like
 * this:
 * ( 8T/10T) --  7T/ 9T -- [ 6T/10T]
 *     |           |           |
 *   6T/11T  --  0T/ 8T --   8T/ 9T
 *     |           |           |
 *  28T/32T  --  7T/11T --   6T/ 9T
 * 
 * The node you can access directly is in parentheses; the data you want starts in the node marked by square brackets.
 * In this example, most of the nodes are interchangable: they're full enough that no other node's data would fit, but
 * small enough that their data could be moved around. Let's draw these nodes as .. The exceptions are the empty node,
 * which we'll draw as _, and the very large, very full node, which we'll draw as #. Let's also draw the goal data as
 * G. Then, it looks like this:
 * (.) .  G
 *  .  _  .
 *  #  .  .
 * 
 * The goal is to move the data in the top right, G, to the node in parentheses. To do this, we can issue some commands
 * to the grid and rearrange the data:
 * 
 * Move data from node-y0-x1 to node-y1-x1, leaving node node-y0-x1 empty:(.) _  G
 *  .  .  .
 *  #  .  .
 * 
 * Move the goal data from node-y0-x2 to node-y0-x1:(.) G  _
 *  .  .  .
 *  #  .  .
 * 
 * At this point, we're quite close. However, we have no deletion command, so we have to move some more data around.
 * So, next, we move the data from node-y1-x2 to node-y0-x2:(.) G  .
 *  .  .  _
 *  #  .  .
 * 
 * Move the data from node-y1-x1 to node-y1-x2:(.) G  .
 *  .  _  .
 *  #  .  .
 * 
 * Move the data from node-y1-x0 to node-y1-x1:(.) G  .
 *  _  .  .
 *  #  .  .
 * 
 * Next, we can free up space on our node by moving the data from node-y0-x0 to node-y1-x0:(_) G  .
 *  .  .  .
 *  #  .  .
 * 
 * 
 * Finally, we can access the goal data by moving the it from node-y0-x1 to node-y0-x0:(G) _  .
 *  .  .  .
 *  #  .  .
 * 
 * 
 * 
 * So, after 7 steps, we've accessed the data we want. Unfortunately, each of these moves takes time, and we need to be
 * efficient:
 * What is the fewest number of steps required to move your goal data to node-x0-y0?
 * 
 */
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