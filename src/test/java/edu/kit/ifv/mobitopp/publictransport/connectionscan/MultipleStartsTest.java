package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.simulation.SimulationDateIfc.infinite;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.function.BiConsumer;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopBuilder;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

public class MultipleStartsTest {

	private static final int onlyStartStops = 2;
	private static final int additionalStops = 4;
	private static final RelativeTime changeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime noMinutes = RelativeTime.ZERO;
	private static final RelativeTime oneMinute = RelativeTime.of(1, MINUTES);
	private static final RelativeTime twoMinutes = RelativeTime.of(2, MINUTES);
	
	@Test
	public void initialisesTimeAtStart() throws Exception {
		List<StopPath> starts = someStartPaths();
		ArrivalTimes times = timesFromPaths(starts, someTime(), onlyStartStops);

		SimulationDateIfc time = times.get(startStop());

		assertThat(time, is(oneMinuteLater()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void initialisesOtherStops() throws Exception {
		BiConsumer<Stop, SimulationDateIfc> consumer = mock(BiConsumer.class);
		ArrivalTimes times = timesFromPaths(someStartPaths(), someTime(), onlyStartStops);

		times.initialise(consumer);

		verify(consumer).accept(startStop(), oneMinuteLater());
		verify(consumer).accept(anotherStartStop(), twoMinutesLater());
	}

	@Test
	public void returnsInfiniteWhenTimeHasNotBeenSet() throws Exception {
		ArrivalTimes times = times(onlyStartStops);

		SimulationDateIfc time = times.getConsideringMinimumChangeTime(targetStop());

		assertThat(time, is(equalTo(SimulationDateIfc.infinite)));
	}

	@Test
	public void returnsSetTimeAfterTimeHasBeenSet() throws Exception {
		ArrivalTimes times = times(additionalStops);

		Stop stop = targetStop();
		SimulationDateIfc timeToSet = someTime();
		times.set(stop, timeToSet);
		SimulationDateIfc time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(timeToSet)));
	}

	@Test
	public void returnsSetTimeWhenTimesContainsSeveralTimes() throws Exception {
		Stop stop0 = targetStop();
		Stop stop1 = anotherTargetStop();
		SimulationDateIfc timeToSetForStop0 = someTime();
		SimulationDateIfc timeToSetForStop1 = oneMinuteLater();
		int numberOfStops = additionalStops;

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
		Stop stop = targetStop();

		ArrivalTimes times = timesFromPaths(noWalkTime(), someTime(), onlyStartStops);
		SimulationDateIfc time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(infinite)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooHigh() throws Exception {
		int tooHighIndex = 2;
		Stop stop = buildTargetStop().withId(tooHighIndex).build();

		ArrivalTimes times = times(onlyStartStops);
		SimulationDateIfc time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(SimulationDateIfc.infinite)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooLow() throws Exception {
		int tooLowIndex = -1;
		Stop stop = stop().withId(tooLowIndex).build();

		ArrivalTimes times = times(onlyStartStops);
		SimulationDateIfc time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(SimulationDateIfc.infinite)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeAtStartStops() throws Exception {
		SimulationDateIfc timeAtStart = oneMinuteLater();
		ArrivalTimes times = timesFromPaths(noWalkTime(), timeAtStart, onlyStartStops);
		times.set(startStop(), timeAtStart);

		assertThat(times.getConsideringMinimumChangeTime(startStop()), is(equalTo(timeAtStart)));
		assertThat(times.getConsideringMinimumChangeTime(anotherStartStop()), is(equalTo(timeAtStart)));
	}

	@Test
	public void considersMinimumChangeTimeAtGivenStopOtherThanStart() throws Exception {
		Stop otherStop = targetStop(changeTime);
		SimulationDateIfc timeAtStart = someTime();
		SimulationDateIfc timeAtOther = oneMinuteLater();
		ArrivalTimes times = timesFromPaths(noWalkTime(), timeAtStart, additionalStops);
		times.set(otherStop, timeAtOther);

		SimulationDateIfc timeAtOtherIncludingChangeTime = oneMinuteLater().plus(changeTime);
		assertThat(times.getConsideringMinimumChangeTime(otherStop),
				is(equalTo(timeAtOtherIncludingChangeTime)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGetWithStartStop() throws Exception {
		SimulationDateIfc timeAtStart = someTime();
		ArrivalTimes times = timesFromPaths(noWalkTime(), timeAtStart, onlyStartStops);

		assertThat(times.get(startStop()), is(equalTo(timeAtStart)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGet() throws Exception {
		Stop otherStop = targetStop();
		SimulationDateIfc timeAtStart = someTime();
		SimulationDateIfc timeAtOther = oneMinuteLater();
		ArrivalTimes times = timesFromPaths(noWalkTime(), timeAtStart, additionalStops);
		times.set(otherStop, timeAtOther);

		assertThat(times.get(otherStop), is(equalTo(timeAtOther)));
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

	private static SimulationDateIfc oneMinuteLater() {
		return someTime().plus(oneMinute);
	}

	private static SimulationDateIfc twoMinutesLater() {
		return someTime().plus(twoMinutes);
	}

	private ArrivalTimes times(int numberOfStops) {
		SimulationDateIfc someTime = someTime();
		return times(someTime, numberOfStops);
	}

	private ArrivalTimes times(SimulationDateIfc departure, int numberOfStops) {
		return timesFromPaths(someStartPaths(), departure, numberOfStops);
	}

	private ArrivalTimes timesFromPaths(List<StopPath> fromStarts, SimulationDateIfc departure, int numberOfStops) {
		StopPaths starts = DefaultStopPaths.from(fromStarts);
		return timesFromPaths(starts, departure, numberOfStops);
	}

	private ArrivalTimes timesFromPaths(
			StopPaths starts, SimulationDateIfc timeAtStart, int totalNumberOfStops) {
		return MultipleStarts.create(starts, timeAtStart, totalNumberOfStops);
	}
}
