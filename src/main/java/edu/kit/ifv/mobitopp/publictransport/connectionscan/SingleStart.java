package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class SingleStart extends BasicTimes implements Times {

	private final Stop start;
	private final Time departure;
	private final Stop end;

	private SingleStart(Stop start, Stop end, Time departure, int numberOfStops) {
		super(numberOfStops);
		this.start = start;
		this.end = end;
		this.departure = departure;
		initialise();
	}

	static Times from(Stop start, Stop end, Time departure, int numberOfStops) {
		return new SingleStart(start, end, departure, numberOfStops);
	}

	@Override
	protected void initialiseStart() {
		set(start, departure);
	}

	@Override
	public void initialise(BiConsumer<Stop, Time> consumer) {
		consumer.accept(start, departure);
	}

	@Override
	protected boolean isStart(Stop stop) {
		return start.equals(stop);
	}

	@Override
	public Optional<Stop> stopWithEarliestArrival() {
		return Optional.of(end);
	}

}