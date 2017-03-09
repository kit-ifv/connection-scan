package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class SingleStart extends BasicTimes {

	private final Stop start;
	private final Time startTime;
	private final Stop end;

	private SingleStart(Stop start, Stop end, Time startTime, int numberOfStops) {
		super(numberOfStops);
		this.start = start;
		this.end = end;
		this.startTime = startTime;
		initialise();
	}

	static Times from(Stop start, Stop end, Time departure, int numberOfStops) {
		return new SingleStart(start, end, departure, numberOfStops);
	}
	
	@Override
	public Time startTime() {
		return startTime;
	}

	@Override
	protected void initialiseStart() {
		set(start, startTime);
	}

	@Override
	public void initialise(BiConsumer<Stop, Time> consumer) {
		consumer.accept(start, startTime);
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