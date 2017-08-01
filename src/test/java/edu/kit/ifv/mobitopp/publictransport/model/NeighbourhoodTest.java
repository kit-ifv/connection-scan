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

import org.junit.Before;
import org.junit.Test;

public class NeighbourhoodTest {

	private Neighbourhood neighbourhood;
	private Stop neighbour;
	
	@Before
	public void initialise() {
		neighbourhood = new Neighbourhood();
		neighbour = someStop();
	}

	@Test
	public void returnsEmptyWalkTimeWhenNeighbourhoodIsEmpty() throws Exception {
		Optional<RelativeTime> walkTime = neighbourhood.walkTimeTo(neighbour);

		assertThat(walkTime, isEmpty());
	}

	@Test
	public void returnsDurationForStopWhichIsInNeighbourhood() throws Exception {
		neighbourhood.add(neighbour, RelativeTime.of(1, MINUTES));

		Optional<RelativeTime> walkTime = neighbourhood.walkTimeTo(neighbour);

		assertThat(walkTime, isPresent());
		assertThat(walkTime, hasValue(equalTo(RelativeTime.of(1, MINUTES))));
	}

	@Test
	public void returnsEmptyWalkTimeWhenStopIsNotANeighbour() throws Exception {
		neighbourhood.add(neighbour, RelativeTime.of(1, MINUTES));

		Stop noNeighbour = anotherStop();
		Optional<RelativeTime> walkTime = neighbourhood.walkTimeTo(noNeighbour);

		assertThat(walkTime, isEmpty());
	}
}
