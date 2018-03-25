package timetabling.ettp;

import java.util.concurrent.TimeUnit;

import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.stopcriteria.StopCriterion;

public class MaxElapsedTime implements StopCriterion {

	// maximum runtime in milliseconds
	private final long startTime;
	private final long maxElapsedTime;

	public MaxElapsedTime(long startTime, long maxElapsedTime, TimeUnit timeUnit) {
		this.startTime = startTime;
		this.maxElapsedTime = timeUnit.toMillis(maxElapsedTime);

		// check elapsed
		if (this.maxElapsedTime <= 0) {
			throw new IllegalArgumentException(
					"Error while creating stop criterion: maximum maxElapsedTime should be at least 1 millisecond.");
		}
	}

	@Override
	public boolean searchShouldStop(Search<?> search) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		return elapsedTime >= maxElapsedTime;
	}

	@Override
	public String toString() {
		return "{max runtime: " + maxElapsedTime + " ms}";
	}

}
