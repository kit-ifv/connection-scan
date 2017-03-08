package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.PathToStop;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class MultipleStarts extends BasicTimes implements Times {

	private final Time departure;
	private final List<PathToStop> paths;

	private MultipleStarts(List<PathToStop> paths, Time departure, int numberOfStops) {
		super(numberOfStops);
		this.paths = paths;
		this.departure = departure;
		initialise();
	}

	static Times from(List<PathToStop> paths, Time departure, int numberOfStops) {
		return new MultipleStarts(paths, departure, numberOfStops);
	}

	@Override
	protected void initialiseStart() {
		for (PathToStop pathToStop : paths) {
			set(pathToStop.stop(), departure.add(pathToStop.duration()));
		}
	}

	@Override
	public void initialise(BiConsumer<Stop, Time> consumer) {
		for (PathToStop pathToStop : paths) {
			consumer.accept(pathToStop.stop(), departure.add(pathToStop.duration()));
		}
	}

	@Override
	protected boolean isStart(Stop stop) {
		for (PathToStop pathToStop : paths) {
			if (pathToStop.stop().equals(stop)) {
				return true;
			}
		}
		return false;
	}

}