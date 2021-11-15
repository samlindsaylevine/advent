package advent.year2016.day16;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

public class DragonChecksumTest {

	@Test
	public void streamReversed() {
		List<String> input = ImmutableList.of("a", "b", "c", "d");
		List<String> reversed = DragonChecksum.streamReversed(input).collect(toList());
		List<String> expected = ImmutableList.of("d", "c", "b", "a");
		assertEquals(expected, reversed);
	}
	
	@Test
	public void streamReversedEmpty() {
		List<Integer> empty = ImmutableList.of();
		
		List<Integer> reversed = DragonChecksum.streamReversed(empty) //
				.collect(toList());
		
		assertEquals(empty, reversed);
	}
	
	@Test
	public void concat() {
		Stream<String> a = Stream.of("a", "b", "c");
		Stream<String> b = Stream.of("d", "e");
		Stream<String> c = Stream.empty();
		Stream<String> d = Stream.of("f");
		
		String result = DragonChecksum.concat(a, b, c, d).collect(joining(""));
		assertEquals("abcdef", result);
	}
	
	@Test
	public void concatEmpty() {
		long count = DragonChecksum.concat().count();
		assertEquals(0, count);
	}
	
	@Test
	public void nextReference() {
		assertEquals("100", new DragonChecksum("1").next().toString());
		assertEquals("001", new DragonChecksum("0").next().toString());
		assertEquals("11111000000", new DragonChecksum("11111").next().toString());
		assertEquals("1111000010100101011110000", new DragonChecksum("111100001010").next().toString());
	}
	
	@Test
	public void length() {
		assertEquals(100, DragonChecksum.create("1", 100).toString().length());
	}
	
	@Test
	public void checksumReference() {
		assertEquals("100", new DragonChecksum("110010110100").checksum());
	}
	
	@Test
	public void fullReference() {
		assertEquals("01100", DragonChecksum.create("10000", 20).checksum());
	}
	
}
