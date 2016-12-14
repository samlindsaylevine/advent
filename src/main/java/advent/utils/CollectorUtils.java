package advent.utils;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class CollectorUtils {

	public static <T> Collector<T, ?, LinkedList<T>> toLinkedList() {
		return Collector.of(LinkedList::new, LinkedList::add, (l, r) -> {
			l.addAll(r);
			return l;
		});
	}

	public static <T, K, V> Collector<T, ?, Multimap<K, V>> toMultimap(Function<T, K> keyMapper,
			Function<T, V> valueMapper) {
		return flatMappingToMultimap(item -> Stream.of(keyMapper.apply(item)), valueMapper);

	}

	public static <T, K, V> Collector<T, ?, Multimap<K, V>> flatMappingToMultimap(Function<T, Stream<K>> keyMapper,
			Function<T, V> valueMapper) {
		return Collector.of(HashMultimap::create, //
				(map, item) -> keyMapper.apply(item).forEach(key -> map.put(key, valueMapper.apply(item))), //
				(l, r) -> {
					l.putAll(r);
					return l;
				});

	}

}
