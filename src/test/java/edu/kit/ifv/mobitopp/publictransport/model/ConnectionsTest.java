package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.yetAnotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ConnectionsTest {

	protected static final Point2D somePoint = new Point2D.Float();
	private static final Time someTime = new Time(LocalDateTime.of(1971, 1, 1, 0, 0));
	private static final RelativeTime anotherRelativeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime otherRelativeTime = RelativeTime.of(2, MINUTES);

	@Test
	public void calculateSingleConnection() throws Exception {
		Stop start = someStop();
		Stop end = anotherStop();
		Connection connection = connection()
				.startsAt(start)
				.endsAt(end)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();

		Connections connections = connections();
		connections.add(connection);
		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, contains(connection));
	}

	@Test
	public void creationWithSeveralStopPointsOnRoute() throws Exception {
		Stop stop1 = someStop();
		Stop stop2 = anotherStop();
		Stop stop3 = otherStop();

		Journey journey = mock(Journey.class);
		Connection stop1ToStop2 = connection()
				.withId(1)
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(journey)
				.with(routeInVisum())
				.build();
		Connection stop2ToStop3 = connection()
				.withId(2)
				.startsAt(stop2)
				.endsAt(stop3)
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(journey)
				.with(routeInVisum())
				.build();

		Connections connections = connections();

		connections.add(stop1ToStop2);
		connections.add(stop2ToStop3);
		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, hasItems(stop1ToStop2, stop2ToStop3));
	}

	private static List<Point2D> routeInVisum() {
		ArrayList<Point2D> points = new ArrayList<>();
		points.add(new Point2D.Float());
		points.add(new Point2D.Float());
		points.add(new Point2D.Float());
		points.add(new Point2D.Float());
		return points;
	}

	@Test
	public void creationWithSameStopPointTwiceOnRoute() throws Exception {
		Stop stop1 = stop().withId(1).withName("1").build();
		Stop stop2 = stop().withId(2).withName("2").build();
		Journey journey = mock(Journey.class);
		Connection stop1ToStop2 = connection()
				.withId(1)
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(journey)
				.with(routeInVisum())
				.build();
		Connection stop2ToStop1 = connection()
				.withId(2)
				.startsAt(stop2)
				.endsAt(stop1)
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(journey)
				.with(routeInVisum())
				.build();

		Connections connections = connections();
		connections.add(stop1ToStop2);
		connections.add(stop2ToStop1);
		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, hasItems(stop1ToStop2, stop2ToStop1));
	}

	@Test
	public void doesNotFilterConnectionsWhenNoConnectionIsAdded() throws Exception {
		Collection<Connection> listedConnections = connections().asCollection();

		assertThat(listedConnections, is(empty()));
	}

	@Test
	public void doesNotFilterConnectionWithDifferentStartAndEndAndWhichArrivesAfterItDeparts()
			throws Exception {
		Time someTime = someTime();
		Time notEarlier = oneMinuteLater();
		Connection validConnection = connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(someTime)
				.arrivesAt(notEarlier)
				.build();
		Connections connections = connections();
		connections.add(validConnection);

		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, contains(validConnection));
	}

	@Test
	public void filtersConnectionWithSameStartAndEnd() throws Exception {
		Connections connections = connections();

		Stop sameStop = someStop();
		Connection differentStartAndEnd = connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.build();
		Connection sameStartAndEnd = connection().startsAt(someStop()).endsAt(sameStop).build();
		connections.add(differentStartAndEnd);
		connections.add(sameStartAndEnd);

		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, contains(differentStartAndEnd));
	}

	@Test
	public void filtersConnectionWhichArrivesBeforeItDeparts() throws Exception {
		Connections connections = connections();

		Time earlierTime = someTime();
		Time laterTime = oneMinuteLater();
		ConnectionBuilder differentStartAndEnd = connection()
				.startsAt(someStop())
				.endsAt(anotherStop());
		Connection arrivesBeforeItDeparts = differentStartAndEnd
				.but()
				.departsAt(laterTime)
				.arrivesAt(earlierTime)
				.build();
		Connection sameDepartureAndArrival = differentStartAndEnd
				.but()
				.departsAndArrivesAt(someTime())
				.build();
		connections.add(arrivesBeforeItDeparts);
		connections.add(sameDepartureAndArrival);

		Collection<Connection> listedConnections = connections.asCollection();

		assertThat(listedConnections, contains(sameDepartureAndArrival));
	}

	@Test
	public void appliesAllConnections() throws Exception {
		ConnectionConsumer consumer = mock(ConnectionConsumer.class);
		InOrder inOrder = inOrder(consumer);
		
		Connections connections = connections();
		Connection connection = connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
		connections.add(connection);
		Connection connection2 = connection()
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater()).build();
		connections.add(connection2);

		connections.apply(consumer);

		inOrder.verify(consumer).accept(connection);
		inOrder.verify(consumer).accept(connection2);
	}
	
	@Test
	public void addAllConnections() throws Exception {
		int id = 0;
		Connection connection = connection().withId(id ).build();
		Connections newOnes = new Connections();
		newOnes.add(connection);
		Connections all = new Connections();
		
		all.addAll(newOnes);
		
		assertThat(all.get(id), is(equalTo(connection)));
	}
	
	@Test
	public void nextAfterIsAvailable() {
		Connections connections = connections();
		Connection first = connection().startsAt(someStop()).endsAt(anotherStop()).build();
		Connection second = connection().startsAt(anotherStop()).endsAt(otherStop()).build();
		Connection third = connection().startsAt(otherStop()).endsAt(yetAnotherStop()).build();
		
		connections.add(first);
		connections.add(second);
		connections.add(third);
		Connection nextAfterFirst = connections.nextAfter(first);
		Connection nextAfterSecond = connections.nextAfter(second);
		Connection nextAfterThird = connections.nextAfter(third);
		
		assertThat(nextAfterFirst, is(second));
		assertThat(nextAfterSecond, is(third));
		assertThat(nextAfterThird, is(nullValue()));
	}
	
	@Test
	public void positionOfConnection() {
		Connections connections = connections();
		Connection first = connection().startsAt(someStop()).endsAt(anotherStop()).build();
		Connection second = connection().startsAt(anotherStop()).endsAt(otherStop()).build();
		Connection third = connection().startsAt(otherStop()).endsAt(yetAnotherStop()).build();
		
		connections.add(first);
		connections.add(second);
		connections.add(third);
		int positionOfFirst = connections.positionOf(first);
		int positionOfSecond = connections.positionOf(second);
		int positionOfThird = connections.positionOf(third);
		
		assertThat(positionOfFirst, is(0));
		assertThat(positionOfSecond, is(1));
		assertThat(positionOfThird, is(2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failWhenConnectionIsNotIncluded() {
		Connection connection = connection().build();
		
		connections().positionOf(connection );
	}

	private Time someTime() {
		return someTime;
	}

	private Time oneMinuteLater() {
		return someTime.add(anotherRelativeTime);
	}

	private Time twoMinutesLater() {
		return someTime.add(otherRelativeTime);
	}

	private static Connections connections() {
		return new Connections();
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Journey oneJourney = journey().build();
		Journey anotherJourney = journey().withId(1).build();
		Connection oneConnection = connection().startsAt(someStop()).build();
		Connection anotherConnection = connection().startsAt(anotherStop()).build();
		EqualsVerifier
				.forClass(Connections.class)
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withPrefabValues(Journey.class, oneJourney, anotherJourney)
				.withPrefabValues(Connection.class, oneConnection, anotherConnection)
				.withOnlyTheseFields("connections")
				.usingGetClass()
				.verify();
	}
}
