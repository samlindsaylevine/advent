package advent.year2016.day7;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;

/**
 * A silly little helper class for splitting a stream into its even and odd
 * elements.
 */
public class OddsAndEvens<T> {

	private List<T> evens = new LinkedList<>();
	private List<T> odds = new LinkedList<>();
	private boolean nextElementIsEven = true;

	private OddsAndEvens() {

	}

	private void add(T element) {
		if (this.nextElementIsEven) {
			this.evens.add(element);
		} else {
			this.odds.add(element);
		}

		this.nextElementIsEven = !this.nextElementIsEven;
	}

	public List<T> getEvens() {
		return ImmutableList.copyOf(this.evens);
	}

	public List<T> getOdds() {
		return ImmutableList.copyOf(this.odds);
	}

	/**
	 * Returns a collector that separates a stream into its even-indexed and
	 * odd-indexed elements (with a 0-indexed convention).
	 * 
	 * This does *NOT* work for parallel streams - the merge function is
	 * undefined.
	 */
	public static <T> Collector<T, ?, OddsAndEvens<T>> toOddsAndEvens() {
		return Collector.of(OddsAndEvens::new, //
				OddsAndEvens::add, //
				(l, r) -> {
					throw new UnsupportedOperationException("Does not work for parallel streams");
				});
	}
}
