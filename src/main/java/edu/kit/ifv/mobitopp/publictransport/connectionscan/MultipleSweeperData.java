package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class MultipleSweeperData extends BaseSweeperData {

	private static final int firstConnection = 0;
	private final StopPaths starts;
	private final StopPaths toEnds;
	
	private MultipleSweeperData(
			StopPaths starts, StopPaths toEnds, Times times, UsedConnections usedConnections, UsedJourneys usedJourneys) {
		super(times, usedConnections, usedJourneys);
		this.starts = starts;
		this.toEnds = toEnds;
	}

	static SweeperData from(StopPaths fromStarts, StopPaths toEnds, Time atTime, int numberOfStops) {
		Times times = MultipleStarts.from(fromStarts, atTime, numberOfStops);
		UsedConnections usedConnections = new ArrivalConnections(numberOfStops);
		UsedJourneys usedJourneys = new ScannedJourneys();
		return from(fromStarts, toEnds, times, usedConnections, usedJourneys);
	}

	static SweeperData from(
			StopPaths fromStarts, StopPaths toEnds, Times times, UsedConnections usedConnections,
			UsedJourneys usedJourneys) {
		MultipleSweeperData data = new MultipleSweeperData(fromStarts, toEnds, times, usedConnections,
				usedJourneys);
		times.initialise(data::initialise);
		return data;
	}

	@Override
	public Optional<PublicTransportRoute> createRoute() {
		Optional<Stop> toEnd = stopWithEarliestArrival();
		return toEnd.flatMap(end -> createRoute(starts, end, times().startTime()));
	}

	private Optional<Stop> stopWithEarliestArrival() {
		Stop stop = null;
		Time currentArrival = null;
		for (StopPath path : toEnds.stopPaths()) {
			Stop current = path.stop();
			Time currentTime = times().get(current);
			Time includingFootpath = path.arrivalTimeStartingAt(currentTime);
			if (null == currentArrival || includingFootpath.isBefore(currentArrival)) {
				stop = current;
				currentArrival = currentTime;
			}
		}
		return ofNullable(stop);
	}

	private Optional<PublicTransportRoute> createRoute(StopPaths starts, Stop end, Time time) {
		try {
			List<Connection> connections = usedConnections().buildUpConnection(starts, end, time);
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
	public boolean isAfterArrivalAtEnd(Connection connection) {
		return isAfterArrivalAtEnd(connection.departure());
	}
	
	private boolean isAfterArrivalAtEnd(Time departure) {
		for (Stop stop : toEnds.stops()) {
			if (isTooLateAt(departure, stop)) {
				return true;
			}
		}
		return false;
	}

}
