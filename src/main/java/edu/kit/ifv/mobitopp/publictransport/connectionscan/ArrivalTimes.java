package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

interface ArrivalTimes {

	void initialise(BiConsumer<Stop, SimulationDateIfc> consumer);

	void set(Stop stop, SimulationDateIfc time);

	SimulationDateIfc getConsideringMinimumChangeTime(Stop stop);

	SimulationDateIfc get(Stop stop);

	SimulationDateIfc startTime();

}