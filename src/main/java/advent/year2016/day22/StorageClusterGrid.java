package advent.year2016.day22;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StorageClusterGrid {

	private final Map<Position, Node> nodes;

	public StorageClusterGrid(Stream<String> lines) {
		this.nodes = lines //
				.map(Node::tryParse) //
				.filter(Optional::isPresent) //
				.map(Optional::get) //
				.collect(toMap(n -> n.position, n -> n));
	}

	public long validPairs() {
		return nodes.values().stream() //
				.filter(n -> n.usedTerabytes != 0) //
				.mapToLong(n -> this.validTargets(n).count()) //
				.sum();
	}

	public Optional<Node> get(Position position) {
		return Optional.ofNullable(nodes.get(position));
	}

	private Stream<Node> validTargets(Node source) {
		return nodes.values().stream() //
				.filter(n -> !n.equals(source)) //
				.filter(n -> n.availableTerabytes >= source.usedTerabytes);
	}

	private Stream<Node> adjacent(Node node) {
		Stream<Position> positions = Stream.of( //
				new Position(node.position.x, node.position.y - 1), //
				new Position(node.position.x + 1, node.position.y), //
				new Position(node.position.x, node.position.y + 1), //
				new Position(node.position.x - 1, node.position.y));

		return positions //
				.map(this::get) //
				.filter(Optional::isPresent) //
				.map(Optional::get);
	}

	static class Node {

		final Position position;

		final int usedTerabytes;
		final int availableTerabytes;

		private static Pattern PATTERN = Pattern.compile(".*node-x(\\d+)-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T.*");

		private Node(int x, int y, int usedTerabytes, int availableTerabytes) {
			this.position = new Position(x, y);
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

				return Optional.of(new Node(x, y, used, avail));
			} else {
				return Optional.empty();
			}
		}
	}

	static class Position {
		public final int x;
		public final int y;

		public Position(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
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
			Position other = (Position) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	public static void main(String[] args) throws IOException {
		Path inputFilePath = Paths.get("src/main/java/advent/year2016/day22/input.txt");

		try (Stream<String> lines = Files.lines(inputFilePath)) {
			StorageClusterGrid grid = new StorageClusterGrid(lines);
			System.out.println(grid.validPairs());
		}
	}
}
