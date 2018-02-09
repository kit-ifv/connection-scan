package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.Time;

public interface PublicTransportRoute {

	Stop start();
	
	Stop end();
	
	Time arrival();
	
	RelativeTime duration();

	List<Connection> connections();

}