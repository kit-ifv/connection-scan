package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class TransitNetwork {

	private final Collection<Stop> stops;
	private final Connections connections;

	private TransitNetwork(Collection<Stop> stops, Connections connections) {
		super();
		this.stops = stops;
		this.connections = connections;
	}

	public static TransitNetwork createOf(Collection<Stop> stops, Connections connections) {
		assertIdsOf(stops);
		return new TransitNetwork(stops, connections);
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

	ConnectionSweeper connections() {
		return DefaultConnectionSweeper.from(connections);
	}

	Collection<Stop> stops() {
		return stops;
	}

}
