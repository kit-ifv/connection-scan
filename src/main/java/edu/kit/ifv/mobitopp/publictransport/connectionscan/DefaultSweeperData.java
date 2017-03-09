package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class DefaultSweeperData implements SweeperData {

	private static final int firstConnection = 0;
	private final Times times;
	private final UsedConnections arrivalConnections;
	private final UsedJourneys usedJourneys;

	private DefaultSweeperData(
			Times times, UsedConnections arrivalConnections, UsedJourneys usedJourneys) {
		super();
		this.times = times;
		this.arrivalConnections = arrivalConnections;
		this.usedJourneys = usedJourneys;
	}

	static SweeperData from(Stop start, Stop end, Time departure, int numberOfStops) {
		Times times = SingleStart.from(start, end, departure, numberOfStops);
		UsedConnections usedConnections = new ArrivalConnections(numberOfStops);
		return from(times, usedConnections);
	}

	static SweeperData from(StopPaths fromStarts, StopPaths toEnds, Time atTime, int numberOfStops) {
		Times times = MultipleStarts.from(fromStarts, toEnds, atTime, numberOfStops);
		UsedConnections usedConnections = new ArrivalConnections(numberOfStops);
		return from(times, usedConnections);
	}

	static SweeperData from(Times times, UsedConnections usedConnections) {
		UsedJourneys usedJourneys = new ScannedJourneys();
		return from(times, usedConnections, usedJourneys);
	}

	static SweeperData from(Times times, UsedConnections arrivalConnections, UsedJourneys usedJourneys) {
		DefaultSweeperData data = new DefaultSweeperData(times, arrivalConnections, usedJourneys);
		times.initialise(data::initialise);
		return data;
	}

	private void initialise(Stop start, Time departure) {
		for (Stop neighbour : start.neighbours()) {
			start.arrivalAt(neighbour, departure).ifPresent(
					arrival -> updateTransfer(start, neighbour, departure, arrival));
		}
	}

	private void updateTransfer(Stop fromStart, Stop end, Time departure, Time arrival) {
		Connection connection = Connection.byFootFrom(fromStart, end, departure, arrival);
		updateTimeAndConnection(connection);
	}

	private void updateTimeAndConnection(Connection connection) {
		Stop end = connection.end();
		Time arrival = connection.arrival();
		times.set(end, arrival);
		arrivalConnections.update(end, connection);
		usedJourneys.use(connection.journey());
	}

	/**
	 * Return as soon as the connection departs after the earliest arrival at one of the stops.
	 */
	@Override
	public boolean isTooLateAtOne(Connection connection, List<Stop> endStops) {
		for (Stop stop : endStops) {
			if (isTooLate(connection, stop)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateArrival(Connection connection) {
		if (isNotReachable(connection)) {
			return;
		}
		Time currentArrival = times.get(connection.end());
		if (currentArrival.isLaterThan(connection.arrival())) {
			updateArrivalInternal(connection);
		}
	}

	private boolean isNotReachable(Connection connection) {
		if (usedJourneys.used(connection.journey())) {
			return false;
		}
		Time currentArrival = times.getConsideringMinimumChangeTime(connection.start());
		return currentArrival.isLaterThan(connection.departure());
	}

	private void updateArrivalInternal(Connection connection) {
		updateTimeAndConnection(connection);
		updateArrivalAtNeighbours(connection);
	}

	private void updateArrivalAtNeighbours(Connection connection) {
		Stop end = connection.end();
		Time arrival = connection.arrival();
		for (Stop neighbour : end.neighbours()) {
			end.arrivalAt(neighbour, arrival).ifPresent(arrivalByFoot -> updateArrivalByFoot(end,
					neighbour, connection.arrival(), arrivalByFoot));
		}
	}

	private void updateArrivalByFoot(Stop start, Stop end, Time arrivalAtStart, Time arrivalAtEnd) {
		Time currentArrival = times.get(end);
		if (currentArrival.isLaterThan(arrivalAtEnd)) {
			updateTransfer(start, end, arrivalAtStart, arrivalAtEnd);
		}
	}

	@Override
	public Optional<PublicTransportRoute> createRoute(Stop start, Stop end, Time time) {
		try {
			List<Connection> connections = arrivalConnections.buildUpConnection(start, end);
			return createRoute(start, end, time, connections);
		} catch (StopNotReachable e) {
			return empty();
		}
	}

	private Optional<PublicTransportRoute> createRoute(
			Stop start, Stop end, Time time, List<Connection> connections) {
		Time arrivalTime = times.get(end);
		return of(new ScannedRoute(start, end, time, arrivalTime, connections));
	}

	@Override
	public Optional<PublicTransportRoute> createRoute(
			StopPaths starts, StopPaths toEnd, Time time) {
		Optional<Stop> end = times.stopWithEarliestArrival();
		return end.flatMap(stop -> createRoute(starts, stop, time));
	}

	private Optional<PublicTransportRoute> createRoute(StopPaths starts, Stop end, Time time) {
		try {
			List<Connection> connections = arrivalConnections.buildUpConnection(starts, end, time);
			if (connections.isEmpty()) {
				return empty();
			}
			Stop start = firstStopOf(connections);
			return createRoute(start, end, time, connections);
		} catch (StopNotReachable e) {
			return empty();
		}
	}

	private Stop firstStopOf(List<Connection> connections) {
		return connections.get(firstConnection).start();
	}

	@Override
	public boolean isTooLate(Connection connection, Stop end) {
		Time arrival = times.getConsideringMinimumChangeTime(end);
		return arrival.isBefore(connection.departure());
	}
}