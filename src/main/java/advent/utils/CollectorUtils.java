package advent.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class CollectorUtils {

	public static <T> Collector<T, ?, LinkedList<T>> toLinkedList() {
		return Collector.of(LinkedList::new, //
				LinkedList::add, //
				(l, r) -> {
					l.addAll(r);
					return l;
				});
	}

	/**
	 * Helper collector for making mutable lists, so that we can (e.g.) invoke
	 * {@link Collections#rotate(List, int)} on them.
	 */
	public static <T> Collector<T, ?, ArrayList<T>> toArrayList() {
		return Collector.of(ArrayList::new, //
				ArrayList::add, //
				(l, r) -> {
					l.addAll(r);
					return l;
				});
	}

	/**
	 * Collector turning a stream into a Guava multimap, with each element of
	 * the stream becoming one element of the multimap.
	 */
	public static <T, K, V> Collector<T, ?, Multimap<K, V>> toMultimap(Function<T, K> keyMapper,
			Function<T, V> valueMapper) {
		return flatMappingToMultimap(item -> Stream.of(keyMapper.apply(item)), valueMapper);

	}

	/**
	 * Collector turning a stream into a Guava multimap, with each stream
	 * element becoming any number of elements of the multimap, as defined by
	 * the output of the keyMapper.
	 */
	public static <T, K, V> Collector<T, ?, Multimap<K, V>> flatMappingToMultimap(Function<T, Stream<K>> keyMapper,
			Function<T, V> valueMapper) {
		return Collector.of(HashMultimap::create, //
				(map, item) -> keyMapper.apply(item).forEach(key -> map.put(key, valueMapper.apply(item))), //
				(l, r) -> {
					l.putAll(r);
					return l;
				});

	}

	public static Collector<Character, ?, String> charsToString() {
		return Collector.of(StringBuilder::new, //
				(sb, c) -> sb.appendCodePoint(c), //
				StringBuilder::append, //
				StringBuilder::toString);
	}

}
