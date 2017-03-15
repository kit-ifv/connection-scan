package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteEarlier;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
	private TransitNetwork verifyStopsNetwork;
	
	@Before
	public void initialise() {
		searchTime = someTime();
		start = someStop();
		end = anotherStop();
		List<Stop> stops = asList(start, end);
		verifyStopsNetwork = testOnly(stops);
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
	public void missingEndStop() throws Exception {
		Stop unreachableEnd = otherStop();

		boolean scanNotNeeded = verifyStopsNetwork.scanNotNeeded(start, unreachableEnd, searchTime);

		assertTrue(scanNotNeeded);
	}

	private TransitNetwork testOnly(List<Stop> stops) {
		Connections connections = new Connections();
		connections.add(usableConnection(start, end));
		return TransitNetwork.createOf(stops, connections);
	}

	private Connection usableConnection(Stop start, Stop end) {
		return connection().startsAt(start).endsAt(end).departsAt(searchTime).build();
	}

	@Test
	public void correctSearchRequest() throws Exception {
		boolean scanNotNeeded = verifyStopsNetwork.scanNotNeeded(start, end, searchTime);

		assertFalse(scanNotNeeded);
	}

	@Test
	public void missingStartStop() throws Exception {
		Stop anotherStart = otherStop();

		boolean scanNotNeeded = verifyStopsNetwork.scanNotNeeded(anotherStart, end, searchTime);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void whenTimeIsAfterLatestDeparture() throws Exception {
		Collection<Stop> stops = asList(start, end);
		Connections connections = new Connections();
		connections.add(tooEarlyConnection(start, end));
		TransitNetwork transitNetwork = TransitNetwork.createOf(stops, connections);

		boolean scanNotNeeded = transitNetwork.scanNotNeeded(start, end, searchTime);

		assertTrue(scanNotNeeded);
	}

	private Connection tooEarlyConnection(Stop start, Stop end) {
		return connection().startsAt(start).endsAt(end).departsAt(oneMinuteEarlier()).build();
	}

}
