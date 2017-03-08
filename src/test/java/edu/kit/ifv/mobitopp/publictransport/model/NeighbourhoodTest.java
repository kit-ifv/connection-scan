package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;

public class NeighbourhoodTest {

	@Test
	public void returnsEmptyWalkTimeWhenNeighbourhoodIsEmpty() throws Exception {
		Neighbourhood neighbourhood = new Neighbourhood();

		Stop stop = someStop();
		Optional<RelativeTime> walkTime = neighbourhood.walkTimeTo(stop);

		assertThat(walkTime, isEmpty());
	}

	@Test
	public void returnsDurationForStopWhichIsInNeighbourhood() throws Exception {
		Stop neighbour = someStop();
		Neighbourhood neighbourhood = new Neighbourhood();
		neighbourhood.add(neighbour, RelativeTime.of(1, MINUTES));

		Optional<RelativeTime> walkTime = neighbourhood.walkTimeTo(neighbour);

		assertThat(walkTime, isPresent());
		assertThat(walkTime, hasValue(equalTo(RelativeTime.of(1, MINUTES))));
	}

	@Test
	public void returnsEmptyWalkTimeWhenStopIsNotANeighbour() throws Exception {
		Stop neighbour = someStop();
		Neighbourhood neighbourhood = new Neighbourhood();
		neighbourhood.add(neighbour, RelativeTime.of(1, MINUTES));

		Stop stop = anotherStop();
		Optional<RelativeTime> walkTime = neighbourhood.walkTimeTo(stop);

		assertThat(walkTime, isEmpty());
	}
}
