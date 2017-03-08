package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class MultipleStarts extends BasicTimes implements Times {

	private final Time departure;
	private final List<StopPath> paths;

	private MultipleStarts(List<StopPath> paths, Time departure, int numberOfStops) {
		super(numberOfStops);
		this.paths = paths;
		this.departure = departure;
		initialise();
	}

	static Times from(List<StopPath> paths, Time departure, int numberOfStops) {
		return new MultipleStarts(paths, departure, numberOfStops);
	}

	@Override
	protected void initialiseStart() {
		for (StopPath pathToStop : paths) {
			set(pathToStop.stop(), departure.add(pathToStop.duration()));
		}
	}

	@Override
	public void initialise(BiConsumer<Stop, Time> consumer) {
		for (StopPath pathToStop : paths) {
			consumer.accept(pathToStop.stop(), departure.add(pathToStop.duration()));
		}
	}

	@Override
	protected boolean isStart(Stop stop) {
		for (StopPath pathToStop : paths) {
			if (pathToStop.stop().equals(stop)) {
				return true;
			}
		}
		return false;
	}

}