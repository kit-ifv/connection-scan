package edu.kit.ifv.mobitopp.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.SimulationDate;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

public class FootJourney implements Journey {

	public static final Journey footJourney = new FootJourney();
	private static final TransportSystem footSystem = new TransportSystem("foot");
	private static final int unlimited = Integer.MAX_VALUE;
	private static int footId = -1;

	private FootJourney() {
		super();
	}

	@Override
	public int id() {
		return footId;
	}

	@Override
	public SimulationDateIfc day() {
		return SimulationDate.future();
	}

	@Override
	public Connections connections() {
		return new Connections();
	}

	@Override
	public int capacity() {
		return unlimited;
	}

	@Override
	public TransportSystem transportSystem() {
		return footSystem;
	}

}
