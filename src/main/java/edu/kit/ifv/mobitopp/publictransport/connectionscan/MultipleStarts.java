package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class MultipleStarts extends BasicTimes implements Times {

	private final Time departure;
	private final List<StopPath> startPaths;
	private final List<StopPath> endPaths;

	private MultipleStarts(List<StopPath> startPaths, List<StopPath> endPaths, Time departure, int numberOfStops) {
		super(numberOfStops);
		this.startPaths = startPaths;
		this.endPaths = endPaths;
		this.departure = departure;
		initialise();
	}

	static Times from(StopPaths fromStarts, StopPaths toEnds, Time departure, int numberOfStops) {
		return new MultipleStarts(fromStarts.stopPaths(), toEnds.stopPaths(), departure, numberOfStops);
	}

	@Override
	protected void initialiseStart() {
		for (StopPath pathToStop : startPaths) {
			set(pathToStop.stop(), departure.add(pathToStop.duration()));
		}
	}

	@Override
	public void initialise(BiConsumer<Stop, Time> consumer) {
		for (StopPath pathToStop : startPaths) {
			consumer.accept(pathToStop.stop(), departure.add(pathToStop.duration()));
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

	@Override
	public Optional<Stop> stopWithEarliestArrival() {
		Stop stop = null;
		Time currentArrival = null;
		for (StopPath path : endPaths) {
			Stop current = path.stop();
			Time currentTime = get(current);
			Time includingFootpath = path.arrivalTimeStartingAt(currentTime);
			if (null == currentArrival || includingFootpath.isBefore(currentArrival)) {
				stop = current;
				currentArrival = currentTime;
			}
		}
		return ofNullable(stop);
	}

}