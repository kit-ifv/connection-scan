package edu.kit.ifv.mobitopp.result;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.SimulationDate;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

public class DateFormatTest {

	private DateFormat formatter;

	@Before
	public void initialise() {
		formatter = new DateFormat();
	}

	@Test
	public void asTime() {
		String time = formatter.asTime(date());

		assertThat(time, is(equalTo("01:02:03")));
	}

	@Test
	public void asDay() {
		String day = formatter.asDay(date());

		assertThat(day, is(equalTo("Mo")));
	}

	@Test
	public void asFullDate() {
		String time = formatter.asFullDate(date());

		assertThat(time, is(equalTo("05 01:02:03")));
	}

	@Test
	public void asWeekdayTime() {
		String time = formatter.asWeekdayTime(date());

		assertThat(time, is(equalTo("Mo, 05 01:02:03")));
	}
	
	private SimulationDateIfc date() {
		return new SimulationDate().plusHours(1).plusMinutes(2).plusSeconds(3);
	}
}
