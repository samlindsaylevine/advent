package advent.year2016.day22;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import advent.year2016.day22.StorageClusterGrid.Node;
import advent.year2016.day22.StorageClusterGrid.Position;

public class StorageClusterGridTest {

	@Test
	public void nodeRep() {
		String input = "/dev/grid/node-x37-y25   92T   73T    19T   79%";
		Optional<Node> result = Node.tryParse(input);
		Node node = result.orElseThrow(() -> new NoSuchElementException("Should have successfully parsed node"));

		assertEquals(37, node.position.getX());
		assertEquals(25, node.position.getY());
		assertEquals(73, node.usedTerabytes);
		assertEquals(19, node.availableTerabytes);
	}

	@Test
	public void nodeNoMatch() {
		String input = "root@ebhq-gridcenter# df -h";
		Optional<Node> result = Node.tryParse(input);
		assertFalse(result.isPresent());
	}

	@Test
	public void validPairs() {
		Stream<String> input = Stream.of( //
				"root@ebhq-gridcenter# df -h", //
				"Filesystem              Size  Used  Avail  Use%", //
				"/dev/grid/node-x0-y0     10T   2T    8T    20%", //
				"/dev/grid/node-x0-y1     10T   9T    1T    90%", //
				"/dev/grid/node-x0-y2     10T   4T    6T    40%", //
				"/dev/grid/node-x0-y3     10T   0T    10T    0%");

		StorageClusterGrid grid = new StorageClusterGrid(input);

		// Valid pairs:
		// (0, 0) -> (0, 2)
		// (0, 0) -> (0, 3)
		// (0, 1) -> (0, 3)
		// (0, 2) -> (0, 0)
		// (0, 2) -> (0, 3);

		assertEquals(5, grid.validPairCount());
	}

	private StorageClusterGrid partTwoGrid() {
		Stream<String> input = Stream.of( //
				"Filesystem            Size  Used  Avail  Use%", //
				"/dev/grid/node-x0-y0   10T    8T     2T   80%", //
				"/dev/grid/node-x0-y1   11T    6T     5T   54%", //
				"/dev/grid/node-x0-y2   32T   28T     4T   87%", //
				"/dev/grid/node-x1-y0    9T    7T     2T   77%", //
				"/dev/grid/node-x1-y1    8T    0T     8T    0%", //
				"/dev/grid/node-x1-y2   11T    7T     4T   63%", //
				"/dev/grid/node-x2-y0   10T    6T     4T   60%", //
				"/dev/grid/node-x2-y1    9T    8T     1T   88%", //
				"/dev/grid/node-x2-y2    9T    6T     3T   66%");

		return new StorageClusterGrid(input);
	}

	@Test
	public void stepsToGetData() {
		StorageClusterGrid grid = partTwoGrid();

		assertEquals(Optional.of(7), grid.stepsToGetData());
	}

	@Test
	public void stepsUsingAssumptions() {
		assertEquals(7, partTwoGrid().stepsToGetDataUsingAssumptions());
	}

	@Test
	public void equals() {
		Position a = new Position(1, 2);
		Position b = new Position(1, 2);
		assertEquals(a, b);

		Node m = new Node(a, 4, 7);
		Node n = new Node(b, 4, 7);
		assertEquals(m, n);

		StorageClusterGrid f = new StorageClusterGrid(ImmutableMap.of(a, m), a);
		StorageClusterGrid g = new StorageClusterGrid(ImmutableMap.of(b, n), b);
		assertEquals(f, g);
	}

	@Test
	public void moving() {
		StorageClusterGrid grid = partTwoGrid();

		grid = move(grid, 0, 1, 1, 1);
		assertEquals(0, grid.get(new Position(0, 1)).get().usedTerabytes);
		assertEquals(11, grid.get(new Position(0, 1)).get().availableTerabytes);
		assertEquals(6, grid.get(new Position(1, 1)).get().usedTerabytes);
		assertEquals(2, grid.get(new Position(1, 1)).get().availableTerabytes);

	}

	private StorageClusterGrid move(StorageClusterGrid grid, int fromX, int fromY, int toX, int toY) {
		Node source = grid.get(new Position(fromX, fromY)).get();
		Node target = grid.get(new Position(toX, toY)).get();

		return grid.moving(source, target);
	}

}
