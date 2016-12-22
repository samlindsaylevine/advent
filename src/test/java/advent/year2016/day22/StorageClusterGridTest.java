package advent.year2016.day22;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

import advent.year2016.day22.StorageClusterGrid.Node;

public class StorageClusterGridTest {

	@Test
	public void nodeRep() {
		String input = "/dev/grid/node-x37-y25   92T   73T    19T   79%";
		Optional<Node> result = Node.tryParse(input);
		Node node = result.orElseThrow(() -> new NoSuchElementException("Should have successfully parsed node"));

		assertEquals(37, node.position.x);
		assertEquals(25, node.position.y);
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

		assertEquals(5, grid.validPairs());
	}

}
