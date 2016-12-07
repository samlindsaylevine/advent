package advent.year2016.day7;

import static advent.year2016.day7.OddsAndEvens.toOddsAndEvens;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class OddsAndEvensTest {

	@Test
	public void empty() {
		OddsAndEvens<String> result = Stream.<String> empty().collect(toOddsAndEvens());
		assertEquals(Collections.emptyList(), result.getEvens());
		assertEquals(Collections.emptyList(), result.getOdds());
	}

	@Test
	public void simple() {
		OddsAndEvens<Integer> result = IntStream.range(0, 10) //
				.boxed() //
				.collect(toOddsAndEvens());

		assertEquals(ImmutableList.of(0, 2, 4, 6, 8), result.getEvens());
		assertEquals(ImmutableList.of(1, 3, 5, 7, 9), result.getOdds());
	}

	@Test
	public void parallel() {
		try {
			IntStream.range(0, 1000) //
					.boxed() //
					.parallel() //
					.collect(toOddsAndEvens());
			fail("Should fail on merge for parallel streams");
		} catch (UnsupportedOperationException e) {
			// Expected behavior.
		}
	}

}
