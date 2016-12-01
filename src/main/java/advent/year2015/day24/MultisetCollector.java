package advent.year2015.day24;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

public class MultisetCollector<T> implements Collector<T, Multiset<T>, Multiset<T>> {

	private MultisetCollector() {

	}

	public static <T> MultisetCollector<T> toMultiset() {
		return new MultisetCollector<>();
	}

	public static <T> Collector<T, ?, Multiset<T>> toImmutableMultiset() {
		return Collectors.collectingAndThen(toMultiset(), ImmutableMultiset::copyOf);
	}

	@Override
	public Supplier<Multiset<T>> supplier() {
		return HashMultiset::create;
	}

	@Override
	public BiConsumer<Multiset<T>, T> accumulator() {
		return Multiset::add;
	}

	@Override
	public BinaryOperator<Multiset<T>> combiner() {
		return (set1, set2) -> {
			set1.addAll(set2);
			return set1;
		};
	}

	@Override
	public Function<Multiset<T>, Multiset<T>> finisher() {
		return Function.identity();
	}

	@Override
	public Set<Characteristics> characteristics() {
		return ImmutableSet.of(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
	}

}
