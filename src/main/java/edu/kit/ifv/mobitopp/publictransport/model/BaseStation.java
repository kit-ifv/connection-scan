package edu.kit.ifv.mobitopp.publictransport.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.network.Node;

public abstract class BaseStation implements Station {

	private final int id;
	private final Set<Stop> stops;
	private final Set<Node> nodes;

	public BaseStation(int id) {
		super();
		this.id = id;
		stops = new HashSet<>();
		nodes = new HashSet<>();
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public List<Node> nodes() {
		return new ArrayList<>(nodes);
	}

	public void locateAt(Collection<Node> locations) {
		this.nodes.addAll(locations);
	}

	@Override
	public void forEachNode(BiConsumer<Node, Station> consumer) {
		for (Node node : nodes) {
			consumer.accept(node, this);
		}
	}
	
	@Override
	public void add(Stop newStop) {
		stops.add(newStop);
	}

	@Override
	public Collection<Stop> stops() {
		return Collections.unmodifiableCollection(stops);
	}

	@Override
	public void forEach(Consumer<Stop> action) {
		stops.forEach(action);
	}

	@Override
	public <T> List<T> toEachOf(Station end, BiFunction<Stop, Stop, Optional<T>> function) {
		List<T> results = new ArrayList<>();
		for (Stop origin : stops) {
			for (Stop destination : end.stops()) {
				function.apply(origin, destination).ifPresent(results::add);
			}
		}
		return results;
	}

}