package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.publictransport.model.Time.infinite;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopBuilder;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class MultipleStartsTest {

	private static final int noStops = 0;
	private static final int onlyStartStops = 2;
	private static final int additionalStops = 4;
	private static final RelativeTime changeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime noMinutes = RelativeTime.ZERO;
	private static final RelativeTime oneMinute = RelativeTime.of(1, MINUTES);
	private static final RelativeTime twoMinutes = RelativeTime.of(2, MINUTES);
	
	private StopPaths ends;
	
	@Before
	public void initialise() {
		ends = mock(StopPaths.class);
	}

	@Test
	public void initialisesTimeAtStart() throws Exception {
		List<StopPath> starts = someStartPaths();
		Times times = timesFromPaths(starts, someTime(), onlyStartStops);

		Time time = times.get(startStop());

		assertThat(time, is(oneMinuteLater()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void initialisesOtherStops() throws Exception {
		BiConsumer<Stop, Time> consumer = mock(BiConsumer.class);
		Times times = timesFromPaths(someStartPaths(), someTime(), onlyStartStops);

		times.initialise(consumer);

		verify(consumer).accept(startStop(), oneMinuteLater());
		verify(consumer).accept(anotherStartStop(), twoMinutesLater());
	}

	@Test
	public void returnsInfiniteWhenTimeHasNotBeenSet() throws Exception {
		Times times = times(onlyStartStops);

		Time time = times.getConsideringMinimumChangeTime(targetStop());

		assertThat(time, is(equalTo(Time.infinite)));
	}

	@Test
	public void returnsSetTimeAfterTimeHasBeenSet() throws Exception {
		Times times = times(additionalStops);

		Stop stop = targetStop();
		Time timeToSet = someTime();
		times.set(stop, timeToSet);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(timeToSet)));
	}

	@Test
	public void returnsSetTimeWhenTimesContainsSeveralTimes() throws Exception {
		Stop stop0 = targetStop();
		Stop stop1 = anotherTargetStop();
		Time timeToSetForStop0 = someTime();
		Time timeToSetForStop1 = oneMinuteLater();
		int numberOfStops = additionalStops;

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
		Stop stop = targetStop();

		Times times = timesFromPaths(noWalkTime(), someTime(), onlyStartStops);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(infinite)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooHigh() throws Exception {
		int tooHighIndex = 2;
		Stop stop = buildTargetStop().withId(tooHighIndex).build();

		Times times = times(onlyStartStops);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(Time.infinite)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooLow() throws Exception {
		int tooLowIndex = -1;
		Stop stop = stop().withId(tooLowIndex).build();

		Times times = times(onlyStartStops);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(Time.infinite)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeAtStartStops() throws Exception {
		Time timeAtStart = oneMinuteLater();
		Times times = timesFromPaths(noWalkTime(), timeAtStart, onlyStartStops);
		times.set(startStop(), timeAtStart);

		assertThat(times.getConsideringMinimumChangeTime(startStop()), is(equalTo(timeAtStart)));
		assertThat(times.getConsideringMinimumChangeTime(anotherStartStop()), is(equalTo(timeAtStart)));
	}

	@Test
	public void considersMinimumChangeTimeAtGivenStopOtherThanStart() throws Exception {
		Stop otherStop = targetStop(changeTime);
		Time timeAtStart = someTime();
		Time timeAtOther = oneMinuteLater();
		Times times = timesFromPaths(noWalkTime(), timeAtStart, additionalStops);
		times.set(otherStop, timeAtOther);

		Time timeAtOtherIncludingChangeTime = oneMinuteLater().add(changeTime);
		assertThat(times.getConsideringMinimumChangeTime(otherStop),
				is(equalTo(timeAtOtherIncludingChangeTime)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGetWithStartStop() throws Exception {
		Time timeAtStart = someTime();
		Times times = timesFromPaths(noWalkTime(), timeAtStart, onlyStartStops);

		assertThat(times.get(startStop()), is(equalTo(timeAtStart)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGet() throws Exception {
		Stop otherStop = targetStop();
		Time timeAtStart = someTime();
		Time timeAtOther = oneMinuteLater();
		Times times = timesFromPaths(noWalkTime(), timeAtStart, additionalStops);
		times.set(otherStop, timeAtOther);

		assertThat(times.get(otherStop), is(equalTo(timeAtOther)));
	}

	@Test
	public void findsStopWithEarliestArrival() {
		Time timeAtStart = someTime();
		StopPaths starts = DefaultStopPaths.from(emptyList());
		StopPaths ends = DefaultStopPaths.from(asList(shortDistance(), longDistance()));
		Times times = timesFromPaths(starts, ends, timeAtStart, onlyStartStops);

		Optional<Stop> stop = times.stopWithEarliestArrival();

		assertThat(stop, isPresent());
		assertThat(stop, hasValue(nearStop()));
	}

	@Test
	public void doesNotFindStopWithEarliestArrivalWhenNoStopsAreAvailable() {
		Time timeAtStart = someTime();
		Times times = timesFromPaths(emptyList(), timeAtStart , noStops);

		Optional<Stop> stop = times.stopWithEarliestArrival();

		assertThat(stop, isEmpty());
	}
	

	@Test
	public void isAfterArrivalAtOneOfSeveralEndStops() throws Exception {
		StopPaths starts = mock(StopPaths.class);
		StopPaths ends = mock(StopPaths.class);
		when(starts.stopPaths()).thenReturn(emptyList());
		when(ends.stopPaths()).thenReturn(asList(shortDistance(), longDistance()));
		when(ends.stops()).thenReturn(asList(nearStop(), farStop()));
		Time atTime = someTime();
		Times times = timesFromPaths(starts, ends, atTime, additionalStops);
		Time beforeArrival = someTime();
		Time arrival = oneMinuteLater();
		Time afterArrival = twoMinutesLater();
		
		times.set(nearStop(), arrival);
		times.set(farStop(), afterArrival);
		
		assertFalse(times.isAfterArrivalAtEnd(beforeArrival));
		assertFalse(times.isAfterArrivalAtEnd(arrival));
		assertTrue(times.isAfterArrivalAtEnd(afterArrival));
	}

	private StopPath shortDistance() {
		return new StopPath(nearStop(), shortDuration());
	}

	private StopPath longDistance() {
		return new StopPath(farStop(), longDuration());
	}

	private RelativeTime shortDuration() {
		return oneMinute;
	}

	private Stop nearStop() {
		return targetStop();
	}

	private RelativeTime longDuration() {
		return twoMinutes;
	}

	private Stop farStop() {
		return anotherTargetStop();
	}

	private List<StopPath> noWalkTime() {
		return asList(pathTo(startStop(), noMinutes),
				pathTo(anotherStartStop(), noMinutes));
	}

	private List<StopPath> someStartPaths() {
		return asList(pathTo(startStop(), oneMinute), pathTo(anotherStartStop(), twoMinutes));
	}

	private StopPath pathTo(Stop stop, RelativeTime walkTime) {
		return new StopPath(stop, walkTime);
	}

	private Stop startStop() {
		return stop().withId(0).withName("start").minimumChangeTime(changeTime).build();
	}

	private Stop anotherStartStop() {
		return stop().withId(1).withName("another start").build();
	}

	private Stop targetStop() {
		return buildTargetStop().build();
	}
	
	private Stop targetStop(RelativeTime changetime) {
		return buildTargetStop().minimumChangeTime(changetime).build();
	}

	private StopBuilder buildTargetStop() {
		return stop().withId(2).withName("target stop");
	}

	private Stop anotherTargetStop() {
		return stop().withId(3).withName("another target stop").build();
	}

	private static Time oneMinuteLater() {
		return someTime().add(oneMinute);
	}

	private static Time twoMinutesLater() {
		return someTime().add(twoMinutes);
	}

	private Times times(int numberOfStops) {
		Time someTime = someTime();
		return times(someTime, numberOfStops);
	}

	private Times times(Time departure, int numberOfStops) {
		return timesFromPaths(someStartPaths(), departure, numberOfStops);
	}

	private Times timesFromPaths(List<StopPath> fromStarts, Time departure, int numberOfStops) {
		StopPaths starts = DefaultStopPaths.from(fromStarts);
		return MultipleStarts.from(starts, ends, departure, numberOfStops);
	}

	private Times timesFromPaths(
			StopPaths starts, StopPaths ends, Time timeAtStart, int totalNumberOfStops) {
		return MultipleStarts.from(starts, ends, timeAtStart, totalNumberOfStops);
	}
}
