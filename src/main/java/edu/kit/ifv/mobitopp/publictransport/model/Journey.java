package edu.kit.ifv.mobitopp.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

public interface Journey {

	int id();

	SimulationDateIfc day();

	Connections connections();

	int capacity();

	TransportSystem transportSystem();

}