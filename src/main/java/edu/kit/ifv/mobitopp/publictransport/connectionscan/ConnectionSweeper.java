package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface ConnectionSweeper {

	boolean isTooLate(Time time);

	Optional<PublicTransportRoute> sweep(SweeperData data, Stop start, Stop end, Time searchTime);

	Optional<PublicTransportRoute> sweep(
			SweeperData data, StopPaths startStops, StopPaths ends, Time searchTime);

}