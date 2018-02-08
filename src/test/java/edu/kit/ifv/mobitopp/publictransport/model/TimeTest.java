package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.RelativeTime.of;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TimeTest {

	private Time oneYearEarlier;
	private Time oneMonthEarlier;
	private Time oneDayEarlier;
	private Time oneHourEarlier;
	private Time oneMinuteEarlier;
	private Time oneSecondEarlier;
	private Time current;
	private Time same;
	private Time oneSecondLater;
	private Time oneMinuteLater;
	private Time oneHourLater;
	private Time oneDayLater;
	private Time oneMonthLater;
	private Time oneYearLater;

	@Before
	public void initialise() throws Exception {
		current = time(LocalDateTime.of(2016, 5, 27, 10, 38));
		same = Time.ofSeconds(current.toSeconds());
		oneYearEarlier = time(LocalDateTime.of(2015, 5, 27, 10, 38));
		oneMonthEarlier = time(LocalDateTime.of(2016, 4, 27, 10, 38));
		oneDayEarlier = current.minus(of(1, DAYS));
		oneHourEarlier = current.minus(of(1, HOURS));
		oneMinuteEarlier = current.minus(of(1, MINUTES));
		oneSecondEarlier = current.minus(of(1, SECONDS));
		oneSecondLater = current.plus(of(1, SECONDS));
		oneMinuteLater = current.plus(of(1, MINUTES));
		oneHourLater = current.plus(of(1, HOURS));
		oneDayLater = current.plus(of(1, DAYS));
		oneMonthLater = time(LocalDateTime.of(2016, 6, 27, 10, 38));
		oneYearLater = time(LocalDateTime.of(2017, 5, 27, 10, 38));
	}

	@Test
	public void compareToLaterSecond() throws Exception {
		Time second = oneSecondLater;
		compareToLater(second);
	}

	@Test
	public void compareToLaterMinute() throws Exception {
		Time minute = oneMinuteLater;
		compareToLater(minute);
	}

	@Test
	public void compareToLaterHour() throws Exception {
		Time hour = oneHourLater;
		compareToLater(hour);
	}

	@Test
	public void compareToLaterDay() throws Exception {
		Time day = oneDayLater;
		compareToLater(day);
	}

	@Test
	public void compareToLaterMonth() throws Exception {
		Time month = oneMonthLater;
		compareToLater(month);
	}

	@Test
	public void compareToLaterYear() throws Exception {
		Time year = oneYearLater;
		compareToLater(year);
	}

	private void compareToLater(Time later) {
		assertThat(current.compareTo(later), is(lessThan(0)));
		assertThat(current.isBefore(later), is(true));
		assertThat(current.isBeforeOrEqualTo(later), is(true));
		assertThat(current.isAfter(later), is(false));
		assertThat(current.isAfterOrEqualTo(later), is(false));
	}

	@Test
	public void compareToEarlierSecond() throws Exception {
		Time second = oneSecondEarlier;
		compareToEarlier(second);
	}

	@Test
	public void compareToEarlierMinute() throws Exception {
		Time minute = oneMinuteEarlier;
		compareToEarlier(minute);
	}

	@Test
	public void compareToEarlierHour() throws Exception {
		Time hour = oneHourEarlier;
		compareToEarlier(hour);
	}

	@Test
	public void compareToEarlierDay() throws Exception {
		Time day = oneDayEarlier;
		compareToEarlier(day);
	}

	@Test
	public void compareToEarlierMonth() throws Exception {
		Time month = oneMonthEarlier;
		compareToEarlier(month);
	}

	@Test
	public void compareToEarlierYear() throws Exception {
		Time year = oneYearEarlier;
		compareToEarlier(year);
	}

	private void compareToEarlier(Time earlier) {
		assertThat(current.compareTo(earlier), is(greaterThan(0)));
		assertThat(current.isBefore(earlier), is(false));
		assertThat(current.isBeforeOrEqualTo(earlier), is(false));
		assertThat(current.isAfter(earlier), is(true));
		assertThat(current.isAfterOrEqualTo(earlier), is(true));
	}

	@Test
	public void compareToSameTime() throws Exception {
		assertThat(current.compareTo(same), is(0));
		assertThat(current.isBefore(same), is(false));
		assertThat(current.isBeforeOrEqualTo(same), is(true));
		assertThat(current.isAfter(same), is(false));
		assertThat(current.isAfterOrEqualTo(same), is(true));
	}

	@Test
	public void infiniteIsLaterThanOtherTimes() throws Exception {
		assertThat(Time.infinite.isAfter(current), is(true));
	}

	@Test
	public void incrementsTimeByRelativeAmount() throws Exception {
		RelativeTime oneMinute = RelativeTime.of(1, MINUTES);

		Time increasedTime = current.plus(oneMinute);

		assertThat(increasedTime, is(oneMinuteLater));
		assertThat(increasedTime, is(not(sameInstance(current))));
	}

	@Test
	public void decrementsTimeByRelativeAmount() throws Exception {
		RelativeTime oneMinute = RelativeTime.of(1, MINUTES);

		Time decreasedTime = current.minus(oneMinute);

		assertThat(decreasedTime, is(oneMinuteEarlier));
		assertThat(decreasedTime, is(not(sameInstance(current))));
	}

	@Test
	public void differenceTo() throws Exception {
		LocalDateTime minute1 = LocalDateTime.of(1, 1, 1, 0, 1, 0);
		LocalDateTime minute3 = LocalDateTime.of(1, 1, 1, 0, 3, 0);
		Time earlier = time(minute1);
		Time later = time(minute3);

		RelativeTime duration = later.differenceTo(earlier);

		assertThat(duration, is(equalTo(RelativeTime.of(2, MINUTES))));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(Time.class).usingGetClass().verify();
	}

	private static Time time(LocalDateTime time) {
		return new Time(time);
	}
}
