package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class StopTest {

	private static final RelativeTime noChangeTime = RelativeTime.of(0, MINUTES);
	private static final RelativeTime smallChangeTime = RelativeTime.of(1, MINUTES);
	private static final int internalId = 0;
	private static final RelativeTime someDuration = RelativeTime.ZERO;
	
	private Station station;

	@Before
	public void initialise() throws Exception {
		station = mock(Station.class);
	}

	@Test
	public void addsChangeTimeToGivenTimeWhenChangeTimeIsGreaterZero() throws Exception {
		Point2D someCoordinate = someCoordinate();
		int stopId = 0;

		Stop stop = new Stop(internalId, "name", someCoordinate, smallChangeTime, station, stopId);
		Time currentTime = someTime();
		Time timeIncludingChangeTime = stop.addChangeTimeTo(currentTime);

		assertThat(timeIncludingChangeTime, is(equalTo(oneMinuteLater())));
	}

	@Test
	public void addsNothingToGivenTimeWhenChangeTimeIsZero() throws Exception {
		Point2D someCoordinate = someCoordinate();
		int stopId = 0;

		Stop stop = new Stop(internalId, "name", someCoordinate, noChangeTime, station, stopId);
		Time currentTime = someTime();
		Time timeIncludingChangeTime = stop.addChangeTimeTo(currentTime);

		assertThat(timeIncludingChangeTime, is(equalTo(currentTime)));
	}

	private static Double someCoordinate() {
		return new Point2D.Double(0, 0);
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Stop someStop = new Stop(internalId, "", new Point2D.Double(), noChangeTime, station, 0);
		EqualsVerifier
				.forClass(Stop.class)
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Map.class, Collections.emptyMap(),
						Collections.singletonMap(someStop, someDuration))
				.withPrefabValues(Set.class, Collections.emptySet(), Collections.singleton(someStop))
				.withOnlyTheseFields("id")
				.usingGetClass()
				.verify();
	}

}
