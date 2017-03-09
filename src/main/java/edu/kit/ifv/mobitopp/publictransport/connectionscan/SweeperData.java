package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface SweeperData {

	Time atTime();
	
	/**
	 * Connection departs after arrival at end {@link Stop}
	 * 
	 * @param connection
	 * @return
	 */
	boolean isTooLate(Connection connection);

	void updateArrival(Connection connection);

	Optional<PublicTransportRoute> createRoute();

}