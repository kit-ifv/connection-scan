package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class MultipleSweeperDataTest {

	private static final RelativeTime oneMinute = RelativeTime.of(1, MINUTES);
	private static final RelativeTime twoMinutes = RelativeTime.of(2, MINUTES);
	
	private Times times;
	private UsedConnections usedConnections;
	private UsedJourneys usedJourneys;
	
	@Before
	public void initialise() {
		times = mock(Times.class);
		usedConnections = mock(UsedConnections.class);
		usedJourneys = mock(UsedJourneys.class);
	}

	@Test
	public void isAfterArrivalAtOneOfSeveralEndStops() throws Exception {
		StopPaths starts = mock(StopPaths.class);
		StopPaths ends = mock(StopPaths.class);
		when(ends.stops()).thenReturn(asList(nearStop(), farStop()));
		SweeperData data = dataFromPaths(starts, ends);
		Time beforeArrival = someTime();
		Time atArrival = Data.oneMinuteLater();
		Time afterArrival = Data.twoMinutesLater();
		Connection beforeArrivalConnection = departing(beforeArrival);
		Connection arrivalConnection = departing(atArrival);
		Connection afterArrivalConnection = departing(afterArrival);

		arrivingAt(nearStop(), atArrival);
		arrivingAt(farStop(), afterArrival);

		assertFalse(data.isAfterArrivalAtEnd(beforeArrivalConnection));
		assertFalse(data.isAfterArrivalAtEnd(arrivalConnection));
		assertTrue(data.isAfterArrivalAtEnd(afterArrivalConnection));
	}

	private void arrivingAt(Stop stop, Time atArrival) {
		when(times.getConsideringMinimumChangeTime(stop)).thenReturn(atArrival);
	}

	private Connection departing(Time beforeArrival) {
		return connection().departsAt(beforeArrival).build();
	}

	private StopPath shortDistance() {
		return new StopPath(nearStop(), shortDuration());
	}

	private StopPath longDistance() {
		return new StopPath(farStop(), longDuration());
	}

	private RelativeTime shortDuration() {
		return oneMinute;
	}

	private Stop nearStop() {
		return Data.someStop();
	}

	private RelativeTime longDuration() {
		return twoMinutes;
	}

	private Stop farStop() {
		return Data.anotherStop();
	}
	
	private SweeperData dataFromPaths(StopPaths starts, StopPaths ends) {
		return MultipleSweeperData.from(starts, ends, times, usedConnections, usedJourneys);
	}
}
