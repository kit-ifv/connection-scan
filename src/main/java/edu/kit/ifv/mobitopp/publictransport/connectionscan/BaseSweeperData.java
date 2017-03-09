package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.of;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public abstract class BaseSweeperData implements SweeperData {

	private final Times times;
	private final UsedConnections usedConnections;
	private final UsedJourneys usedJourneys;

	public BaseSweeperData(Times times, UsedConnections usedConnections, UsedJourneys usedJourneys) {
		super();
		this.times = times;
		this.usedConnections = usedConnections;
		this.usedJourneys = usedJourneys;
	}

	protected void initialise(Stop start, Time departure) {
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
		usedConnections.update(end, connection);
		usedJourneys.use(connection.journey());
	}
	
	protected UsedConnections usedConnections() {
		return usedConnections;
	}
	
	protected Times times() {
		return times;
	}

	@Override
	public Time atTime() {
		return times.startTime();
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
	public boolean isTooLate(Connection connection) {
		return times.isAfterArrivalAtEnd(connection.departure());
	}

	protected Optional<PublicTransportRoute> createRoute(Stop start, Stop end, Time time, List<Connection> connections) {
		Time arrivalTime = times().get(end);
		return of(new ScannedRoute(start, end, time, arrivalTime, connections));
	}

}