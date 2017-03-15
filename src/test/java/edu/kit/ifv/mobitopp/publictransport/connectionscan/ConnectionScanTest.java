package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class ConnectionScanTest {

	private ConnectionSweeper connections;
	private PreparedSearchRequest searchRequest;
	private TransitNetwork timetable;
	private ConnectionScan scan;

	@Before
	public void initialise() throws Exception {
		timetable = mock(TransitNetwork.class);
		connections = mock(ConnectionSweeper.class);
		searchRequest = mock(PreparedSearchRequest.class);
		
		when(timetable.connections()).thenReturn(connections);
		scan = scan(timetable);
	}

	private ConnectionScan scan(TransitNetwork timetable) {
		return new ConnectionScan(timetable) {

			@Override
			PreparedSearchRequest newSearchRequest(StopPaths fromStarts, StopPaths toEnds, Time atTime) {
				return searchRequest;
			}
		};
	}

	@Test
	public void missingStop() throws Exception {
		Stop start = someStop();
		Stop end = otherStop();
		Stop unreachableEnd = anotherStop();
		List<Stop> stops = asList(start, end);
		use(stops);

		Time searchTime = someTime();
		when(connections.areDepartedBefore(searchTime)).thenReturn(false);
		Optional<PublicTransportRoute> route = scan.findRoute(start, unreachableEnd, searchTime);

		assertThat(route, isEmpty());
		verify(connections).areDepartedBefore(searchTime);
		verifyNoMoreInteractions(connections);
	}

	private void use(Collection<Stop> stops) {
		when(timetable.stops()).thenReturn(stops);
	}

	@Test
	public void fromOneStopToAnotherStop() throws Exception {
		Stop start = someStop();
		Stop end = otherStop();
		List<Stop> stops = asList(start, end);
		use(stops);

		PublicTransportRoute route = mock(PublicTransportRoute.class);
		Time searchTime = someTime();
		when(connections.areDepartedBefore(searchTime)).thenReturn(false);
		when(connections.sweep(any())).thenReturn(of(route));

		Optional<PublicTransportRoute> startToStop = scan.findRoute(start, end, searchTime);

		assertThat(startToStop, isPresent());
		assertThat(startToStop, hasValue(equalTo(route)));
		verify(connections).areDepartedBefore(searchTime);
		verify(connections).sweep(any());
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
		use(stops);
		Time searchTime = someTime();
		when(connections.areDepartedBefore(searchTime)).thenReturn(false);
		
		Optional<PublicTransportRoute> route = scan.findRoute(anotherStart, end, searchTime);

		assertThat(route, isEmpty());
		verify(connections).areDepartedBefore(searchTime);
		verifyNoMoreInteractions(connections);
	}

	@Test
	public void whenTimeIsAfterLatestDeparture() throws Exception {
		Stop start = someStop();
		Stop end = otherStop();
		Collection<Stop> stops = asList(start, end);
		use(stops);
		Time searchTime = someTime();
		when(connections.areDepartedBefore(searchTime)).thenReturn(true);

		Optional<PublicTransportRoute> route = scan.findRoute(start, end, searchTime);

		assertThat(route, isEmpty());
		verify(connections).areDepartedBefore(searchTime);
		verifyNoMoreInteractions(connections);
	}

	@Test
	public void findsRouteBetweenSeveralStops() throws Exception {
		Time time = someTime();
		List<Stop> startStops = asList(someStop());
		List<Stop> endStops = asList(anotherStop());
		List<Stop> stops = asList(someStop(), anotherStop());
		use(stops);
		StopPaths starts = mock(StopPaths.class);
		StopPaths ends = mock(StopPaths.class);
		PublicTransportRoute vehicleRoute = mock(PublicTransportRoute.class);
		when(starts.stops()).thenReturn(startStops);
		when(ends.stops()).thenReturn(endStops);
		when(connections.sweep(searchRequest)).thenReturn(of(vehicleRoute));

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(starts, ends, time);

		assertThat(startToEnd, isPresent());
		assertThat(startToEnd, hasValue(vehicleRoute));
		verify(connections).sweep(searchRequest);
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenStartTimeIsTooLate() throws Exception {
		Time time = someTime();
		List<Stop> stops = asList(someStop(), anotherStop());
		use(stops);
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(connections.areDepartedBefore(time)).thenReturn(true);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenStartStopsAreNotAvailable() throws Exception {
		Time time = someTime();
		List<Stop> startStops = asList(someStop());
		List<Stop> endStops = asList(anotherStop());
		List<Stop> stops = endStops;
		use(stops);
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenEndStopsAreNotAvailable() throws Exception {
		Time time = someTime();
		List<Stop> startStops = asList(someStop());
		List<Stop> endStops = asList(anotherStop());
		List<Stop> stops = startStops;
		use(stops);
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenNoEndStopsAreGiven() throws Exception {
		Time time = someTime();
		List<Stop> startStops = asList(someStop());
		List<Stop> endStops = emptyList();
		List<Stop> stops = asList(someStop());
		use(stops);
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

	@Test
	public void findsNoRouteBetweenSeveralStopsWhenNoStartStopsAreGiven() throws Exception {
		Time time = someTime();
		List<Stop> startStops = emptyList();
		List<Stop> stops = asList(someStop(), anotherStop());
		use(stops);
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(startStops);

		Optional<PublicTransportRoute> startToEnd = scan.findRoute(reachableStart, reachableEnd, time);

		assertThat(startToEnd, isEmpty());
	}

}
