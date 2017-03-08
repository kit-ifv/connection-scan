package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface ConnectionSweeper {

	boolean isTooLate(Time time);

	Optional<PublicTransportRoute> sweep(Arrival arrival, Stop start, Stop end, Time searchTime);

	Optional<PublicTransportRoute> sweep(
			Arrival arrival, StopPaths startStops, StopPaths ends, Time searchTime);

}