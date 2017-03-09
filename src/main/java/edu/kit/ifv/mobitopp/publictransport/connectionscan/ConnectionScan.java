package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class ConnectionScan implements RouteSearch {

	private final Collection<Stop> stops;
	private final ConnectionSweeper connections;

	ConnectionScan(Collection<Stop> stops, ConnectionSweeper connections) {
		super();
		this.stops = stops;
		this.connections = connections;
	}

	public static RouteSearch create(Collection<Stop> stops, Connections connections) {
		assertIdsOf(stops);
		ConnectionSweeper prepareConnections = PreparedConnections.from(connections);
		return new ConnectionScan(stops, prepareConnections);
	}

	private static void assertIdsOf(Collection<Stop> stops) {
		ArrayList<Stop> internalStops = new ArrayList<>(stops);
		internalStops.sort(comparing(Stop::id));
		for (int index = 0; index < internalStops.size(); index++) {
			Stop stop = internalStops.get(index);
			if (index != stop.id()) {
				throw new IllegalArgumentException("Ids of stops must be consecutive starting at 0. Wrong id at stop: " + stop);
			}
		}
	}

	@Override
	public Optional<PublicTransportRoute> findRoute(Stop fromStart, Stop toEnd, Time atTime) {
		if (scanNotNeeded(fromStart, toEnd, atTime)) {
			return Optional.empty();
		}
		Arrival arrival = newArrival(fromStart, atTime);
		return connections.sweep(arrival, fromStart, toEnd, atTime);
	}

	private boolean scanNotNeeded(Stop start, Stop end, Time time) {
		return connections.isTooLate(time) || notAvailable(start, end);
	}

	private boolean notAvailable(Stop fromStart, Stop toEnd) {
		return !stops.contains(fromStart) || !stops.contains(toEnd);
	}
	
	private Arrival newArrival(Stop fromStart, Time atTime) {
		return ScannedArrival.from(fromStart, atTime, arrivalSize());
	}

	@Override
	public Optional<PublicTransportRoute> findRoute(
			StopPaths fromStarts, StopPaths toEnds, Time atTime) {
		if (scanNotNeeded(fromStarts, toEnds, atTime)) {
			return Optional.empty();
		}
		Arrival arrival = newArrival(fromStarts, atTime);
		Optional<PublicTransportRoute> found = connections.sweep(arrival, fromStarts, toEnds, atTime);
		return found.map(tour -> tour.addFootpaths(fromStarts, toEnds));
	}


	private boolean scanNotNeeded(StopPaths startStops, StopPaths endStops, Time time) {
		return connections.isTooLate(time) || notAvailable(startStops, endStops);
	}

	private boolean notAvailable(StopPaths startStops, StopPaths endStops) {
		return notAvailable(startStops) || notAvailable(endStops);
	}

	private boolean notAvailable(StopPaths requested) {
		return requested.stops().isEmpty() || !stops.containsAll(requested.stops());
	}

	Arrival newArrival(StopPaths fromStarts, Time atTime) {
		return ScannedArrival.from(fromStarts.stopPaths(), atTime, arrivalSize());
	}
	
	private int arrivalSize() {
		return stops.size();
	}

	@Override
	public String toString() {
		return "ConnectionScan [stops=" + stops + ", connections=" + connections + "]";
	}

}
