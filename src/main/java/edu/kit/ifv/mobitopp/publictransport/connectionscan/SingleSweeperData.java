package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.empty;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class SingleSweeperData extends BaseSweeperData implements SweeperData {

	private final Stop start;

	private SingleSweeperData(
			Stop start, Times times, UsedConnections usedConnections, UsedJourneys usedJourneys) {
		super(times, usedConnections, usedJourneys);
		this.start = start;
	}

	static SweeperData from(Stop start, Stop end, Time atTime, int numberOfStops) {
		Times times = SingleStart.from(start, end, atTime, numberOfStops);
		UsedConnections usedConnections = new ArrivalConnections(numberOfStops);
		return from(start, times, usedConnections);
	}

	static SweeperData from(Stop start, Times times, UsedConnections usedConnections) {
		UsedJourneys usedJourneys = new ScannedJourneys();
		BaseSweeperData data = new SingleSweeperData(start, times, usedConnections, usedJourneys);
		times.initialise(data::initialise);
		return data;
	}

	@Override
	public Optional<PublicTransportRoute> createRoute() {
		Optional<Stop> toEnd = times().stopWithEarliestArrival();
		return toEnd.flatMap(end -> createRoute(start, end, times().startTime()));
	}

	private Optional<PublicTransportRoute> createRoute(Stop start, Stop end, Time time) {
		try {
			List<Connection> connections = usedConnections().buildUpConnection(start, end);
			return createRoute(start, end, time, connections);
		} catch (StopNotReachable e) {
			return empty();
		}
	}
}