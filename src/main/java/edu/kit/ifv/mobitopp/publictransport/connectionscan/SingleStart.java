package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

class SingleStart extends BaseTimes {

	private final Stop start;
	private final SimulationDateIfc startTime;

	private SingleStart(Stop start, SimulationDateIfc startTime, int numberOfStops) {
		super(numberOfStops);
		this.start = start;
		this.startTime = startTime;
		initialise();
	}

	static ArrivalTimes create(Stop start, SimulationDateIfc departure, int numberOfStops) {
		return new SingleStart(start, departure, numberOfStops);
	}
	
	@Override
	public SimulationDateIfc startTime() {
		return startTime;
	}

	@Override
	protected void initialiseStart() {
		set(start, startTime);
	}

	@Override
	public void initialise(BiConsumer<Stop, SimulationDateIfc> consumer) {
		consumer.accept(start, startTime);
	}

	@Override
	protected boolean isStart(Stop stop) {
		return start.equals(stop);
	}

}