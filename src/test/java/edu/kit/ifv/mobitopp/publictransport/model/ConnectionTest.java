package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.matcher.ConnectionMatchers.departsBefore;
import static edu.kit.ifv.mobitopp.publictransport.matcher.ConnectionMatchers.valid;
import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ConnectionTest {

	private static final Time someTime = new Time(LocalDateTime.of(2011, 10, 17, 0, 0));

	@Test
	public void footConnectionHasFixedId() throws Exception {
		Connection connection = Connection.byFootFrom(someStop(), anotherStop(), someTime(),
				someTime());

		assertThat(connection.id(), is(-1));
	}

	@Test
	public void isValidWhenStartAndEndAreDifferentAndDepartsBeforeOrAtTheSameTimeAsItArrives()
			throws Exception {
		Connection validConnection = connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAndArrivesAt(sameTime())
				.build();

		assertThat(validConnection, is(valid()));
	}

	@Test
	public void isInvalidWhenStartAndEndAreTheSame() throws Exception {
		Stop sameStop = someStop();

		Connection connection = connection().startsAt(someStop()).endsAt(sameStop).build();

		assertThat(connection, is(not(valid())));
	}

	@Test
	public void isInvalidWhenStartAndEndAreDifferentButDepartsAfterItArrives() throws Exception {
		Time earlierTime = someTime();
		Time laterTime = oneMinuteLater();
		Connection connection = connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.arrivesAt(earlierTime)
				.departsAt(laterTime)
				.build();

		assertThat(connection, is(not(valid())));
	}

	@Test
	public void departsBeforeTimeLaterThenDeparture() throws Exception {
		Time earlierTime = someTime();
		Connection connection = connection().departsAt(earlierTime).build();

		Time laterTime = oneMinuteLater();
		assertThat(connection, departsBefore(laterTime));
	}

	@Test
	public void departsAfterTimeEarlierThenDeparture() throws Exception {
		Time laterTime = oneMinuteLater();
		Connection connection = connection().departsAt(laterTime).build();

		Time earlierTime = someTime();
		assertThat(connection, not(departsBefore(earlierTime)));
	}

	@Test
	public void ensuresStartAndEndStopAreInRoute() throws Exception {
		Point2D start = new Point2D.Double(1, 0);
		Point2D end = new Point2D.Double(2, 0);

		Connection connection = connection().startsAt(start).endsAt(end).build();

		assertThat(connection.points().toList(), contains(start, end));
	}
	
	@Test
	public void calculatesDuration() {
		Connection connection = connection().departsAt(someTime()).arrivesAt(oneMinuteLater()).build();
		
		RelativeTime duration = connection.duration();
		
		assertThat(duration, is(equalTo(RelativeTime.of(1, MINUTES))));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Stop oneStop = someStop();
		Stop anotherStop = anotherStop();
		Journey oneJourney = journey().withId(0).build();
		Journey anotherJourney = journey().withId(1).build();
		Connection oneConnection = connection().startsAt(oneStop).build();
		Connection anotherConnection = connection().startsAt(anotherStop).build();
		EqualsVerifier
				.forClass(Connection.class)
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Stop.class, oneStop, anotherStop)
				.withPrefabValues(Journey.class, oneJourney, anotherJourney)
				.withPrefabValues(Connection.class, oneConnection, anotherConnection)
				.withIgnoredFields("points", "id", "next", "positionInJourney")
				.usingGetClass()
				.verify();
	}

	private static Time someTime() {
		return time(0, 0);
	}

	private static Time sameTime() {
		return time(0, 0);
	}

	private Time oneMinuteLater() {
		return time(0, 1);
	}

	private static Time time(int hour, int minute) {
		return someTime.add(hour, HOURS).add(minute, MINUTES);
	}

}
