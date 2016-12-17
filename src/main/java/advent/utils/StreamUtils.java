package advent.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {

	/**
	 * Much like {@link Stream#iterate(Object, UnaryOperator)}, except with an
	 * ending condition after which the stream terminates.
	 * 
	 * The first element for which stopCondition evaluates to true will not be
	 * included in the stream nor will anything after it.
	 * 
	 * This is closely based on the implementation of
	 * {@link Stream#iterate(Object, UnaryOperator)}; if this were in Java 9, it
	 * would not be necessary because we would have "takeWhile" available on
	 * Stream, but that is not present yet in Java 8.
	 */
	public static <T> Stream<T> iterateUntil(T seed, UnaryOperator<T> f, Predicate<T> stopCondition) {
		Objects.requireNonNull(f);

		final Iterator<T> iterator = new Iterator<T>() {
			private T nextElement = seed;

			@Override
			public boolean hasNext() {
				return !stopCondition.test(nextElement);
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				T output = nextElement;
				nextElement = f.apply(nextElement);
				return output;
			}
		};

		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
	}

}
