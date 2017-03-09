package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class SingleSweeperData extends BaseSweeperData {

	private final Stop start;
	private final Stop end;

	private SingleSweeperData(
			Stop start, Stop end, Times times, UsedConnections usedConnections, UsedJourneys usedJourneys) {
		super(times, usedConnections, usedJourneys);
		this.start = start;
		this.end = end;
	}

	static SweeperData from(Stop start, Stop end, Time atTime, int numberOfStops) {
		Times times = SingleStart.create(start, atTime, numberOfStops);
		UsedConnections usedConnections = new ArrivalConnections(numberOfStops);
		UsedJourneys usedJourneys = new ScannedJourneys();
		BaseSweeperData data = new SingleSweeperData(start, end, times, usedConnections, usedJourneys);
		times.initialise(data::initialise);
		return data;
	}

	@Override
	public boolean isAfterArrivalAtEnd(Connection connection) {
		return isTooLateAt(connection.departure(), end);
	}

	@Override
	protected List<Connection> collectConnections(UsedConnections usedConnections, Time time)
			throws StopNotReachable {
		return usedConnections.collectConnections(start, end);
	}
}