package advent.year2015.day17;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class EggnogContainersTest {

	@Test
	public void reference() {
		List<Integer> containers = ImmutableList.of(20, 15, 10, 5, 5);

		assertEquals(4, EggnogContainers.countStorageSolutions(25, containers));
		assertEquals(3, EggnogContainers.countStorageSolutions(25, containers, 2));
	}
}
