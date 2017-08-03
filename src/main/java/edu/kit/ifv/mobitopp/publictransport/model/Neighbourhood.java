package edu.kit.ifv.mobitopp.publictransport.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class Neighbourhood implements Iterable<Stop> {

	private final Stop self;
	private final Map<Stop, RelativeTime> neighbours;

	public Neighbourhood(Stop self) {
		super();
		this.self = self;
		neighbours = new HashMap<>();
	}

	void add(Stop neighbour, RelativeTime duration) {
		RelativeTime symmetricWalkTime = calculateSymmetricWalkTime(neighbour, duration);
		neighbours.put(neighbour, symmetricWalkTime);
		neighbour.neighbours().neighbours.put(self, symmetricWalkTime);
	}

	private RelativeTime calculateSymmetricWalkTime(Stop neighbour, RelativeTime duration) {
		Neighbourhood otherNeighbourhood = neighbour.neighbours();
		Optional<RelativeTime> walkTimeToSelf = otherNeighbourhood.walkTimeTo(self);
		if (walkTimeToSelf.isPresent()) {
			if (!walkTimeToSelf.get().equals(duration)) {
				logAsymmetric(neighbour);
				return minimumOf(walkTimeToSelf.get(), duration);
			}
		}
		return duration;
	}

	private void logAsymmetric(Stop neighbour) {
		System.out.println("Asymmetric walk time detected. From " + self + " to " + neighbour);
		System.out.println("Using symmetric walk time.");
	}

	private static RelativeTime minimumOf(RelativeTime first, RelativeTime second) {
		return first.compareTo(second) < 0 ? first : second;
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
