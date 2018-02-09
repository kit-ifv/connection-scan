package edu.kit.ifv.mobitopp.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.Time;

public interface Journey {

	int id();

	Time day();

	Connections connections();

	int capacity();

	TransportSystem transportSystem();

}