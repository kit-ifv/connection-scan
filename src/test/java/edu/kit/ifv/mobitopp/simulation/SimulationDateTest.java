package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class SimulationDateTest {

	private SimulationDateIfc date;
	private SimulationDateIfc time;

	private final int year = 1970;
	private final int month = 1;
	private final int day = 0;
	private final int hour = 6;
	private final int minute = 30;
	private final int second = 5;
	private SimulationDateIfc monday;
	private SimulationDateIfc tuesday;
	private SimulationDateIfc wednesday;
	private SimulationDateIfc thursday;
	private SimulationDateIfc friday;
	private SimulationDateIfc saturday;
	private SimulationDateIfc sunday;
	private SimulationDateIfc nextMonday;
	private SimulationDateIfc same;
	private SimulationDateIfc later;

	@Before
	public void setUp() {
		date = new SimulationDate();
		time = date.plusHours(hour).plusMinutes(minute).plusSeconds(second);
		same = new SimulationDate(time);
		later = time.plusSeconds(1);
		monday = new SimulationDate();
		tuesday = monday.nextDay();
		wednesday = tuesday.nextDay();
		thursday = wednesday.nextDay();
		friday = thursday.nextDay();
		saturday = friday.nextDay();
		sunday = saturday.nextDay();
		nextMonday = sunday.nextDay();
	}
	
	@Test
	public void startsAtMonday() {
		SimulationDateIfc date = new SimulationDate();
		
		assertThat(date.weekDay(), is(DayOfWeek.MONDAY));
	}
	
	@Test
	public void oneWeek() {
		assertThat(SimulationDate.oneWeek(),
				contains(monday, tuesday, wednesday, thursday, friday, saturday, sunday));
	}

	@Test
	public void constructor() {
		assertUnchangedDayOf(date);
		assertZeroHourOf(date);
		assertZeroMinuteOf(date);
		assertZeroSecondOf(date);

		assertUnchangedDayOf(time);
		assertUnchangedHourOf(time);
		assertUnchangedMinuteOf(time);
		assertUnchangedSecondOf(time);
	}

	private void assertZeroSecondOf(SimulationDateIfc date) {
		assertEquals("failure - second wrong", 0, date.getSecond());
	}

	private void assertZeroMinuteOf(SimulationDateIfc date) {
		assertEquals("failure - minute wrong", 0, date.getMinute());
	}

	private void assertZeroHourOf(SimulationDateIfc date) {
		assertEquals("failure - hour wrong", 0, date.getHour());
	}

	private void assertUnchangedSecondOf(SimulationDateIfc date) {
		assertEquals("failure - second wrong", second, date.getSecond());
	}

	private void assertUnchangedMinuteOf(SimulationDateIfc date) {
		assertEquals("failure - minute wrong", minute, date.getMinute());
	}

	private void assertUnchangedHourOf(SimulationDateIfc date) {
		assertEquals("failure - hour wrong", hour, date.getHour());
	}

	private void assertUnchangedDayOf(SimulationDateIfc date) {
		assertEquals("failure - day wrong", day, date.getDay());
	}

	@Test
	public void weekDay() {
		assertThat(monday.weekDay(), is(equalTo(DayOfWeek.MONDAY)));
		assertThat(tuesday.weekDay(), is(equalTo(DayOfWeek.TUESDAY)));
		assertThat(wednesday.weekDay(), is(equalTo(DayOfWeek.WEDNESDAY)));
		assertThat(thursday.weekDay(), is(equalTo(DayOfWeek.THURSDAY)));
		assertThat(friday.weekDay(), is(equalTo(DayOfWeek.FRIDAY)));
		assertThat(saturday.weekDay(), is(equalTo(DayOfWeek.SATURDAY)));
		assertThat(sunday.weekDay(), is(equalTo(DayOfWeek.SUNDAY)));
		assertThat(nextMonday.weekDay(), is(equalTo(DayOfWeek.MONDAY)));
	}

	@Test
	public void previousDay() {
		SimulationDateIfc nextDate = date.previousDay();
		
		assertChangedDayOf(nextDate, day - 1);
		assertZeroHourOf(nextDate);
		assertZeroMinuteOf(nextDate);
		assertZeroSecondOf(nextDate);
		
		SimulationDateIfc nextTime = time.previousDay();
		assertChangedDayOf(nextTime, day - 1);
		assertZeroHourOf(nextTime);
		assertZeroMinuteOf(nextTime);
		assertZeroSecondOf(nextTime);
	}
	
	@Test
	public void nextDay() {
		SimulationDateIfc nextDate = date.nextDay();

		assertChangedDayOf(nextDate, day + 1);
		assertZeroHourOf(nextDate);
		assertZeroMinuteOf(nextDate);
		assertZeroSecondOf(nextDate);

		SimulationDateIfc nextTime = time.nextDay();
		assertChangedDayOf(nextTime, day + 1);
		assertZeroHourOf(nextTime);
		assertZeroMinuteOf(nextTime);
		assertZeroSecondOf(nextTime);
	}
	
	@Test
	public void before() {
		assertTrue(time.isBefore(later));
	}
	
	@Test
	public void beforeOrEqual() {
		assertTrue(time.isBeforeOrEqualTo(later));
		assertTrue(time.isBeforeOrEqualTo(same));
	}
	
	@Test
	public void after() {
		assertTrue(later.isAfter(time));
	}

	@Test
	public void afterOrEqual() {
		assertTrue(later.isAfterOrEqualTo(time));
		assertTrue(time.isAfterOrEqualTo(same));
	}
	
	@Test
	public void compare() {
		assertThat(later.compareTo(time), is(greaterThan(0)));
		assertThat(time.compareTo(later), is(lessThan(0)));
		assertThat(same.compareTo(time), is(equalTo(0)));
	}

	@Test
	public void isMidnight() {
		assertTrue(date.isMidnight());
	}
	
	@Test
	public void isNotMidnight() {
		SimulationDateIfc oneSecondAfter = date.plusSeconds(1);
		SimulationDateIfc oneMinuteAfter = date.plusMinutes(1);
		SimulationDateIfc oneHourAfter = date.plusHours(1);
		assertFalse(oneSecondAfter.isMidnight());
		assertFalse(oneMinuteAfter.isMidnight());
		assertFalse(oneHourAfter.isMidnight());
	}

	private void assertChangedDayOf(SimulationDateIfc nextDate, int expected) {
		assertEquals("failure - day wrong", expected, nextDate.getDay());
	}
	
	@Test
	public void decrease() {
		int seconds = 1;
		SimulationDateIfc changed = time.minus(RelativeTime.ofSeconds(seconds));
		
		assertChangedSecondOf(changed, second - seconds);
		assertUnchangedDayOf(changed);
		assertUnchangedHourOf(changed);
		assertUnchangedMinuteOf(changed);
	}
	
	@Test
	public void increase() {
		int seconds = 1;
		SimulationDateIfc changed = time.plus(RelativeTime.ofSeconds(seconds));
		
		assertChangedSecondOf(changed, second + seconds);
		assertUnchangedDayOf(changed);
		assertUnchangedHourOf(changed);
		assertUnchangedMinuteOf(changed);
	}

	@Test
	public void increaseDay() {
		int increment = 1;
		SimulationDateIfc next = time.plusDays(increment);
		
		assertChangedDayOf(next, day + increment);
		assertUnchangedHourOf(next);
		assertUnchangedMinuteOf(next);
		assertUnchangedSecondOf(next);
	}
	@Test
	public void increaseMinute() {
		int inc_dd = 2;
		int inc_hh = 2;
		int inc_mm = 10;

		SimulationDateIfc next = time.plusMinutes(inc_dd * 24 * 60 + inc_hh * 60 + inc_mm);

		assertChangedDayOf(next, day + inc_dd);
		assertChangedHourOf(next, hour + inc_hh);
		assertChangedMinuteOf(next, minute + inc_mm);
		assertUnchangedSecondOf(next);
	}

	private void assertChangedMinuteOf(SimulationDateIfc next, int minute) {
		assertEquals("failure - minute wrong", minute, next.getMinute());
	}

	private void assertChangedHourOf(SimulationDateIfc next, int hour) {
		assertEquals("failure - hour wrong", hour, next.getHour());
	}

	@Test
	public void increaseSecond() {
		int inc_dd = 2;
		int inc_hh = 2;
		int inc_mm = 10;
		int inc_ss = 17;

		SimulationDateIfc next = time
				.plusSeconds((((inc_dd * 24) + inc_hh) * 60 + inc_mm) * 60 + inc_ss);

		assertChangedDayOf(next, day + inc_dd);
		assertChangedHourOf(next, hour + inc_hh);
		assertChangedMinuteOf(next, minute + inc_mm);
		assertChangedSecondOf(next, second + inc_ss);
	}

	private void assertChangedSecondOf(SimulationDateIfc next, int second) {
		assertEquals("failure - second wrong", second, next.getSecond());
	}

	@Test
	public void startOfDay() {
		SimulationDateIfc value = time.startOfDay();

		assertUnchangedDayOf(value);
		assertZeroHourOf(value);
		assertZeroMinuteOf(value);
		assertZeroSecondOf(value);
	}

	@Test
	public void newTime() {
		SimulationDateIfc value = time.newTime(12, 15, 30);

		assertUnchangedDayOf(value);
		assertEquals("failure - hour wrong", 12, value.getHour());
		assertEquals("failure - minute wrong", 15, value.getMinute());
		assertEquals("failure - second wrong", 30, value.getSecond());
	}

	@Test(expected = java.lang.AssertionError.class)
	public void newTimeInvalidInput() {
		time.newTime(0, 0, 61);
	}

	@Test
	public void differenceInSeconds() {
		SimulationDateIfc value = time.plusSeconds(61);

		assertEquals("failure - wrong difference", 61, value.differenceTo(time).seconds());
		assertEquals("failure - wrong difference", -61, time.differenceTo(value).seconds());
	}

	@Test
	public void differenceInMinutes() {
		SimulationDateIfc value = time.plusSeconds(61);

		assertEquals("failure - wrong difference", 1, value.differenceTo(time).toMinutes());
		assertEquals("failure - wrong difference", -1, time.differenceTo(value).toMinutes());

		value = time.plusMinutes(3);

		assertEquals("failure - wrong difference", 3, value.differenceTo(time).toMinutes());
		assertEquals("failure - wrong difference", -3, time.differenceTo(value).toMinutes());
	}

	@Test
	public void testEquals() {

		SimulationDateIfc value = time.startOfDay();

		assertEquals("failure - equals", date, date);
		assertEquals("failure - equals", time, time);

		assertEquals("failure - equals", date, value);
		assertNotEquals("failure - equals", time, value);
	}

	@Test
	public void testHashCode() {

		SimulationDateIfc value = time.startOfDay();

		assertEquals("failure - equals", date, date);
		assertEquals("failure - equals", time, time);
		assertEquals("failure - equals", date, value);

		assertEquals("failure - hashCode", date.hashCode(), date.hashCode());
		assertEquals("failure - hashCode", time.hashCode(), time.hashCode());
		assertEquals("failure - hashCode", date.hashCode(), value.hashCode());
	}

	@Test
	public void fromSimulationDate() throws Exception {
		SimulationDateIfc simulation = time;

		Time date = simulation.toTime();

		Time expectedDate = time();
		assertThat(date, is(equalTo(expectedDate)));
	}

	@Test
	public void convertsToSimulationDatePreservingYearMonthAndDay() throws Exception {
		SimulationDateIfc simulationDate = SimulationDate.from(time());

		assertThat(simulationDate, is(equalTo(this.time)));
	}

	private Time time() {
		return new Time(LocalDateTime.of(year, month, day + 5, hour, minute, second));
	}
}
