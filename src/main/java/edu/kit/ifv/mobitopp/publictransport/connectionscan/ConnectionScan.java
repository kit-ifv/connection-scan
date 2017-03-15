package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class ConnectionScan implements RouteSearch {

	private final TransitNetwork transitNetwork;

	public ConnectionScan(TransitNetwork transitNetwork) {
		super();
		this.transitNetwork = transitNetwork;
	}

	@Override
	public Optional<PublicTransportRoute> findRoute(Stop fromStart, Stop toEnd, Time atTime) {
		if (scanNotNeeded(fromStart, toEnd, atTime)) {
			return Optional.empty();
		}
		PreparedSearchRequest searchRequest = newSweeperData(fromStart, toEnd, atTime);
		return sweepOver(searchRequest);
	}

	private boolean scanNotNeeded(Stop start, Stop end, Time time) {
		return connections().areDepartedBefore(time) || notAvailable(start, end);
	}

	private boolean notAvailable(Stop fromStart, Stop toEnd) {
		return !stops().contains(fromStart) || !stops().contains(toEnd);
	}
	
	private PreparedSearchRequest newSweeperData(Stop fromStart, Stop toEnd, Time atTime) {
		return SingleSearchRequest.from(fromStart, toEnd, atTime, arrivalSize());
	}

	@Override
	public Optional<PublicTransportRoute> findRoute(
			StopPaths fromStarts, StopPaths toEnds, Time atTime) {
		if (scanNotNeeded(fromStarts, toEnds, atTime)) {
			return Optional.empty();
		}
		PreparedSearchRequest searchRequest = newSearchRequest(fromStarts, toEnds, atTime);
		return sweepOver(searchRequest);
	}

	private Optional<PublicTransportRoute> sweepOver(PreparedSearchRequest searchRequest) {
		return connections().sweep(searchRequest);
	}

	private boolean scanNotNeeded(StopPaths startStops, StopPaths endStops, Time time) {
		return connections().areDepartedBefore(time) || notAvailable(startStops, endStops);
	}

	private boolean notAvailable(StopPaths startStops, StopPaths endStops) {
		return notAvailable(startStops) || notAvailable(endStops);
	}

	private boolean notAvailable(StopPaths requested) {
		return requested.stops().isEmpty() || !stops().containsAll(requested.stops());
	}

	PreparedSearchRequest newSearchRequest(StopPaths fromStarts, StopPaths toEnds, Time atTime) {
		return MultipleSearchRequest.from(fromStarts, toEnds, atTime, arrivalSize());
	}
	
	private int arrivalSize() {
		return stops().size();
	}
	
	private Collection<Stop> stops() {
		return transitNetwork.stops();
	}
	
	private ConnectionSweeper connections() {
		return transitNetwork.connections();
	}

	@Override
	public String toString() {
		return "ConnectionScan [transitNetwork=" + transitNetwork + "]";
	}

}
