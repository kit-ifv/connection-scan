package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

interface ConnectionSweeper {

	boolean areDepartedBefore(SimulationDateIfc time);

	Optional<PublicTransportRoute> sweep(PreparedSearchRequest searchRequest);
	
}