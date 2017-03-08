package edu.kit.ifv.mobitopp.publictransport.model;

public interface Journey {

	int id();

	Time day();

	Connections connections();

	int capacity();

	TransportSystem transportSystem();

}