package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.publictransport.model.Time.infinite;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class SingleStartTest {

	private static final RelativeTime changeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime defaultChangeTime = RelativeTime.of(0, MINUTES);
	private static final int onlyStartStop = 1;
	private static final int startAndEnd = 2;

	@Test
	public void returnsInfiniteWhenTimeHasNotBeenSet() throws Exception {
		Times times = times(onlyStartStop);

		Time time = times.getConsideringMinimumChangeTime(anotherStop());

		assertThat(time, is(equalTo(Time.infinite)));
	}

	@Test
	public void returnsSetTimeAfterTimeHasBeenSet() throws Exception {
		Times times = times(onlyStartStop);

		Stop stop = someStop();
		Time timeToSet = someTime();
		times.set(stop, timeToSet);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(timeToSet)));
	}

	@Test
	public void returnsSetTimeWhenTimesContainsSeveralTimes() throws Exception {
		Stop stop0 = someStop();
		Stop stop1 = anotherStop();
		Time timeToSetForStop0 = someTime();
		Time timeToSetForStop1 = oneMinuteLater();
		int numberOfStops = 2;

		Times times = times(numberOfStops);
		times.set(stop0, timeToSetForStop0);
		times.set(stop1, timeToSetForStop1);
		Time timeForStop0 = times.getConsideringMinimumChangeTime(stop0);
		Time timeForStop1 = times.getConsideringMinimumChangeTime(stop1);

		assertThat(timeForStop0, is(equalTo(timeToSetForStop0)));
		assertThat(timeForStop1, is(equalTo(timeToSetForStop1)));
	}

	@Test
	public void returnsInfiniteForStopsExceptStartStop() throws Exception {
		Stop stop = anotherStop();

		Times times = times(someStop(), someTime(), onlyStartStop);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(infinite)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooHigh() throws Exception {
		int tooHighIndex = 1;
		Stop stop = stop().withId(tooHighIndex).build();

		Times times = times(onlyStartStop);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(Time.infinite)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooLow() throws Exception {
		int tooLowIndex = -1;
		Stop stop = stop().withId(tooLowIndex).build();

		Times times = times(onlyStartStop);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(Time.infinite)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeAtStartStop() throws Exception {
		Stop start = someStop(changeTime);
		Time timeAtStart = oneMinuteLater();
		Times times = times(start, timeAtStart, 2);
		times.set(start, timeAtStart);

		assertThat(times.getConsideringMinimumChangeTime(start), is(equalTo(timeAtStart)));
	}

	@Test
	public void considersMinimumChangeTimeAtGivenStopOtherThanStart() throws Exception {
		Stop start = someStop(changeTime);
		Stop otherStop = anotherStop(changeTime);
		Time timeAtStart = someTime();
		Time timeAtOther = oneMinuteLater();
		Times times = times(start, timeAtStart, 2);
		times.set(start, timeAtStart);
		times.set(otherStop, timeAtOther);

		Time timeAtOtherIncludingChangeTime = oneMinuteLater().add(changeTime);
		assertThat(times.getConsideringMinimumChangeTime(otherStop),
				is(equalTo(timeAtOtherIncludingChangeTime)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGetAtStartStop() throws Exception {
		Stop start = someStop(changeTime);
		Time timeAtStart = someTime();
		Times times = times(start, timeAtStart, 2);
		times.set(start, timeAtStart);

		assertThat(times.get(start), is(equalTo(timeAtStart)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGet() throws Exception {
		Stop start = someStop(changeTime);
		Stop otherStop = anotherStop(changeTime);
		Time timeAtStart = someTime();
		Time timeAtOther = oneMinuteLater();
		Times times = times(start, timeAtStart, 2);
		times.set(start, timeAtStart);
		times.set(otherStop, timeAtOther);

		assertThat(times.get(otherStop), is(equalTo(timeAtOther)));
	}

	@Test
	public void initialisesTimeAtStart() throws Exception {
		Stop start = someStop();
		Times times = times(start, someTime(), onlyStartStop);

		Time time = times.get(someStop());

		assertThat(time, is(someTime()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void initialisesOtherStops() throws Exception {
		BiConsumer<Stop, Time> consumer = mock(BiConsumer.class);
		Times times = times(someStop(), someTime(), onlyStartStop);

		times.initialise(consumer);

		verify(consumer).accept(someStop(), someTime());
	}
	
	@Test
	public void endIsStopWithEarliestArrival() {
		Stop start = someStop();
		Stop end = anotherStop();
		Times times = times(start, end, someTime(), startAndEnd);
		
		Optional<Stop> earliestArrival = times.stopWithEarliestArrival();
		
		assertThat(earliestArrival , hasValue(end));
	}

	@Test
	public void isAfterArrivalAtEnd() {
		Stop start = someStop();
		Stop end = anotherStop();
		Time atTime = someTime();
		Time reachable = someTime();
		Time tooLateTime = twoMinutesLater();
		Times times = times(start, end, atTime, startAndEnd);

		assertFalse(times.isAfterArrivalAtEnd(reachable));
		assertFalse(times.isAfterArrivalAtEnd(tooLateTime));
		
		times.set(end, oneMinuteLater());
		
		assertFalse(times.isAfterArrivalAtEnd(reachable));
		assertTrue(times.isAfterArrivalAtEnd(tooLateTime));
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

	private Times times(int numberOfStops) {
		Stop stop = stop().minimumChangeTime(defaultChangeTime).build();
		return times(stop, someTime(), numberOfStops);
	}

	private Times times(Stop start, Time departure, int numberOfStops) {
		Stop dummyEnd = Data.otherStop();
		return times(start, dummyEnd , departure, numberOfStops);
	}
	private Times times(Stop start, Stop end, Time departure, int numberOfStops) {
		return SingleStart.from(start, end, departure, numberOfStops);
	}
}
