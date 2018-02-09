package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

abstract class BaseTimes implements ArrivalTimes {

	private final SimulationDateIfc[] times;

	BaseTimes(int numberOfStops) {
		super();
		times = new SimulationDateIfc[numberOfStops];
	}

	protected void initialise() {
		for (int i = 0; i < times.length; i++) {
			times[i] = SimulationDateIfc.infinite;
		}
		initialiseStart();
	}

	protected abstract void initialiseStart();

	@Override
	public void set(Stop stop, SimulationDateIfc time) {
		times[stop.id()] = time;
	}

	@Override
	public SimulationDateIfc getConsideringMinimumChangeTime(Stop stop) {
		if (isStart(stop)) {
			return get(stop);
		}
		return considerChangeTime(stop);
	}

	protected abstract boolean isStart(Stop stop);

	private SimulationDateIfc considerChangeTime(Stop stop) {
		return stop.addChangeTimeTo(get(stop));
	}

	@Override
	public SimulationDateIfc get(Stop stop) {
		int internal = stop.id();
		if (internal >= times.length || internal < 0) {
			return SimulationDateIfc.infinite;
		}
		return times[internal];
	}

}