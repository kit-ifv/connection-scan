package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public interface RouteSearch {

	Optional<PublicTransportRoute> findRoute(Stop fromStart, Stop toEnd, Time time);

	Optional<PublicTransportRoute> findRoute(ReachableStops startStops, ReachableStops endStops, Time time);

}