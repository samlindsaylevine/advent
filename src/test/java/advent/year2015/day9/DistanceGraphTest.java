package advent.year2015.day9;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DistanceGraphTest {

	@Test
	public void reference() {
		DistanceGraph graph = new DistanceGraph();
		graph.addEdge("London to Dublin = 464");
		graph.addEdge("London to Belfast = 518");
		graph.addEdge("Dublin to Belfast = 141");
		assertEquals(605, graph.shortestRouteLength());
	}

}
