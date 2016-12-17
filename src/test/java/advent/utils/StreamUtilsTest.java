package advent.utils;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class StreamUtilsTest {

	@Test
	public void iterateUntil() {
		List<Integer> expected = ImmutableList.of(1, 2, 3);

		List<Integer> actual = StreamUtils.iterateUntil(1, i -> i + 1, i -> i == 4) //
				.collect(toList());

		assertEquals(expected, actual);
	}

	@Test
	public void iterateUntilSeedElementFailsTest() {
		List<String> expected = ImmutableList.of();

		List<String> actual = StreamUtils.iterateUntil("a", s -> s + "b", "a"::equals) //
				.collect(toList());

		assertEquals(expected, actual);
	}

}
