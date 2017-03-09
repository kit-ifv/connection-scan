package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class MultipleStarts extends BasicTimes implements Times {

	private final Time startTime;
	private final List<StopPath> startPaths;
	private final StopPaths toEnds;

	private MultipleStarts(List<StopPath> startPaths, StopPaths toEnds, Time startTime, int numberOfStops) {
		super(numberOfStops);
		this.startPaths = startPaths;
		this.toEnds = toEnds;
		this.startTime = startTime;
		initialise();
	}

	static Times from(StopPaths fromStarts, StopPaths toEnds, Time startTime, int numberOfStops) {
		return new MultipleStarts(fromStarts.stopPaths(), toEnds, startTime, numberOfStops);
	}

	@Override
	public Time startTime() {
		return startTime;
	}

	@Override
	protected void initialiseStart() {
		for (StopPath pathToStop : startPaths) {
			set(pathToStop.stop(), startTime.add(pathToStop.duration()));
		}
	}

	@Override
	public void initialise(BiConsumer<Stop, Time> consumer) {
		for (StopPath pathToStop : startPaths) {
			consumer.accept(pathToStop.stop(), startTime.add(pathToStop.duration()));
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
		for (StopPath path : toEnds.stopPaths()) {
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
	
	@Override
	public boolean isAfterArrivalAtEnd(Time departure) {
		for (Stop stop : toEnds.stops()) {
			if (isTooLateAt(departure, stop)) {
				return true;
			}
		}
		return false;
	}

}