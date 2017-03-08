package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class ConnectionScanTest {

	private ConnectionSweeper connections;

	@Before
	public void initialise() throws Exception {
		connections = mock(ConnectionSweeper.class);
	}

	@Test
	public void missingStop() throws Exception {
		Stop start = someStop();
		Stop end = otherStop();
		Stop unreachableEnd = anotherStop();
		List<Stop> stops = asList(start, end);
		ConnectionScan scan = scan(stops, connections);

		Time searchTime = someTime();
		when(connections.isTooLate(searchTime)).thenReturn(false);
		Optional<PublicTransportRoute> route = scan.findRoute(start, unreachableEnd, searchTime);

		assertThat(route, isEmpty());
		verify(connections).isTooLate(searchTime);
		verifyNoMoreInteractions(connections);
	}

	@Test
	public void fromOneStopToAnotherStop() throws Exception {
		Stop start = someStop();
		Stop end = otherStop();
		List<Stop> stops = asList(start, end);

		PublicTransportRoute route = mock(PublicTransportRoute.class);
		Time searchTime = someTime();
		when(connections.isTooLate(searchTime)).thenReturn(false);
		when(connections.sweep(any(), eq(start), eq(end), eq(searchTime))).thenReturn(of(route));

		ConnectionScan connectionScan = scan(stops, connections);

		Optional<PublicTransportRoute> startToStop = connectionScan.findRoute(start, end, searchTime);

		assertThat(startToStop, isPresent());
		assertThat(startToStop, hasValue(equalTo(route)));
		verify(connections).isTooLate(searchTime);
		verify(connections).sweep(any(), eq(start), eq(end), eq(searchTime));
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> asList(T... elements) {
		return new ArrayList<>(Arrays.asList(elements));
	}

	@Test
	public void missingStopWhenPassengerIsNotOnATrip() throws Exception {
		Stop start = someStop();
		Stop anotherStart = anotherStop();
		Stop end = otherStop();
		List<Stop> stops = asList(start, end);
		ConnectionScan scan = scan(stops, connections);

		Time searchTime = someTime();
		when(connections.isTooLate(searchTime)).thenReturn(false);
		Optional<PublicTransportRoute> route = scan.findRoute(anotherStart, end, searchTime);

		assertThat(route, isEmpty());
		verify(connections).isTooLate(searchTime);
		verifyNoMoreInteractions(connections);
	}

	@Test
	public void whenTimeIsAfterLatestDeparture() throws Exception {
		Stop start = someStop();
		Stop end = otherStop();
		Collection<Stop> stops = asList(start, end);

		Time searchTime = someTime();
		when(connections.isTooLate(searchTime)).thenReturn(true);
		ConnectionScan scan = scan(stops, connections);
		Optional<PublicTransportRoute> route = scan.findRoute(start, end, searchTime);

		assertThat(route, isEmpty());
		verify(connections).isTooLate(searchTime);
		verifyNoMoreInteractions(connections);
	}

	@Test
	public void findsRouteBetweenSeveralStops() throws Exception {
		Time time = someTime();
		List<Stop> startStops = asList(someStop());
		List<Stop> endStops = asList(anotherStop());
		List<Stop> stops = asList(someStop(), anotherStop());
		int numberOfStops = stops.size();
		ReachableStops reachableStart = mock(ReachableStops.class);
		ReachableStops reachableEnd = mock(ReachableStops.class);
		PublicTransportRoute vehicleRoute = mock(PublicTransportRoute.class);
		PublicTransportRoute tourIncludingFootpath = mock(PublicTransportRoute.class);
		Arrival arrival = mock(Arrival.class);
		when(reachableStart.createArrival(time, numberOfStops)).thenReturn(arrival);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);
		when(vehicleRoute.addFootpaths(reachableStart, reachableEnd)).thenReturn(tourIncludingFootpath);
		when(connections.sweep(arrival, reachableStart, reachableEnd, time)).thenReturn(of(vehicleRoute));

		ConnectionScan scan = scan(stops, connections);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isPresent());
		assertThat(startToEnd, hasValue(tourIncludingFootpath));
		verify(reachableStart).createArrival(time, numberOfStops);
		verify(connections).sweep(arrival, reachableStart, reachableEnd, time);
		verify(vehicleRoute).addFootpaths(reachableStart, reachableEnd);
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenStartTimeIsTooLate() throws Exception {
		Time time = someTime();
		List<Stop> stops = asList(someStop(), anotherStop());
		ReachableStops reachableStart = mock(ReachableStops.class);
		ReachableStops reachableEnd = mock(ReachableStops.class);
		when(connections.isTooLate(time)).thenReturn(true);

		ConnectionScan scan = scan(stops, connections);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenStartStopsAreNotAvailable() throws Exception {
		Time time = someTime();
		List<Stop> startStops = asList(someStop());
		List<Stop> endStops = asList(anotherStop());
		List<Stop> stops = endStops;
		ReachableStops reachableStart = mock(ReachableStops.class);
		ReachableStops reachableEnd = mock(ReachableStops.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		ConnectionScan scan = scan(stops, connections);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenEndStopsAreNotAvailable() throws Exception {
		Time time = someTime();
		List<Stop> startStops = asList(someStop());
		List<Stop> endStops = asList(anotherStop());
		List<Stop> stops = startStops;
		ReachableStops reachableStart = mock(ReachableStops.class);
		ReachableStops reachableEnd = mock(ReachableStops.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		ConnectionScan scan = scan(stops, connections);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenNoEndStopsAreGiven() throws Exception {
		Time time = someTime();
		List<Stop> startStops = asList(someStop());
		List<Stop> endStops = emptyList();
		List<Stop> stops = asList(someStop());
		ReachableStops reachableStart = mock(ReachableStops.class);
		ReachableStops reachableEnd = mock(ReachableStops.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		ConnectionScan scan = scan(stops, connections);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenNoStartStopsAreGiven() throws Exception {
		Time time = someTime();
		List<Stop> startStops = emptyList();
		List<Stop> stops = asList(someStop(), anotherStop());
		ReachableStops reachableStart = mock(ReachableStops.class);
		ReachableStops reachableEnd = mock(ReachableStops.class);
		when(reachableStart.stops()).thenReturn(startStops);

		ConnectionScan scan = scan(stops, connections);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsOnWrongStopIds() {
		Stop first = stop().withId(0).build();
		Stop tooHighId = stop().withId(2).build();
		Collection<Stop> stops = asList(first, tooHighId);
		
		ConnectionScan.from(stops, noConnections());
	}

	private Connections noConnections() {
		return new Connections();
	}

	private static ConnectionScan scan(Collection<Stop> stops, ConnectionSweeper sweeper) {
		return new ConnectionScan(stops, sweeper);
	}

}
