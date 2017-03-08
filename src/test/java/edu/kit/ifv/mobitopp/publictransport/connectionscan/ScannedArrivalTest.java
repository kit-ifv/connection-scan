package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fourMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.threeMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.FootJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class ScannedArrivalTest {

	private static final int maximumNumberOfStops = 4;
	private static final RelativeTime defaultTransferTime = RelativeTime.of(0, MINUTES);

	private Stop stop1;
	private Stop stop2;
	private Stop stop3;
	private Stop otherStop;

	@Before
	public void initialise() throws Exception {
		stop1 = stop().withId(0).withName("stop 1").build();
		stop2 = stop().withId(1).withName("stop 2").build();
		stop3 = stop().withId(2).withName("stop 3").with(anotherLocation()).build();
		otherStop = stop().withId(2).withName("other stop").build();
	}

	@Test
	public void whenOneConnectionHasBeenUpdated() throws Exception {
		Connection connection = someConnection();

		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(connection);

		Optional<PublicTransportRoute> tour = arrivalTimes.createRoute(stop1, stop2, someTime());

		PublicTransportRoute expectedTour = new ScannedRoute(stop1, stop2, someTime(), oneMinuteLater(),
				asList(connection));
		assertThat(tour, isPresent());
		assertThat(tour, hasValue(expectedTour));
	}

	private Connection someConnection() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	@Test
	public void whenSeveralConnectionsHaveBeenUpdated() throws Exception {
		Connection connection1 = longConnection();
		Connection connection2 = shortConnection();

		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(connection1);
		arrivalTimes.updateArrival(connection2);

		Optional<PublicTransportRoute> tour1 = arrivalTimes.createRoute(stop1, stop2, someTime());
		Optional<PublicTransportRoute> tour2 = arrivalTimes.createRoute(stop1, otherStop(), someTime());

		PublicTransportRoute expectedTour1 = new ScannedRoute(stop1, stop2, someTime(),
				threeMinutesLater(), asList(connection1));
		assertThat(tour1, isPresent());
		assertThat(tour1, hasValue(expectedTour1));

		PublicTransportRoute expectedTour2 = new ScannedRoute(stop1, otherStop(), someTime(),
				twoMinutesLater(), asList(connection2));
		assertThat(tour2, isPresent());
		assertThat(tour2, hasValue(expectedTour2));
	}

	private Connection shortConnection() {
		return connection()
				.startsAt(stop1)
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private Connection longConnection() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	@Test
	public void createsToursWhenConnectionOfTheSameJourneyHasBeenTakenAlready() throws Exception {
		RelativeTime changeTime = RelativeTime.of(1, MINUTES);
		Journey sameJourney = journey().build();
		Connection connection1 = connection()
				.startsAt(stop1)
				.endsAt(stopWithChangeTime(changeTime))
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(sameJourney)
				.build();
		Connection connection2 = connection()
				.startsAt(stopWithChangeTime(changeTime))
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(sameJourney)
				.build();

		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(connection1);
		arrivalTimes.updateArrival(connection2);

		Optional<PublicTransportRoute> tour = arrivalTimes.createRoute(stop1, otherStop(), someTime());

		assertThat(tour, isPresent());
		PublicTransportRoute expectedTour = new ScannedRoute(stop1, otherStop(), someTime(),
				twoMinutesLater(), asList(connection1, connection2));
		assertThat(tour, hasValue(expectedTour));
	}

	@Test
	public void whenConnectionStartsAfterLatestDepartureAtTheEndStop() throws Exception {
		Connection connection1 = someConnection();
		Arrival arrivalTimes = newScannedArrival(stop1);

		assertThat(arrivalTimes.isTooLate(connection1, stop2), is(false));
		assertThat(arrivalTimes.isTooLate(tooLateConnection(), stop2), is(false));
		arrivalTimes.updateArrival(connection1);
		assertThat(arrivalTimes.isTooLate(connection1, stop2), is(false));
		assertThat(arrivalTimes.isTooLate(tooLateConnection(), stop2), is(true));
	}

	private Connection tooLateConnection() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(twoMinutesLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	@Test
	public void whenConnectionIsNotReachable() throws Exception {
		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(someConnection());
		arrivalTimes.updateArrival(notReachable());
		arrivalTimes.updateArrival(reachable());

		Optional<PublicTransportRoute> tour = arrivalTimes.createRoute(stop1, otherStop(), someTime());

		PublicTransportRoute expectedTour = new ScannedRoute(stop1, otherStop(), someTime(),
				twoMinutesLater(), asList(someConnection(), reachable()));
		assertThat(tour, isPresent());
		assertThat(tour, hasValue(expectedTour));
	}

	private Connection notReachable() {
		return connection()
				.startsAt(stop2)
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	private Connection reachable() {
		return connection()
				.startsAt(stop2)
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	@Test
	public void whenStopsBelongToSameEndStation() throws Exception {
		stop2.addNeighbour(stop3, defaultTransferTime);

		Connection viaStop2 = someConnection();
		Connection direct = direct();

		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(viaStop2);
		arrivalTimes.updateArrival(direct);

		Optional<PublicTransportRoute> tourToStop3 = arrivalTimes.createRoute(stop1, stop3, someTime());
		Optional<PublicTransportRoute> tourToStop2 = arrivalTimes.createRoute(stop1, stop2, someTime());

		PublicTransportRoute expectedTourToStop3 = new ScannedRoute(stop1, stop3, someTime(),
				oneMinuteLater(), asList(viaStop2, laterByFootFrom2To3()));
		assertThat(tourToStop3, isPresent());
		assertThat(tourToStop3, hasValue(expectedTourToStop3));

		PublicTransportRoute expectedTourToStop2 = new ScannedRoute(stop1, stop2, someTime(),
				oneMinuteLater(), asList(viaStop2));
		assertThat(tourToStop2, isPresent());
		assertThat(tourToStop2, hasValue(expectedTourToStop2));
	}

	private Connection laterByFootFrom2To3() {
		List<Point2D> footPoints = asList(someLocation(), anotherLocation());
		return connection()
				.startsAt(stop2)
				.endsAt(stop3)
				.departsAt(oneMinuteLater())
				.arrivesAt(oneMinuteLater())
				.partOf(FootJourney.footJourney)
				.with(footPoints)
				.build();
	}

	private Connection direct() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop3)
				.departsAt(someTime())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	@Test
	public void whenStopsAreInTheNeighbourhoodOfEachOtherButStopHasEarlierArrivalTime()
			throws Exception {
		stop3.addNeighbour(stop2, defaultTransferTime);

		Connection viaStop3 = viaStop3();
		Connection viaStop2 = viaStop2();

		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(viaStop3);
		arrivalTimes.updateArrival(viaStop2);

		Optional<PublicTransportRoute> tourToStop3 = arrivalTimes.createRoute(stop1, stop3, someTime());
		Optional<PublicTransportRoute> tourToStop2 = arrivalTimes.createRoute(stop1, stop2, someTime());

		PublicTransportRoute expectedTourToStop3 = new ScannedRoute(stop1, stop3, someTime(),
				oneMinuteLater(), asList(viaStop3));
		assertThat(tourToStop3, isPresent());
		assertThat(tourToStop3, hasValue(expectedTourToStop3));

		PublicTransportRoute expectedTourToStop2 = new ScannedRoute(stop1, stop2, someTime(),
				oneMinuteLater(), asList(viaStop3, laterByFootFrom3To2()));
		assertThat(tourToStop2, isPresent());
		assertThat(tourToStop2, hasValue(expectedTourToStop2));
	}

	private Connection laterByFootFrom3To2() {
		List<Point2D> footPoints = asList(anotherLocation(), someLocation());
		Connection byFootFromStop3ToStop2 = connection()
				.startsAt(stop3)
				.endsAt(stop2)
				.departsAt(oneMinuteLater())
				.arrivesAt(oneMinuteLater())
				.partOf(FootJourney.footJourney)
				.with(footPoints)
				.build();
		return byFootFromStop3ToStop2;
	}

	private Connection viaStop2() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop2)
				.departsAt(someTime())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private Connection viaStop3() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop3)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	private Double anotherLocation() {
		return new Point2D.Double(1, 0);
	}

	private Double someLocation() {
		return new Point2D.Double(0, 0);
	}

	@Test
	public void doesNotUpdateTimeAndConnectionWhenStopIsReachableViaTransferWalkWhichArrivesLaterThanAnAlreadySetConnection()
			throws Exception {
		stop2.addNeighbour(stop3, RelativeTime.of(1, MINUTES));
		stop3.addNeighbour(stop2, RelativeTime.of(1, MINUTES));

		Connection directToStop3 = viaStop3();
		Connection directToStop2 = someConnection();

		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(directToStop3);
		arrivalTimes.updateArrival(directToStop2);

		Optional<PublicTransportRoute> tourToStop3 = arrivalTimes.createRoute(stop1, stop3, someTime());
		Optional<PublicTransportRoute> tourToStop2 = arrivalTimes.createRoute(stop1, stop2, someTime());

		PublicTransportRoute expectedTourToStop3 = new ScannedRoute(stop1, stop3, someTime(),
				oneMinuteLater(), asList(directToStop3));
		assertThat(tourToStop3, isPresent());
		assertThat(tourToStop3, hasValue(expectedTourToStop3));

		PublicTransportRoute expectedTourToStop2 = new ScannedRoute(stop1, stop2, someTime(),
				oneMinuteLater(), asList(directToStop2));
		assertThat(tourToStop2, isPresent());
		assertThat(tourToStop2, hasValue(expectedTourToStop2));
	}

	@Test
	public void whenStartStopsAreInOneNeighbourhood() throws Exception {
		stop2.addNeighbour(stop3, defaultTransferTime);

		Connection viaStop3 = from3To1();
		Connection direct = from2To1();
		Connection transferToStop3 = byFootFrom2To3();

		Arrival arrivalTimes = newScannedArrival(stop2);

		arrivalTimes.updateArrival(viaStop3);
		arrivalTimes.updateArrival(direct);

		Optional<PublicTransportRoute> routeToStop1 = arrivalTimes.createRoute(stop2, stop1,
				someTime());
		Optional<PublicTransportRoute> routeToStop3 = arrivalTimes.createRoute(stop2, stop3,
				someTime());

		PublicTransportRoute expectedRouteToStop1 = new ScannedRoute(stop2, stop1, someTime(),
				oneMinuteLater(), asList(transferToStop3, viaStop3));
		assertThat(routeToStop1, isPresent());
		assertThat(routeToStop1, hasValue(expectedRouteToStop1));

		PublicTransportRoute expectedRouteToStop3 = new ScannedRoute(stop2, stop3, someTime(),
				someTime(), asList(transferToStop3));
		assertThat(routeToStop3, isPresent());
		assertThat(routeToStop3, hasValue(expectedRouteToStop3));
	}

	private Connection byFootFrom2To3() {
		List<Point2D> footPoints = asList(someLocation(), anotherLocation());
		Connection transferToStop2 = connection()
				.startsAt(stop2)
				.endsAt(stop3)
				.departsAt(someTime())
				.arrivesAt(someTime())
				.partOf(FootJourney.footJourney)
				.with(footPoints)
				.build();
		return transferToStop2;
	}

	private Connection from2To1() {
		return connection()
				.startsAt(stop2)
				.endsAt(stop1)
				.departsAt(someTime())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	private Connection from3To1() {
		return connection()
				.startsAt(stop3)
				.endsAt(stop1)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	@Test
	public void whenStopIsReachableViaTransferWalk() throws Exception {
		stop2.addNeighbour(stop3, RelativeTime.of(1, MINUTES));
		Connection viaStop2 = someConnection();
		Connection direct = directToStopWithNeighbour();

		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(viaStop2);
		arrivalTimes.updateArrival(direct);

		Optional<PublicTransportRoute> tourToStop3 = arrivalTimes.createRoute(stop1, stop3, someTime());
		Optional<PublicTransportRoute> tourToStop2 = arrivalTimes.createRoute(stop1, stop2, someTime());

		Connection byFootFromStop2ToStop3 = footPathBetweenNeighbours();
		PublicTransportRoute expectedTourToStop3 = new ScannedRoute(stop1, stop3, someTime(),
				twoMinutesLater(), asList(viaStop2, byFootFromStop2ToStop3));
		assertThat(tourToStop3, isPresent());
		assertThat(tourToStop3, hasValue(expectedTourToStop3));

		PublicTransportRoute expectedTourToStop2 = new ScannedRoute(stop1, stop2, someTime(),
				oneMinuteLater(), asList(viaStop2));
		assertThat(tourToStop2, isPresent());
		assertThat(tourToStop2, hasValue(expectedTourToStop2));
	}

	private Connection footPathBetweenNeighbours() {
		List<Point2D> footPoints = asList(someLocation(), anotherLocation());
		Connection byFootFromStop2ToStop3 = connection()
				.startsAt(stop2)
				.endsAt(stop3)
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(FootJourney.footJourney)
				.with(footPoints)
				.build();
		return byFootFromStop2ToStop3;
	}

	private Connection directToStopWithNeighbour() {
		return connection()
				.startsAt(stop1)
				.endsAt(stop3)
				.departsAt(someTime())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	@Test
	public void whenStopIsNotReachable() throws Exception {
		Connection connection = someConnection();

		Arrival arrivalTimes = newScannedArrival(stop1);

		arrivalTimes.updateArrival(connection);

		Stop unreachable = stop().withId(2).withName("unreachable").build();
		Optional<PublicTransportRoute> tour = arrivalTimes.createRoute(stop1, unreachable, someTime());

		assertThat(tour, isEmpty());
	}

	@Test
	public void usesWalkTimeDuringInitialisation() throws Exception {
		Stop start = stop2;
		Stop neighbouringStop = stop3;
		start.addNeighbour(neighbouringStop, RelativeTime.of(1, MINUTES));

		Connection transferToStop2 = connection()
				.startsAt(start)
				.endsAt(neighbouringStop)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(FootJourney.footJourney)
				.with(asList(someLocation(), anotherLocation()))
				.build();

		Arrival arrivalTimes = newScannedArrival(start);
		Optional<PublicTransportRoute> tourToStop2 = arrivalTimes.createRoute(start, neighbouringStop,
				someTime());

		PublicTransportRoute expectedTourToStop2 = new ScannedRoute(start, neighbouringStop, someTime(),
				oneMinuteLater(), asList(transferToStop2));

		assertThat(tourToStop2, isPresent());
		assertThat(tourToStop2, hasValue(expectedTourToStop2));
	}

	@Test
	public void doesNotUseMinimumChangeTimeToReachStartStop() throws Exception {
		RelativeTime changeTime = RelativeTime.of(1, MINUTES);
		Stop start = stopWithChangeTime(changeTime);
		Stop end = stop2;
		Journey someJourney = journey().build();
		Connection connection = connection()
				.startsAt(start)
				.endsAt(end)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney)
				.build();

		Arrival arrivalTimes = newScannedArrival(start);

		arrivalTimes.updateArrival(connection);

		Optional<PublicTransportRoute> tour = arrivalTimes.createRoute(start, end, someTime());

		assertThat(tour, isPresent());
		PublicTransportRoute expectedTour = new ScannedRoute(start, end, someTime(), oneMinuteLater(),
				asList(connection));
		assertThat(tour, hasValue(expectedTour));
	}

	@Test
	public void doesNotUseMinimumChangeTimeToReachEndStop() throws Exception {
		RelativeTime changeTime = RelativeTime.of(1, MINUTES);
		Stop start = stop1;
		Stop end = stopWithChangeTime(changeTime);
		Journey someJourney = journey().build();
		Connection connection = connection()
				.startsAt(start)
				.endsAt(end)
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.partOf(someJourney)
				.build();

		Arrival arrivalTimes = newScannedArrival(start);

		arrivalTimes.updateArrival(connection);

		Optional<PublicTransportRoute> tour = arrivalTimes.createRoute(start, end, someTime());

		assertThat(tour, isPresent());
		PublicTransportRoute expectedTour = new ScannedRoute(start, end, someTime(), oneMinuteLater(),
				asList(connection));
		assertThat(tour, hasValue(expectedTour));
	}

	@Test
	public void usesMinimumChangeTimeOnlyForReachabilityCheckNotToCompareArrivalTimes()
			throws Exception {
		RelativeTime changeTime = RelativeTime.of(2, MINUTES);

		Arrival arrvalTimes = newScannedArrival(stop1);
		arrvalTimes.updateArrival(someConnection());
		arrvalTimes.updateArrival(toIntermediateStop(changeTime));
		arrvalTimes.updateArrival(laterToIntermediateStop(changeTime));
		arrvalTimes.updateArrival(toEndStop(changeTime));
		Optional<PublicTransportRoute> tour = arrvalTimes.createRoute(stop1, otherStop(), someTime());

		assertThat(tour, isPresent());
		PublicTransportRoute expectedTour = new ScannedRoute(stop1, otherStop(), someTime(),
				fourMinutesLater(),
				asList(someConnection(), toIntermediateStop(changeTime), toEndStop(changeTime)));
		assertThat(tour, hasValue(expectedTour));
	}

	@Test
	public void usesFootpaths() throws Exception {
		Stop startStop = stop1;
		Stop endStop = stop2;
		List<Stop> startStops = asList(startStop);
		Connection someConnection = someConnection();
		Times times = mock(Times.class);
		UsedConnections usedConnections = mock(UsedConnections.class);
		StopPaths start = mock(StopPaths.class);
		StopPaths reachableEnd = mock(StopPaths.class);
		when(start.stops()).thenReturn(startStops);
		when(reachableEnd.stopWithEarliestArrival(times)).thenReturn(of(endStop));
		when(times.get(endStop)).thenReturn(oneMinuteLater());
		Time scanTime = someTime();
		when(usedConnections.buildUpConnection(start, endStop, scanTime))
				.thenReturn(asList(someConnection));

		Arrival arrival = newScannedArrival(times, usedConnections);

		Optional<PublicTransportRoute> tour = arrival.createRoute(start, reachableEnd, scanTime);

		assertThat(tour, isPresent());
		PublicTransportRoute expectedTour = new ScannedRoute(stop1, endStop, scanTime, oneMinuteLater(),
				asList(someConnection()));
		assertThat(tour, hasValue(expectedTour));
		verify(reachableEnd).stopWithEarliestArrival(times);
		verify(times).get(endStop);
		verify(usedConnections).buildUpConnection(start, endStop, scanTime);
	}

	@Test
	public void doesNotCreateTourWhenNoConnectionsCanBeFound() throws Exception {
		Times times = mock(Times.class);
		UsedConnections usedConnections = mock(UsedConnections.class);
		StopPaths start = mock(StopPaths.class);
		StopPaths end = mock(StopPaths.class);
		Stop startStop = stop2;
		Stop endStop = stop1;
		List<Stop> startStops = asList(startStop);
		when(end.stopWithEarliestArrival(times)).thenReturn(of(endStop));
		when(start.stops()).thenReturn(startStops);
		Time scanTime = someTime();
		when(times.get(any())).thenReturn(scanTime);
		when(usedConnections.buildUpConnection(start, endStop, scanTime)).thenReturn(emptyList());
		Arrival arrival = newScannedArrival(times, usedConnections);

		Optional<PublicTransportRoute> tour = arrival.createRoute(start, end, scanTime);

		assertThat(tour, isEmpty());
		verify(usedConnections).buildUpConnection(start, endStop, scanTime);
	}

	private Connection toEndStop(RelativeTime changeTime) {
		return connection()
				.startsAt(stopWithChangeTime(changeTime))
				.endsAt(otherStop())
				.departsAt(fourMinutesLater())
				.arrivesAt(fourMinutesLater())
				.build();
	}

	private Connection laterToIntermediateStop(RelativeTime changeTime) {
		return connection()
				.startsAt(stop2)
				.endsAt(stopWithChangeTime(changeTime))
				.departsAt(twoMinutesLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	private Connection toIntermediateStop(RelativeTime changeTime) {
		return connection()
				.startsAt(stop2)
				.endsAt(stopWithChangeTime(changeTime))
				.departsAt(oneMinuteLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}

	private Stop otherStop() {
		return otherStop;
	}

	private Stop stopWithChangeTime(RelativeTime changeTime) {
		return stop()
				.withId(3)
				.withName("containing change time")
				.minimumChangeTime(changeTime)
				.build();
	}

	private Arrival newScannedArrival(Stop start) {
		Times times = SingleStart.from(start, someTime(), maximumNumberOfStops);
		UsedConnections usedConnections = new ArrivalConnections(maximumNumberOfStops);
		return ScannedArrival.from(times, usedConnections);
	}

	private Arrival newScannedArrival(Times times, UsedConnections connections) {
		UsedJourneys usedJourney = mock(UsedJourneys.class);
		return ScannedArrival.from(times, connections, usedJourney);
	}

}
