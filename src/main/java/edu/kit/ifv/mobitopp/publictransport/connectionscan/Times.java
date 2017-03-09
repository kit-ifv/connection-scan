package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

interface Times {

	void initialise(BiConsumer<Stop, Time> consumer);

	void set(Stop stop, Time time);

	Time getConsideringMinimumChangeTime(Stop stop);

	Time get(Stop stop);

	Optional<Stop> stopWithEarliestArrival();

	Time startTime();

	boolean isAfterArrivalAtEnd(Time departure);

}