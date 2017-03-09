package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface ConnectionSweeper {

	boolean allAreDepartedAt(Time time);

	Optional<PublicTransportRoute> sweep(SweeperData data);
	
}