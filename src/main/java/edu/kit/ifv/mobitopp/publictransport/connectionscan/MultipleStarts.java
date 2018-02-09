package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

class MultipleStarts extends BaseTimes {

	private final SimulationDateIfc startTime;
	private final List<StopPath> startPaths;

	private MultipleStarts(List<StopPath> startPaths, SimulationDateIfc startTime, int numberOfStops) {
		super(numberOfStops);
		this.startPaths = startPaths;
		this.startTime = startTime;
		initialise();
	}

	static ArrivalTimes create(StopPaths fromStarts, SimulationDateIfc startTime, int numberOfStops) {
		return new MultipleStarts(fromStarts.stopPaths(), startTime, numberOfStops);
	}

	@Override
	public SimulationDateIfc startTime() {
		return startTime;
	}

	@Override
	protected void initialiseStart() {
		for (StopPath pathToStop : startPaths) {
			set(pathToStop.stop(), startTime.plus(pathToStop.duration()));
		}
	}

	@Override
	public void initialise(BiConsumer<Stop, SimulationDateIfc> consumer) {
		for (StopPath pathToStop : startPaths) {
			consumer.accept(pathToStop.stop(), startTime.plus(pathToStop.duration()));
		}
	}

	@Override
	protected boolean isStart(Stop stop) {
		for (StopPath pathToStop : startPaths) {
			if (pathToStop.stop().equals(stop)) {
				return true;
			}
		}
		return false;
	}

}