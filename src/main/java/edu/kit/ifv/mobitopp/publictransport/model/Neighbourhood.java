package edu.kit.ifv.mobitopp.publictransport.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class Neighbourhood implements Iterable<Stop> {

	private final Map<Stop, RelativeTime> neighbours;

	public Neighbourhood() {
		super();
		neighbours = new HashMap<>();
	}

	void add(Stop stop, RelativeTime duration) {
		neighbours.put(stop, duration);
	}

	public Optional<RelativeTime> walkTimeTo(Stop stop) {
		if (neighbours.containsKey(stop)) {
			return Optional.of(neighbours.get(stop));
		}
		return Optional.empty();
	}

	@Override
	public Iterator<Stop> iterator() {
		return neighbours.keySet().iterator();
	}

	public boolean isEmpty() {
		return neighbours.isEmpty();
	}

	@Override
	public String toString() {
		return "Neighbourhood [neighbours=" + neighbours + "]";
	}

	public int size() {
		return neighbours.size();
	}

	public boolean contains(Stop stop) {
		return neighbours.containsKey(stop);
	}

}
