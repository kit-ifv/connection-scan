package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface UsedConnections {

	void update(Stop stop, Connection connection);

	List<Connection> buildUpConnection(Stop fromStart, Stop toEnd) throws StopNotReachable;

	List<Connection> buildUpConnection(StopPaths starts, Stop end, Time time) throws StopNotReachable;

}