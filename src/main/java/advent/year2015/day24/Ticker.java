package advent.year2015.day24;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Silly little utility used for checking progress during a long-running task.
 *
 * Prints out a status update every so often, at a defined period.
 */
public class Ticker {

	private final int period;

	private final AtomicLong counter = new AtomicLong(0);

	public Ticker(int period) {
		this.period = period;
	}

	public void tick() {
		long value = this.counter.incrementAndGet();

		if (value % this.period == 0) {
			System.out.println(value);
		}
	}

}
