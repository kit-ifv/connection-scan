package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class MultipleSweeperData extends BaseSweeperData {

	private final StopPaths fromStarts;
	private final StopPaths toEnds;
	
	private MultipleSweeperData(
			StopPaths starts, StopPaths toEnds, Times times, UsedConnections usedConnections, UsedJourneys usedJourneys) {
		super(times, usedConnections, usedJourneys);
		this.fromStarts = starts;
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
		BaseSweeperData data = new MultipleSweeperData(fromStarts, toEnds, times, usedConnections,
				usedJourneys);
		times.initialise(data::initialise);
		return data;
	}

	@Override
	public Optional<PublicTransportRoute> createRoute() {
		Optional<PublicTransportRoute> found = super.createRoute();
		return found.map(tour -> tour.addFootpaths(fromStarts, toEnds));
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

	@Override
	protected List<Connection> collectConnections(UsedConnections usedConnections, Time time)
			throws StopNotReachable {
		Optional<Stop> toEnd = stopWithEarliestArrival();
		if (toEnd.isPresent()) {
			return usedConnections.buildUpConnection(fromStarts, toEnd.get(), time);
		}
		return emptyList();
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
