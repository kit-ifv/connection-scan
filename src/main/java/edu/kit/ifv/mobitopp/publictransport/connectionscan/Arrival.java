package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface Arrival {

	boolean isTooLate(Connection connection, Stop end);

	boolean isTooLateAtOne(Connection connection, List<Stop> stops);

	void updateArrival(Connection connection);

	Optional<PublicTransportRoute> createRoute(Stop start, Stop end, Time time);

	Optional<PublicTransportRoute> createRoute(ReachableStops starts, ReachableStops toEnd, Time time);

}