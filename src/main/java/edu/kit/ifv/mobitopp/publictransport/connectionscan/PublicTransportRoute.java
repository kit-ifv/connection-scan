package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public interface PublicTransportRoute {

	Time arrival();
	
	RelativeTime duration();

	PublicTransportRoute addFootpaths(StopPaths reachableStart, StopPaths reachableEnd);

	List<Connection> connections();

}