package edu.kit.ifv.mobitopp.publictransport.model;

import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Test;

public class BaseStationTest {

	private BiFunction<Stop, Stop, Optional<Object>> function;
	private Stop someStop;
	private Stop anotherStop;
	private Stop otherStop;
	private Stop yetAnotherStop;
	private Station station;
	private Station otherStation;
	private Object result1;
	private Object result2;
	private Object result3;
	private Object result4;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() throws Exception {
		someStop = Data.someStop();
		anotherStop = Data.anotherStop();
		otherStop = Data.otherStop();
		yetAnotherStop = Data.yetAnotherStop();
		station = someStation();
		otherStation = anotherStation();
		function = mock(BiFunction.class);
		result1 = new Object();
		result2 = new Object();
		result3 = new Object();
		result4 = new Object();
	}

	@Test
	public void doesNotCallFunctionWhenStationContainsOneStopButOtherStationDoesNot()
			throws Exception {
		station.add(someStop);

		callToEachOf();

		verifyNoFunctionCalls();
	}

	@Test
	public void doesNotCallFunctionWhenStationDoesNotContainStopsButOtherStationContainsOneStop()
			throws Exception {
		otherStation.add(someStop);

		callToEachOf();

		verifyNoFunctionCalls();
	}

	@Test
	public void doesNotCallFunctionWhenStationsDoNotContainStops() throws Exception {
		callToEachOf();

		verifyNoFunctionCalls();
	}

	private void verifyNoFunctionCalls() {
		verifyZeroInteractions(function);
	}

	@Test
	public void callsFunctionWhenBothStationsContainSeveralStopsButEmptyOptionalWillBeReturned() {
		linkSeveralStopsAndStations();

		when(function.apply(any(), any())).thenReturn(Optional.empty());

		List<Object> tours = callToEachOf();

		assertThat(tours, is(empty()));
		verifySeveralFunctionCalls();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void callsFunctionWhenBothStationsContainSeveralStopsAndFunctionReturnsAResultForAll() {
		linkSeveralStopsAndStations();

		when(function.apply(any(), any())).thenReturn(of(result1), of(result2), of(result3),
				of(result4));

		List<Object> tours = callToEachOf();

		assertThat(tours, contains(result1, result2, result3, result4));
		verifySeveralFunctionCalls();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void callsFunctionWhenBothStationsContainSeveralStopsAndFunctionReturnsAResultForSome() {
		linkSeveralStopsAndStations();

		when(function.apply(any(), any())).thenReturn(of(result1), Optional.empty(), of(result2),
				Optional.empty());

		List<Object> tours = callToEachOf();

		assertThat(tours, contains(result1, result2));
		verifySeveralFunctionCalls();
	}

	private void linkSeveralStopsAndStations() {
		station.add(someStop);
		station.add(anotherStop);
		otherStation.add(otherStop);
		otherStation.add(yetAnotherStop);
	}

	private void verifySeveralFunctionCalls() {
		verify(function, times(4)).apply(any(), any());
	}

	private List<Object> callToEachOf() {
		return station.toEachOf(otherStation, function);
	}

	private static Station someStation() {
		return newStation(0);
	}

	private static Station anotherStation() {
		return newStation(1);
	}

	private static BaseStation newStation(int id) {
		return new BaseStation(id, emptyList()) {

			@Override
			public RelativeTime minimumChangeTime(int id) {
				throw new RuntimeException("Should never be called");
			}
		};
	}

}
