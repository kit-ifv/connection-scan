package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.simulation.SimulationDateIfc.infinite;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.BiConsumer;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

public class SingleStartTest {

	private static final RelativeTime changeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime defaultChangeTime = RelativeTime.of(0, MINUTES);
	private static final int onlyStartStop = 1;

	@Test
	public void returnsInfiniteWhenTimeHasNotBeenSet() throws Exception {
		ArrivalTimes times = times(onlyStartStop);

		SimulationDateIfc time = times.getConsideringMinimumChangeTime(anotherStop());

		assertThat(time, is(equalTo(SimulationDateIfc.infinite)));
	}

	@Test
	public void returnsSetTimeAfterTimeHasBeenSet() throws Exception {
		ArrivalTimes times = times(onlyStartStop);

		Stop stop = someStop();
		SimulationDateIfc timeToSet = someTime();
		times.set(stop, timeToSet);
		SimulationDateIfc time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(timeToSet)));
	}

	@Test
	public void returnsSetTimeWhenTimesContainsSeveralTimes() throws Exception {
		Stop stop0 = someStop();
		Stop stop1 = anotherStop();
		SimulationDateIfc timeToSetForStop0 = someTime();
		SimulationDateIfc timeToSetForStop1 = oneMinuteLater();
		int numberOfStops = 2;

		ArrivalTimes times = times(numberOfStops);
		times.set(stop0, timeToSetForStop0);
		times.set(stop1, timeToSetForStop1);
		SimulationDateIfc timeForStop0 = times.getConsideringMinimumChangeTime(stop0);
		SimulationDateIfc timeForStop1 = times.getConsideringMinimumChangeTime(stop1);

		assertThat(timeForStop0, is(equalTo(timeToSetForStop0)));
		assertThat(timeForStop1, is(equalTo(timeToSetForStop1)));
	}

	@Test
	public void returnsInfiniteForStopsExceptStartStop() throws Exception {
		Stop stop = anotherStop();

		ArrivalTimes times = times(someStop(), someTime(), onlyStartStop);
		SimulationDateIfc time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(infinite)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooHigh() throws Exception {
		int tooHighIndex = 1;
		Stop stop = stop().withId(tooHighIndex).build();

		ArrivalTimes times = times(onlyStartStop);
		SimulationDateIfc time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(infinite)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooLow() throws Exception {
		int tooLowIndex = -1;
		Stop stop = stop().withId(tooLowIndex).build();

		ArrivalTimes times = times(onlyStartStop);
		SimulationDateIfc time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(infinite)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeAtStartStop() throws Exception {
		Stop start = someStop(changeTime);
		SimulationDateIfc timeAtStart = oneMinuteLater();
		ArrivalTimes times = times(start, timeAtStart, 2);
		times.set(start, timeAtStart);

		assertThat(times.getConsideringMinimumChangeTime(start), is(equalTo(timeAtStart)));
	}

	@Test
	public void considersMinimumChangeTimeAtGivenStopOtherThanStart() throws Exception {
		Stop start = someStop(changeTime);
		Stop otherStop = anotherStop(changeTime);
		SimulationDateIfc timeAtStart = someTime();
		SimulationDateIfc timeAtOther = oneMinuteLater();
		ArrivalTimes times = times(start, timeAtStart, 2);
		times.set(start, timeAtStart);
		times.set(otherStop, timeAtOther);

		SimulationDateIfc timeAtOtherIncludingChangeTime = oneMinuteLater().plus(changeTime);
		assertThat(times.getConsideringMinimumChangeTime(otherStop),
				is(equalTo(timeAtOtherIncludingChangeTime)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGetAtStartStop() throws Exception {
		Stop start = someStop(changeTime);
		SimulationDateIfc timeAtStart = someTime();
		ArrivalTimes times = times(start, timeAtStart, 2);
		times.set(start, timeAtStart);

		assertThat(times.get(start), is(equalTo(timeAtStart)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGet() throws Exception {
		Stop start = someStop(changeTime);
		Stop otherStop = anotherStop(changeTime);
		SimulationDateIfc timeAtStart = someTime();
		SimulationDateIfc timeAtOther = oneMinuteLater();
		ArrivalTimes times = times(start, timeAtStart, 2);
		times.set(start, timeAtStart);
		times.set(otherStop, timeAtOther);

		assertThat(times.get(otherStop), is(equalTo(timeAtOther)));
	}

	@Test
	public void initialisesTimeAtStart() throws Exception {
		Stop start = someStop();
		ArrivalTimes times = times(start, someTime(), onlyStartStop);

		SimulationDateIfc time = times.get(someStop());

		assertThat(time, is(someTime()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void initialisesOtherStops() throws Exception {
		BiConsumer<Stop, SimulationDateIfc> consumer = mock(BiConsumer.class);
		ArrivalTimes times = times(someStop(), someTime(), onlyStartStop);

		times.initialise(consumer);

		verify(consumer).accept(someStop(), someTime());
	}

	private Stop someStop() {
		return someStop(defaultChangeTime);
	}
	
	private Stop someStop(RelativeTime minimumChangeTime) {
		return stop().withId(0).minimumChangeTime(minimumChangeTime).build();
	}

	private Stop anotherStop() {
		return anotherStop(defaultChangeTime);
	}
	
	private Stop anotherStop(RelativeTime minimumChangeTime) {
		return stop().withId(1).minimumChangeTime(minimumChangeTime).build();
	}

	private ArrivalTimes times(int numberOfStops) {
		Stop stop = stop().minimumChangeTime(defaultChangeTime).build();
		return times(stop, someTime(), numberOfStops);
	}

	private ArrivalTimes times(Stop start, SimulationDateIfc departure, int numberOfStops) {
		return SingleStart.create(start, departure, numberOfStops);
	}
}
