package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.PathToStop;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public interface ReachableStops {

	Arrival createArrival(Time time, int numberOfStops);

	List<Stop> stops();

	PathToStop pathTo(Stop stop);

	Optional<Stop> earliestArrivalAtStop(Times times);

	boolean isStart(Stop stop, Time time, Connection connection);

}
