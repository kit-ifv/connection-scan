package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteEarlier;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class TransitNetworkTest {

	private Time searchTime;
	private Stop start;
	private Stop end;
	private Stop unreachableStop;
	private TransitNetwork correctNetwork;
	private Time tooLate;
	
	@Before
	public void initialise() {
		searchTime = someTime();
		tooLate = oneMinuteLater();
		start = someStop();
		end = anotherStop();
		unreachableStop = otherStop();
		List<Stop> stops = asList(start, end);
		correctNetwork = correctNetwork(stops);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnWrongStopIds() {
		Stop first = stop().withId(0).build();
		Stop tooHighId = stop().withId(2).build();
		Collection<Stop> stops = asList(first, tooHighId);

		TransitNetwork.createOf(stops, noConnections());
	}

	private Connections noConnections() {
		return new Connections();
	}

	@Test
	public void missingEndStop() {
		Stop unreachableEnd = otherStop();

		boolean scanNotNeeded = correctNetwork.scanNotNeeded(start, unreachableEnd, searchTime);

		assertTrue(scanNotNeeded);
	}

	private TransitNetwork correctNetwork(List<Stop> stops) {
		Connections connections = new Connections();
		connections.add(usableConnection(start, end));
		return TransitNetwork.createOf(stops, connections);
	}

	private Connection usableConnection(Stop start, Stop end) {
		return connection().startsAt(start).endsAt(end).departsAt(searchTime).build();
	}

	@Test
	public void correctSearchRequest() {
		boolean scanNotNeeded = correctNetwork.scanNotNeeded(start, end, searchTime);

		assertFalse(scanNotNeeded);
	}

	@Test
	public void missingStartStop() {
		Stop anotherStart = otherStop();

		boolean scanNotNeeded = correctNetwork.scanNotNeeded(anotherStart, end, searchTime);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void whenTimeIsAfterLatestDeparture() {
		Collection<Stop> stops = asList(start, end);
		TransitNetwork transitNetwork = tooLateSearchTime(stops);

		boolean scanNotNeeded = transitNetwork.scanNotNeeded(start, end, searchTime);

		assertTrue(scanNotNeeded);
	}

	private TransitNetwork tooLateSearchTime(Collection<Stop> stops) {
		Connections connections = new Connections();
		connections.add(tooEarlyConnection(start, end));
		TransitNetwork transitNetwork = TransitNetwork.createOf(stops, connections);
		return transitNetwork;
	}

	private Connection tooEarlyConnection(Stop start, Stop end) {
		return connection().startsAt(start).endsAt(end).departsAt(oneMinuteEarlier()).build();
	}
	
	@Test
	public void correctSearchRequestBetweenSeveralStops() {
		List<Stop> startStops = asList(start);
		List<Stop> endStops = asList(end);
		StopPaths starts = mock(StopPaths.class);
		StopPaths ends = mock(StopPaths.class);
		when(starts.stops()).thenReturn(startStops);
		when(ends.stops()).thenReturn(endStops);
		
		boolean scanNotNeeded = correctNetwork.scanNotNeeded(starts, ends, searchTime);

		assertFalse(scanNotNeeded);
	}

	@Test
	public void tooEarlySearchRequestBetweenSeveralStops() {
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(asList(start));
		when(reachableEnd.stops()).thenReturn(asList(end));

		boolean scanNotNeeded = correctNetwork.scanNotNeeded(reachableStart, reachableEnd, tooLate);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void missingStartStopsOnSearchRequestBetweenSeveralStops() {
		List<Stop> startStops = asList(unreachableStop);
		List<Stop> endStops = asList(end);
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		boolean scanNotNeeded = correctNetwork.scanNotNeeded(reachableStart, reachableEnd, searchTime);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void missingEndStopsOnSearchRequestBetweenSeveralStops() throws Exception {
		List<Stop> startStops = asList(start);
		List<Stop> endStops = asList(unreachableStop);
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		boolean scanNotNeeded = correctNetwork.scanNotNeeded(reachableStart, reachableEnd, searchTime);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void noEndStopsInSearchRequest() throws Exception {
		List<Stop> startStops = asList(start);
		List<Stop> endStops = emptyList();
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		boolean scanNotNeeded = correctNetwork.scanNotNeeded(reachableStart, reachableEnd, searchTime);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void noStartStopsInSearchRequest() throws Exception {
		List<Stop> startStops = emptyList();
		List<Stop> endStops = asList(end);
		StopPaths reachableStart = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(reachableStart.stops()).thenReturn(startStops);
		when(reachableEnd.stops()).thenReturn(endStops);

		boolean scanNotNeeded = correctNetwork.scanNotNeeded(reachableStart, reachableEnd, searchTime);

		assertTrue(scanNotNeeded);
	}

}
