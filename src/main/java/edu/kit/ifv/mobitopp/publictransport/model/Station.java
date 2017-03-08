package edu.kit.ifv.mobitopp.publictransport.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.network.Node;

public interface Station {

	void add(Stop newStop);

	int id();

	void forEach(Consumer<Stop> action);

	<T> List<T> toEachOf(Station end, BiFunction<Stop, Stop, Optional<T>> function);

	void forEachNode(BiConsumer<Node, Station> consumer);

	RelativeTime minimumChangeTime(int id);

	Collection<Stop> stops();

	List<Node> nodes();

}