package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class DefaultStopPaths implements StopPaths {

	private final List<StopPath> stopPaths;
	private final List<Stop> stops;
	private final Map<Stop, StopPath> stopToPath;

	private DefaultStopPaths(List<StopPath> reachable, List<Stop> stops, Map<Stop, StopPath> stopToPath) {
		super();
		this.stopPaths = reachable;
		this.stops = stops;
		this.stopToPath = stopToPath;
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
	public List<Stop> stops() {
		return stops;
	}
	
	@Override
	public List<StopPath> stopPaths() {
		return Collections.unmodifiableList(stopPaths);
	}

	@Override
	public StopPath pathTo(Stop stop) {
		if (stopToPath.containsKey(stop)) {
			return stopToPath.get(stop);
		}
		throw new IllegalArgumentException("Stop is not known: " + stop);
	}

	@Override
	public boolean isConnectionReachableAt(Stop stop, Time time, Connection connection) {
		if (stopToPath.containsKey(stop)) {
			StopPath pathToStop = stopToPath.get(stop);
			Time arrivalAtStop = time.add(pathToStop.duration());
			return arrivalAtStop.isBeforeOrEqualTo(connection.departure());
		}
		return false;
	}

	@Override
	public String toString() {
		return "DefaultStopPaths [stopPaths=" + stopPaths + "]";
	}

}
