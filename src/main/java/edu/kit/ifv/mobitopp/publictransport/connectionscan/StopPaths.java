package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public interface StopPaths {

	List<Stop> stops();

	StopPath pathTo(Stop stop);

	List<StopPath> stopPaths();
	
	// TODO move
	Optional<Stop> stopWithEarliestArrival(Times times);

	boolean isConnectionReachableAt(Stop stop, Time time, Connection connection);

}
