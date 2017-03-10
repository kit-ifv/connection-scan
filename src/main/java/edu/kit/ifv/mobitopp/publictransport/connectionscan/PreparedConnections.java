package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class PreparedConnections implements ConnectionSweeper {

	private static final int defaultDistance = 100;
	private static final Function<Time, Boolean> alwaysTooLate = (time) -> true;
	private final List<Connection> connections;
	private final Function<Time, Integer> lookup;
	private final Function<Time, Boolean> isTooLate;
	private final int distanceBetweenConnectionsToCheck;

	private PreparedConnections(
			List<Connection> connections, Function<Time, Integer> lookup,
			Function<Time, Boolean> isTooLate, int distanceToCancel) {
		super();
		this.connections = connections;
		this.lookup = lookup;
		this.isTooLate = isTooLate;
		distanceBetweenConnectionsToCheck = distanceToCancel;
	}

	static PreparedConnections from(Connections connections) {
		return from(connections, defaultDistance);
	}

	static PreparedConnections from(Connections connections, int distanceToCancel) {
		Stream<Connection> stream = connections.asCollection().stream();
		List<Connection> sorted = stream.sorted(new ConnectionComparator()).collect(toList());
		List<Connection> fixedConnections = Collections.unmodifiableList(sorted);
		Function<Time, Integer> lookup = initialiseLookup(fixedConnections);
		Function<Time, Boolean> isTooLate = createIsTooLate(fixedConnections);
		return new PreparedConnections(fixedConnections, lookup, isTooLate, distanceToCancel);
	}

	private static Function<Time, Integer> initialiseLookup(List<Connection> connections) {
		TreeMap<Time, Integer> lookup = new TreeMap<>();
		for (int index = 0; index < connections.size(); index++) {
			Time departure = connections.get(index).departure();
			lookup.putIfAbsent(departure, index);
		}
		return (time) -> indexIn(lookup, time, connections.size());
	}

	private static Integer indexIn(TreeMap<Time, Integer> lookup, Time time, int noEntryFound) {
		Entry<Time, Integer> possibleEntry = lookup.ceilingEntry(time);
		if (possibleEntry == null) {
			return noEntryFound;
		}
		return possibleEntry.getValue();
	}

	private static Function<Time, Boolean> createIsTooLate(List<Connection> connections) {
		if (connections.isEmpty()) {
			return alwaysTooLate;
		}
		Connection latestConnection = connections.get(connections.size() - 1);
		return latestConnection::departsBefore;
	}

	public List<Connection> asList() {
		return Collections.unmodifiableList(connections);
	}

	@Override
	public boolean areDepartedBefore(Time time) {
		return isTooLate.apply(time);
	}

	@Override
	public Optional<PublicTransportRoute> sweep(SweeperData data) {
		Time atTime = data.startTime();
		int fromStartIndex = lookup.apply(atTime);
		scanConnections(fromStartIndex, data);
		return data.createRoute();
	}

	private void scanConnections(int startIndex, SweeperData data) {
		for (int index = startIndex; index < connections.size(); index++) {
			Connection connection = connections.get(index);
			if (shouldCheck(index) && data.isAfterArrivalAtEnd(connection)) {
				break;
			}
			data.updateArrival(connection);
		}
	}

	/**
	 * Reduces overhead when stopping connection sweep.
	 * 
	 * @param index
	 * @return
	 */
	private boolean shouldCheck(int index) {
		return 0 == index % distanceBetweenConnectionsToCheck;
	}

	@Override
	public String toString() {
		return "PreparedConnections [connections=" + connections
				+ ", distanceBetweenConnectionsToCheck=" + distanceBetweenConnectionsToCheck + "]";
	}

}
