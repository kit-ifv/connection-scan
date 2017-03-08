package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class DefaultStopPaths implements StopPaths {

	private final List<StopPath> reachable;
	private final List<Stop> stops;
	private final Map<Stop, StopPath> stopToPaths;

	private DefaultStopPaths(List<StopPath> reachable, List<Stop> stops, Map<Stop, StopPath> lookup) {
		super();
		this.reachable = reachable;
		this.stops = stops;
		this.stopToPaths = lookup;
	}

	public static StopPaths from(List<StopPath> reachable) {
		List<Stop> stops = reachable.stream().map(StopPath::stop).collect(toList());
		Map<Stop, StopPath> lookup = new HashMap<>();
		for (StopPath stopDistance : reachable) {
			lookup.put(stopDistance.stop(), stopDistance);
		}
		return new DefaultStopPaths(reachable, stops, lookup);
	}

	@Override
	public Arrival createArrival(Time time, int totalNumberOfStopsInNetwork) {
		return ScannedArrival.from(reachable, time, totalNumberOfStopsInNetwork);
	}

	@Override
	public List<Stop> stops() {
		return stops;
	}

	@Override
	public StopPath pathTo(Stop stop) {
		if (stopToPaths.containsKey(stop)) {
			return stopToPaths.get(stop);
		}
		throw new IllegalArgumentException("Stop is not known: " + stop);
	}

	@Override
	public Optional<Stop> stopWithEarliestArrival(Times times) {
		if (stops.isEmpty()) {
			return empty();
		}
		return earliestStop(times);
	}

	private Optional<Stop> earliestStop(Times times) {
		Stop stop = null;
		Time currentArrival = null;
		for (StopPath stopDistance : reachable) {
			Stop current = stopDistance.stop();
			Time currentTime = times.get(current);
			Time includingFootpath = stopDistance.arrivalTimeStartingAt(currentTime);
			if (null == currentArrival || includingFootpath.isBefore(currentArrival)) {
				stop = current;
				currentArrival = currentTime;
			}
		}
		return ofNullable(stop);
	}

	@Override
	public boolean isConnectionReachableAt(Stop stop, Time time, Connection connection) {
		if (stopToPaths.containsKey(stop)) {
			StopPath pathToStop = stopToPaths.get(stop);
			Time arrivalAtStop = time.add(pathToStop.duration());
			return arrivalAtStop.isBeforeOrEqualTo(connection.departure());
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reachable == null) ? 0 : reachable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DefaultStopPaths other = (DefaultStopPaths) obj;
		if (reachable == null) {
			if (other.reachable != null) {
				return false;
			}
		} else if (!reachable.equals(other.reachable)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Stops [reachable=" + reachable + "]";
	}

}
