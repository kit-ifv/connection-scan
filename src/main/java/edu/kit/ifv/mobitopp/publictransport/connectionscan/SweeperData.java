package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface SweeperData {

	/**
	 * Connection departs after arrival at end {@link Stop}
	 * 
	 * @param connection
	 * @param end
	 * @return
	 */
	boolean isTooLate(Connection connection, Stop end);

	/**
	 * Connection departs after arrival at one of the given end {@link Stop}s
	 * 
	 * @param connection
	 * @param stops
	 * @return
	 */
	boolean isTooLateAtOne(Connection connection, List<Stop> stops);

	void updateArrival(Connection connection);

	Optional<PublicTransportRoute> createRoute(Stop start, Stop end, Time time);

	Optional<PublicTransportRoute> createRoute(StopPaths starts, StopPaths toEnd, Time time);

}