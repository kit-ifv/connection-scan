package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.PathToStop;
import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class PathsToStopsTest {

	@Test
	public void knowsDistanceToStops() throws Exception {
		PathToStop asExpected = shortDistance();
		ReachableStops stops = stops(asList(asExpected));

		PathToStop distance = stops.pathTo(nearStop());

		assertThat(distance, is(asExpected));
	}

	@Test
	public void findsStopWithEarliestArrival() throws Exception {
		Times times = mock(Times.class);
		ReachableStops stops = stops(asList(shortDistance(), longDistance()));
		when(times.get(nearStop())).thenReturn(oneMinuteLater());
		when(times.get(farStop())).thenReturn(someTime());

		Optional<Stop> stop = stops.earliestArrivalAtStop(times);

		assertThat(stop, isPresent());
		assertThat(stop, hasValue(nearStop()));
		verify(times).get(nearStop());
		verify(times).get(farStop());
	}

	@Test
	public void doesNotFindStopWithEarliestArrivalWhenNoStopsAreAvailable() throws Exception {
		Times times = mock(Times.class);
		ReachableStops stops = stops(emptyList());

		Optional<Stop> stop = stops.earliestArrivalAtStop(times);

		assertThat(stop, isEmpty());
		verifyZeroInteractions(times);
	}

	@Test
	public void considersPathToStopToFindCorrectStartStop() throws Exception {
		ReachableStops stops = stops(asList(shortDistance(), longDistance()));
		Connection connection = mock(Connection.class);
		when(connection.departure()).thenReturn(oneMinuteLater());
		Time time = someTime();

		assertTrue(stops.isStart(nearStop(), time, connection));
		assertFalse(stops.isStart(farStop(), time, connection));
	}

	private static ReachableStops stops(List<PathToStop> stops) {
		return PathsToStops.from(stops);
	}

	private PathToStop shortDistance() {
		return new PathToStop(nearStop(), shortDuration());
	}

	private PathToStop longDistance() {
		return new PathToStop(farStop(), longDuration());
	}

	private RelativeTime shortDuration() {
		return RelativeTime.of(1, MINUTES);
	}

	private Stop nearStop() {
		return someStop();
	}

	private RelativeTime longDuration() {
		return RelativeTime.of(2, MINUTES);
	}

	private Stop farStop() {
		return anotherStop();
	}
}
