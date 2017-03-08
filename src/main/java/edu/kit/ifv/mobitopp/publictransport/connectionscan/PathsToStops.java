package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.PathToStop;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class PathsToStops implements ReachableStops {

	private final List<PathToStop> reachable;
	private final List<Stop> stops;
	private final Map<Stop, PathToStop> lookup;

	private PathsToStops(List<PathToStop> reachable, List<Stop> stops, Map<Stop, PathToStop> lookup) {
		super();
		this.reachable = reachable;
		this.stops = stops;
		this.lookup = lookup;
	}

	public static ReachableStops from(List<PathToStop> reachable) {
		List<Stop> stops = reachable.stream().map(PathToStop::stop).collect(toList());
		Map<Stop, PathToStop> lookup = new HashMap<>();
		for (PathToStop stopDistance : reachable) {
			lookup.put(stopDistance.stop(), stopDistance);
		}
		return new PathsToStops(reachable, stops, lookup);
	}

	@Override
	public Arrival createArrival(Time time, int numberOfStops) {
		return ScannedArrival.from(reachable, time, numberOfStops);
	}

	@Override
	public List<Stop> stops() {
		return stops;
	}

	@Override
	public PathToStop pathTo(Stop stop) {
		return lookup.get(stop);
	}

	@Override
	public Optional<Stop> earliestArrivalAtStop(Times times) {
		if (stops.isEmpty()) {
			return empty();
		}
		return earliestStop(times);
	}

	private Optional<Stop> earliestStop(Times times) {
		Stop stop = null;
		Time currentArrival = null;
		for (PathToStop stopDistance : reachable) {
			Stop current = stopDistance.stop();
			Time currentTime = times.get(current);
			Time includingFootpath = stopDistance.walkAt(currentTime);
			if (null == currentArrival || includingFootpath.isBefore(currentArrival)) {
				stop = current;
				currentArrival = currentTime;
			}
		}
		return ofNullable(stop);
	}

	@Override
	public boolean isStart(Stop stop, Time time, Connection connection) {
		Time departure = connection.departure();
		if (lookup.containsKey(stop)) {
			PathToStop pathToStop = lookup.get(stop);
			Time arrivalAtStop = time.add(pathToStop.duration());
			return arrivalAtStop.isBeforeOrEqualTo(departure);
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
		PathsToStops other = (PathsToStops) obj;
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
