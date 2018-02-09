package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

public interface RouteSearch {

	Optional<PublicTransportRoute> findRoute(Stop fromStart, Stop toEnd, SimulationDateIfc time);

	Optional<PublicTransportRoute> findRoute(StopPaths startStops, StopPaths endStops, SimulationDateIfc time);

}