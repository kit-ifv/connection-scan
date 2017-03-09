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
import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class PreparedConnectionsTest {

	private static final int cancelAlways = 1;
	private SweeperData data;
	private Optional<PublicTransportRoute> someRoute;

	@Before
	public void initialise() throws Exception {
		someRoute = of(mock(PublicTransportRoute.class));
		data = mock(SweeperData.class);
		when(data.isTooLateAtOne(any(), any())).thenReturn(false);
	}

	@Test
	public void findRouteWithOneAvailableConnection() throws Exception {
		when(data.isTooLate(from1To2(), anotherStop())).thenReturn(false);
		when(data.createRoute(someStop(), anotherStop(), someTime())).thenReturn(someRoute);
		PreparedConnections connections = connections(from1To2());

		Optional<PublicTransportRoute> startToStop = connections.sweep(data, someStop(),
				anotherStop(), someTime());

		assertThat(startToStop, is(equalTo(someRoute)));
		verify(data).isTooLate(from1To2(), anotherStop());
		verify(data).updateArrival(from1To2());
		verify(data).createRoute(someStop(), anotherStop(), someTime());
	}

	@Test
	public void findRouteInConnectionsWithDifferentArrivalTimes() throws Exception {
		when(data.isTooLate(from1To2(), anotherStop())).thenReturn(false);
		when(data.isTooLate(from1To2Long(), anotherStop())).thenReturn(false);
		when(data.createRoute(someStop(), anotherStop(), someTime())).thenReturn(someRoute);
		PreparedConnections connections = connections(from1To2(), from1To2Long());

		Optional<PublicTransportRoute> startToStop = connections.sweep(data, someStop(),
				anotherStop(), someTime());

		assertThat(startToStop, is(equalTo(someRoute)));
		verify(data).isTooLate(from1To2(), anotherStop());
		verify(data).isTooLate(from1To2Long(), anotherStop());
		verify(data).updateArrival(from1To2());
		verify(data).updateArrival(from1To2Long());
		verify(data).createRoute(someStop(), anotherStop(), someTime());
	}

	@Test
	public void findRouteInSeveralConnections() throws Exception {
		when(data.isTooLate(any(), any())).thenReturn(false);
		when(data.createRoute(someStop(), yetAnotherStop(), someTime())).thenReturn(someRoute);
		PreparedConnections connections = connections(from1To2(), from2To3(), from3To4());

		Optional<PublicTransportRoute> startToStop = connections.sweep(data, someStop(),
				yetAnotherStop(), someTime());

		assertThat(startToStop, is(equalTo(someRoute)));
		verify(data).isTooLate(from1To2(), yetAnotherStop());
		verify(data).isTooLate(from2To3(), yetAnotherStop());
		verify(data).isTooLate(from3To4(), yetAnotherStop());
		verify(data).updateArrival(from1To2());
		verify(data).updateArrival(from2To3());
		verify(data).updateArrival(from3To4());
		verify(data).createRoute(someStop(), yetAnotherStop(), someTime());
	}

	@Test
	public void findNoRouteWhenDepartureTimeOfConnectionsIsOver() throws Exception {
		when(data.createRoute(someStop(), anotherStop(), oneMinuteLater())).thenReturn(someRoute);
		PreparedConnections connections = connections(from1To2());

		Optional<PublicTransportRoute> startToStop = connections.sweep(data, someStop(),
				anotherStop(), oneMinuteLater());

		assertThat(startToStop, is(equalTo(someRoute)));
		verify(data).createRoute(someStop(), anotherStop(), oneMinuteLater());
		verifyNoMoreInteractions(data);
	}

	@Test
	public void cancelsScanWhenConnectionsIsAfterArrivalAtOneOfSeveralEndStops() throws Exception {
		List<Stop> endStops = asList(anotherStop(), otherStop());
		PreparedConnections connections = alwaysCancelConnections(from1To2(), from2To3());
		StopPaths start = mock(StopPaths.class);
		StopPaths end = mock(StopPaths.class);
		when(end.stops()).thenReturn(endStops);
		when(data.isTooLateAtOne(from2To3(), endStops)).thenReturn(true);

		connections.sweep(data, start, end, someTime());

		verify(end).stops();
		verify(data).isTooLateAtOne(from1To2(), endStops);
		verify(data).isTooLateAtOne(from2To3(), endStops);
		verify(data).updateArrival(from1To2());
		verify(data).createRoute(eq(start), eq(end), eq(someTime()));
		verifyNoMoreInteractions(data);
	}

	@Test
	public void cancelsScanWhenConnectionsIsAfterArrivalAtAnotherOfSeveralEndStops()
			throws Exception {
		List<Stop> endStops = asList(anotherStop(), otherStop());
		PreparedConnections connections = alwaysCancelConnections(from1To2(), from2To3());
		StopPaths start = mock(StopPaths.class);
		StopPaths end = mock(StopPaths.class);
		when(end.stops()).thenReturn(endStops);
		when(data.isTooLateAtOne(from2To3(), endStops)).thenReturn(true);

		connections.sweep(data, start, end, someTime());

		verify(end).stops();
		verify(data).isTooLateAtOne(from1To2(), endStops);
		verify(data).isTooLateAtOne(from2To3(), endStops);
		verify(data).updateArrival(from1To2());
		verify(data).createRoute(eq(start), eq(end), eq(someTime()));
		verifyNoMoreInteractions(data);
	}

	@Test
	public void findsRouteFromSeveralStartsStopsToSeveralEndStops() throws Exception {
		StopPaths start = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(data.createRoute(start, reachableEnd, someTime())).thenReturn(someRoute);
		PreparedConnections connections = alwaysCancelConnections(from1To2(), from3To4());

		Optional<PublicTransportRoute> shortestRoute = connections.sweep(data, start, reachableEnd,
				someTime());

		assertThat(shortestRoute, is(equalTo(someRoute)));
		verify(data).updateArrival(from1To2());
		verify(data).updateArrival(from3To4());
		verify(data).createRoute(eq(start), eq(reachableEnd), eq(someTime()));
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

		assertThat(connections(), is(tooLate(someTime)));
	}

	@Test
	public void isNotTooLateWhenTimeIsBeforeDepartureOfSingleConnection() throws Exception {
		Time laterTime = oneMinuteLater();

		Connection connection = connection().departsAt(laterTime).build();
		PreparedConnections connections = connections(connection);

		Time earlyEnoughTime = someTime();
		assertThat(connections, is(not(tooLate(earlyEnoughTime))));
	}

	@Test
	public void isTooLateWhenTimeIsAfterDepartureOfSingleConnection() throws Exception {
		Time earlierTime = someTime();

		Connection connection = connection().departsAt(earlierTime).build();
		PreparedConnections connections = connections(connection);

		Time tooLateTime = oneMinuteLater();
		assertThat(connections, is(tooLate(tooLateTime)));
	}

	@Test
	public void isTooLateWhenTimeIsAfterDepartureOfSeveralSortedConnections() throws Exception {
		Connection earlierConnection = connection().departsAt(someTime()).build();
		Connection laterConnection = connection().departsAt(oneMinuteLater()).build();
		PreparedConnections connections = connections(earlierConnection, laterConnection);

		assertThat(connections, is(tooLate(twoMinutesLater())));
	}

	@Test
	public void isTooLateWhenTimeIsAfterDepartureOfSeveralNotSortedConnections() throws Exception {
		Connection earlierConnection = connection().departsAt(someTime()).build();
		Connection laterConnection = connection().departsAt(oneMinuteLater()).build();
		PreparedConnections connections = connections(laterConnection, earlierConnection);

		assertThat(connections, is(tooLate(twoMinutesLater())));
	}

	@Test
	public void isNotTooLateWhenTimeIsAfterDepartureEarlierConnectionWhenConnectionsAreNotSorted()
			throws Exception {
		Connection earlierConnection = connection().departsAt(someTime()).build();
		Connection laterConnection = connection().departsAt(twoMinutesLater()).build();
		PreparedConnections connections = connections(laterConnection, earlierConnection);

		assertThat(connections, is(not(tooLate(oneMinuteLater()))));
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

	private static Matcher<PreparedConnections> tooLate(Time time) {
		return new TypeSafeMatcher<PreparedConnections>() {

			@Override
			public void describeTo(Description description) {
				description.appendValue(time);
				description.appendText("too late");
			}

			@Override
			protected boolean matchesSafely(PreparedConnections connections) {
				return connections.isTooLate(time);
			}
		};
	}
}
