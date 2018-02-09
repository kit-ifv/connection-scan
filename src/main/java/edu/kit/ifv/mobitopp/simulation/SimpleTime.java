package edu.kit.ifv.mobitopp.simulation;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.result.DateFormat;

public class SimpleTime implements Time, Comparable<Time> {

	private final long seconds;

	public SimpleTime() {
		super();
		this.seconds = 0;
	}
	
	public SimpleTime(Time date) {
		this(date.fromStart());
	}

	public SimpleTime(RelativeTime fromStart) {
		super();
		this.seconds = inSeconds(fromStart);
	}
	
	public SimpleTime(int days, int hours, int minutes, int seconds) {
		this(RelativeTime.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds));
	}
	
	public static Time ofSeconds(long seconds) {
		return new SimpleTime(RelativeTime.ofSeconds(seconds));
	}

	private long inSeconds(RelativeTime fromStart) {
		return fromStart.seconds();
	}

	@Override
	public long toSeconds() {
		return this.seconds;
	}

	@Override
	public int getDay() {
		return (int) fromStart().toDays();
	}

	@Override
	public DayOfWeek weekDay() {
		int weekDay = getDay() % 7;
		return DayOfWeek.getTypeFromInt(weekDay);
	}

	@Override
	public int getHour() {
		return (int) fromStart().toHours() % 24;
	}

	@Override
	public int getMinute() {
		return (int) (fromStart().toMinutes() % 60);
	}

	@Override
	public int getSecond() {
		return (int) fromStart().seconds() % 60;
	}

	@Override
	public Time previousDay() {
		RelativeTime previousDay = RelativeTime.ofDays(fromStart().toDays()).minusDays(1);
		return new SimpleTime(previousDay);
	}

	@Override
	public Time nextDay() {
		RelativeTime nextDay = RelativeTime.ofDays(fromStart().toDays()).plusDays(1);
		return new SimpleTime(nextDay);
	}

	@Override
	public boolean isMidnight() {
		return (getSecond() == 0) && (getMinute() == 0) && (getHour() == 0);
	}

	@Override
	public boolean isAfter(Time otherDate) {
		return toSeconds() > inSeconds(otherDate);
	}

	@Override
	public boolean isBefore(Time otherDate) {
		return toSeconds() < inSeconds(otherDate);
	}

	@Override
	public boolean isBeforeOrEqualTo(Time otherDate) {
		return toSeconds() <= inSeconds(otherDate);
	}

	@Override
	public boolean isAfterOrEqualTo(Time otherDate) {
		return toSeconds() >= inSeconds(otherDate);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (seconds ^ (seconds >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleTime other = (SimpleTime) obj;
		if (seconds != other.seconds)
			return false;
		return true;
	}

	@Override
	public Time minus(RelativeTime increment) {
		RelativeTime changed = fromStart().minus(increment);
		return new SimpleTime(changed);
	}
	
	@Override
	public Time plus(long amount, ChronoUnit unit) {
		RelativeTime changed = fromStart().plus(RelativeTime.of(amount, unit));
		return new SimpleTime(changed);
	}
	
	@Override
	public Time plus(RelativeTime increment) {
		RelativeTime changed = fromStart().plus(increment);
		return new SimpleTime(changed);
	}

	@Override
	public Time plusDays(int increment) {
		RelativeTime changed = fromStart().plusDays(increment);
		return new SimpleTime(changed);
	}

	@Override
	public Time plusHours(int increment) {
		RelativeTime changed = fromStart().plusHours(increment);
		return new SimpleTime(changed);
	}

	@Override
	public Time plusMinutes(int increment) {
		RelativeTime changed = fromStart().plusMinutes(increment);
		return new SimpleTime(changed);
	}

	@Override
	public Time plusSeconds(int increment) {
		RelativeTime changed = fromStart().plusSeconds(increment);
		return new SimpleTime(changed);
	}

	@Override
	public Time startOfDay() {
		RelativeTime changed = RelativeTime.ofDays(fromStart().toDays());
		return new SimpleTime(changed);
	}

	@Override
	public Time newTime(int hour, int minute, int second) {
		assert hour >= 0 && hour < 28 : (hour + ":" + minute);
		assert minute >= 0 && minute < 60;
		assert second >= 0 && second < 60;
		int day_offset = hour / 24;
		assert day_offset >= 0 && day_offset <= 1 : day_offset;

		RelativeTime changed = RelativeTime
				.ofDays(fromStart().toDays())
				.plusHours(hour)
				.plusMinutes(minute)
				.plusSeconds(second);
		return new SimpleTime(changed);
	}

	public RelativeTime differenceTo(Time otherDate) {
		return this.fromStart().minus(otherDate.fromStart());
	}

	private long inSeconds(Time otherDate) {
		if (otherDate instanceof SimpleTime) {
			return ((SimpleTime) otherDate).toSeconds();
		} else {
			return inSeconds(otherDate.fromStart());
		}
	}

	@Override
	public RelativeTime fromStart() {
		return RelativeTime.of(seconds, SECONDS);
	}

	@Override
	public String toString() {
		return new DateFormat().asWeekdayTime(this);
	}

	@Override
	public int compareTo(Time other) {
		if (isBefore(other)) {
			return -1;
		} else if (other.isBefore(this)) {
			return 1;
		}
		return 0;
	}

	public Time plus(int amount, ChronoUnit unit) {
		return plus(RelativeTime.of(amount, unit));
	}

	public static List<Time> oneWeek() {
		Time start = new SimpleTime();
		List<Time> week = new ArrayList<>();
		for (int day = 0; day < 7; day++) {
			week.add(start.plusDays(day));
		}
		return week;
	}

	public static Time future() {
		return new SimpleTime(RelativeTime.ofDays(4000));
	}

}
