package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface SweeperData {

	Time startTime();
	
	boolean isAfterArrivalAtEnd(Connection connection);

	void updateArrival(Connection connection);

	Optional<PublicTransportRoute> createRoute();

}