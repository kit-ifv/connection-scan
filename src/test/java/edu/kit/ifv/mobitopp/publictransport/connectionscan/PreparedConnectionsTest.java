package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.threeMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.yetAnotherStop;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class PreparedConnectionsTest {

	private static final int cancelAlways = 1;
	private SweeperData data;
	private Optional<PublicTransportRoute> someRoute;

	@Before
	public void initialise() throws Exception {
		someRoute = of(mock(PublicTransportRoute.class));
		data = mock(SweeperData.class);
		when(data.isAfterArrivalAtEnd(any())).thenReturn(false);
	}

	@Test
	public void findRouteWithOneAvailableConnection() {
		departAt(someTime());
		when(data.isAfterArrivalAtEnd(from1To2())).thenReturn(false);
		when(data.createRoute()).thenReturn(someRoute);
		PreparedConnections connections = connections(from1To2());

		Optional<PublicTransportRoute> startToStop = connections.sweep(data);

		assertThat(startToStop, is(equalTo(someRoute)));
		verify(data).isAfterArrivalAtEnd(from1To2());
		verify(data).updateArrival(from1To2());
		verify(data).createRoute();
	}

	@Test
	public void findRouteInConnectionsWithDifferentArrivalTimes() {
		departAt(someTime());
		when(data.isAfterArrivalAtEnd(from1To2())).thenReturn(false);
		when(data.isAfterArrivalAtEnd(from1To2Long())).thenReturn(false);
		when(data.createRoute()).thenReturn(someRoute);
		PreparedConnections connections = alwaysCancelConnections(from1To2(), from1To2Long());

		Optional<PublicTransportRoute> startToStop = connections.sweep(data);

		assertThat(startToStop, is(equalTo(someRoute)));
		verify(data).isAfterArrivalAtEnd(from1To2());
		verify(data).isAfterArrivalAtEnd(from1To2Long());
		verify(data).updateArrival(from1To2());
		verify(data).updateArrival(from1To2Long());
		verify(data).createRoute();
	}

	@Test
	public void findRouteInSeveralConnections() {
		departAt(someTime());
		when(data.isAfterArrivalAtEnd(any())).thenReturn(false);
		when(data.createRoute()).thenReturn(someRoute);
		PreparedConnections connections = alwaysCancelConnections(from1To2(), from2To3(), from3To4());

		Optional<PublicTransportRoute> startToStop = connections.sweep(data);

		assertThat(startToStop, is(equalTo(someRoute)));
		verify(data).isAfterArrivalAtEnd(from1To2());
		verify(data).isAfterArrivalAtEnd(from2To3());
		verify(data).isAfterArrivalAtEnd(from3To4());
		verify(data).updateArrival(from1To2());
		verify(data).updateArrival(from2To3());
		verify(data).updateArrival(from3To4());
		verify(data).createRoute();
	}

	@Test
	public void findNoRouteWhenDepartureTimeOfConnectionsIsOver() {
		departAt(oneMinuteLater());
		when(data.createRoute()).thenReturn(someRoute);
		PreparedConnections connections = alwaysCancelConnections(from1To2());

		Optional<PublicTransportRoute> startToStop = connections.sweep(data);

		assertThat(startToStop, is(equalTo(someRoute)));
		verify(data).startTime();
		verify(data).createRoute();
		verifyNoMoreInteractions(data);
	}

	@Test
	public void cancelsScanWhenConnectionsIsAfterArrivalAtEnd()
			throws Exception {
		PreparedConnections connections = alwaysCancelConnections(from1To2(), from2To3());
		when(data.startTime()).thenReturn(someTime());
		when(data.isAfterArrivalAtEnd(from2To3())).thenReturn(true);

		connections.sweep(data);

		verify(data).startTime();
		verify(data).isAfterArrivalAtEnd(from1To2());
		verify(data).isAfterArrivalAtEnd(from2To3());
		verify(data).updateArrival(from1To2());
		verify(data).createRoute();
		verifyNoMoreInteractions(data);
	}

	private void departAt(Time time) {
		when(data.startTime()).thenReturn(time);
	}

	private Connection from1To2() {
		return builderFrom1To2().build();
	}

	private ConnectionBuilder builderFrom1To2() {
		return connection().startsAt(someStop()).endsAt(anotherStop()).departsAt(someTime()).arrivesAt(
				oneMinuteLater());
	}

	private Connection from1To2Long() {
		return builderFrom1To2().but().arrivesAt(twoMinutesLater()).build();
	}

	private Connection from2To3() {
		return connection()
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private Connection from3To4() {
		return connection()
				.startsAt(otherStop())
				.endsAt(yetAnotherStop())
				.departsAt(twoMinutesLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	@Test
	public void timeIsTooLateWhenNoConnectionsAreAvailable() throws Exception {
		Time someTime = someTime();

		assertThat(connections(), is(afterLatestDeparture(someTime)));
	}

	@Test
	public void isNotTooLateWhenTimeIsBeforeDepartureOfSingleConnection() throws Exception {
		Time laterTime = oneMinuteLater();

		Connection connection = connection().departsAt(laterTime).build();
		PreparedConnections connections = connections(connection);

		Time earlyEnoughTime = someTime();
		assertThat(connections, is(not(afterLatestDeparture(earlyEnoughTime))));
	}

	@Test
	public void isTooLateWhenTimeIsAfterDepartureOfSingleConnection() throws Exception {
		Time earlierTime = someTime();

		Connection connection = connection().departsAt(earlierTime).build();
		PreparedConnections connections = connections(connection);

		Time tooLateTime = oneMinuteLater();
		assertThat(connections, is(afterLatestDeparture(tooLateTime)));
	}

	@Test
	public void isTooLateWhenTimeIsAfterDepartureOfSeveralSortedConnections() throws Exception {
		Connection earlierConnection = connection().departsAt(someTime()).build();
		Connection laterConnection = connection().departsAt(oneMinuteLater()).build();
		PreparedConnections connections = connections(earlierConnection, laterConnection);

		assertThat(connections, is(afterLatestDeparture(twoMinutesLater())));
	}

	@Test
	public void isTooLateWhenTimeIsAfterDepartureOfSeveralNotSortedConnections() throws Exception {
		Connection earlierConnection = connection().departsAt(someTime()).build();
		Connection laterConnection = connection().departsAt(oneMinuteLater()).build();
		PreparedConnections connections = connections(laterConnection, earlierConnection);

		assertThat(connections, is(afterLatestDeparture(twoMinutesLater())));
	}

	@Test
	public void isNotTooLateWhenTimeIsAfterDepartureEarlierConnectionWhenConnectionsAreNotSorted()
			throws Exception {
		Connection earlierConnection = connection().departsAt(someTime()).build();
		Connection laterConnection = connection().departsAt(twoMinutesLater()).build();
		PreparedConnections connections = connections(laterConnection, earlierConnection);

		assertThat(connections, is(not(afterLatestDeparture(oneMinuteLater()))));
	}

	private static PreparedConnections alwaysCancelConnections(Connection... connections) {
		return PreparedConnections.from(validated(connections), cancelAlways);
	}

	private static PreparedConnections connections(Connection... connections) {
		return PreparedConnections.from(validated(connections));
	}

	private static Connections validated(Connection... connections) {
		Connections validatedConnections = new Connections();
		for (Connection connection : connections) {
			validatedConnections.add(connection);
		}
		return validatedConnections;
	}

	private static Matcher<PreparedConnections> afterLatestDeparture(Time time) {
		return new TypeSafeMatcher<PreparedConnections>() {

			@Override
			public void describeTo(Description description) {
				description.appendValue(time);
				description.appendText("too late");
			}

			@Override
			protected boolean matchesSafely(PreparedConnections connections) {
				return connections.areDepartedBefore(time);
			}
		};
	}
}
