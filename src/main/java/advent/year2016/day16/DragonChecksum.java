package advent.year2016.day16;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DragonChecksum {

	private final List<Boolean> data;
	
	public static DragonChecksum create(String initialState, int length) {
		DragonChecksum checksum = new DragonChecksum(initialState);
		
		DragonChecksum longEnough = Stream.iterate(checksum, DragonChecksum::next) //
				.filter(sum -> sum.data.size() >= length) //
				.findFirst() //
				.get();
		
		return new DragonChecksum(longEnough.data.subList(0, length));
	}
	
	DragonChecksum(String initialState) {
		this(initialState.chars() //
			.mapToObj(DragonChecksum::parseDigit) //
			.collect(toList()));
	}
	
	private DragonChecksum(List<Boolean> data) {
		this.data = data;
	}
	
	private static boolean parseDigit(int i) {
		return parseDigit((char) i);
	}
	
	private static boolean parseDigit(char digit) {
		if (digit == '1') {
			return true;
		} else if (digit == '0') {
			return false;
		} else {
			throw new IllegalArgumentException("Bad digit " + digit);
		}
	}
	
	public String toString() {
		return dataToString(this.data);
	}
	
	private static String dataToString(List<Boolean> input) {
		return input.stream() //
				.map(digit -> digit ? "1" : "0") //
				.collect(joining(""));
	}
	
	DragonChecksum next() {
		Stream<Boolean> a = data.stream();
		Stream<Boolean> zero = Stream.of(false);
		Stream<Boolean> b = streamReversed(data).map(x -> !x);
		
		return new DragonChecksum(concat(a,  zero, b).collect(toList()));
	}
	
	public String checksum() {
		return dataToString(checksumList(this.data));
	}
	
	private static List<Boolean> checksumList(List<Boolean> input) {
		if (input.size() % 2 == 1) {
			return input;
		}
		
		List<Boolean> pairwise = IntStream.range(0, input.size()) //
				.filter(i -> i % 2 == 0) //
				.mapToObj(i -> input.get(i).equals(input.get(i + 1))) //
				.collect(toList());
		
		return checksumList(pairwise);
	}
	
	/**
	 * Streams a list in reverse order. Not going to be pleasant for a list that is not random-access.
	 */
	static <T> Stream<T> streamReversed(List<T> list) {
		return IntStream.range(0, list.size()) //
				.mapToObj(i -> list.get(list.size() - i - 1));
	}
	
	@SafeVarargs
	static <T> Stream<T> concat(Stream<T>... streams) {
		return Arrays.stream(streams) //
				.reduce(Stream.empty(), Stream::concat);
	}
	
	public static void main(String[] args) {
		String input = "11110010111001001";
		System.out.println(DragonChecksum.create(input, 272).checksum());
		System.out.println(DragonChecksum.create(input, 35651584).checksum());
	}
}
