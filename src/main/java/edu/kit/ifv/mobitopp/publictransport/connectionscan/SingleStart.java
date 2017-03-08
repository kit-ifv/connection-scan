package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

class SingleStart extends BasicTimes implements Times {

	private final Stop start;
	private final Time departure;

	private SingleStart(Stop start, Time departure, int numberOfStops) {
		super(numberOfStops);
		this.start = start;
		this.departure = departure;
		initialise();
	}

	static Times from(Stop start, Time departure, int numberOfStops) {
		return new SingleStart(start, departure, numberOfStops);
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

}