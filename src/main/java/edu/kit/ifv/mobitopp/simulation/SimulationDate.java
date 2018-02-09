package edu.kit.ifv.mobitopp.simulation;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.result.DateFormat;

public class SimulationDate implements SimulationDateIfc, Comparable<SimulationDateIfc> {

	public static final LocalDateTime monday = LocalDateTime.of(1970, 1, 5, 0, 0);
	private final long seconds;

	public SimulationDate() {
		super();
		this.seconds = 0;
	}
	
	public SimulationDate(SimulationDateIfc date) {
		this(date.fromStart());
	}

	public SimulationDate(RelativeTime fromStart) {
		super();
		this.seconds = inSeconds(fromStart);
	}
	
	public SimulationDate(int days, int hours, int minutes, int seconds) {
		this(RelativeTime.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds));
	}
	
	public static SimulationDateIfc ofSeconds(long seconds) {
		return new SimulationDate(RelativeTime.ofSeconds(seconds));
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
	public SimulationDateIfc previousDay() {
		RelativeTime previousDay = RelativeTime.ofDays(fromStart().toDays()).minusDays(1);
		return new SimulationDate(previousDay);
	}

	@Override
	public SimulationDateIfc nextDay() {
		RelativeTime nextDay = RelativeTime.ofDays(fromStart().toDays()).plusDays(1);
		return new SimulationDate(nextDay);
	}

	@Override
	public boolean isMidnight() {
		return (getSecond() == 0) && (getMinute() == 0) && (getHour() == 0);
	}

	@Override
	public boolean isAfter(SimulationDateIfc otherDate) {
		return toSeconds() > inSeconds(otherDate);
	}

	@Override
	public boolean isBefore(SimulationDateIfc otherDate) {
		return toSeconds() < inSeconds(otherDate);
	}

	@Override
	public boolean isBeforeOrEqualTo(SimulationDateIfc otherDate) {
		return toSeconds() <= inSeconds(otherDate);
	}

	@Override
	public boolean isAfterOrEqualTo(SimulationDateIfc otherDate) {
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
		SimulationDate other = (SimulationDate) obj;
		if (seconds != other.seconds)
			return false;
		return true;
	}

	@Override
	public SimulationDateIfc minus(RelativeTime increment) {
		RelativeTime changed = fromStart().minus(increment);
		return new SimulationDate(changed);
	}
	
	@Override
	public SimulationDateIfc plus(long amount, ChronoUnit unit) {
		RelativeTime changed = fromStart().plus(RelativeTime.of(amount, unit));
		return new SimulationDate(changed);
	}
	
	@Override
	public SimulationDateIfc plus(RelativeTime increment) {
		RelativeTime changed = fromStart().plus(increment);
		return new SimulationDate(changed);
	}

	@Override
	public SimulationDateIfc plusDays(int increment) {
		RelativeTime changed = fromStart().plusDays(increment);
		return new SimulationDate(changed);
	}

	@Override
	public SimulationDateIfc plusHours(int increment) {
		RelativeTime changed = fromStart().plusHours(increment);
		return new SimulationDate(changed);
	}

	@Override
	public SimulationDateIfc plusMinutes(int increment) {
		RelativeTime changed = fromStart().plusMinutes(increment);
		return new SimulationDate(changed);
	}

	@Override
	public SimulationDateIfc plusSeconds(int increment) {
		RelativeTime changed = fromStart().plusSeconds(increment);
		return new SimulationDate(changed);
	}

	@Override
	public SimulationDateIfc startOfDay() {
		RelativeTime changed = RelativeTime.ofDays(fromStart().toDays());
		return new SimulationDate(changed);
	}

	@Override
	public SimulationDateIfc newTime(int hour, int minute, int second) {
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
		return new SimulationDate(changed);
	}

	public RelativeTime differenceTo(SimulationDateIfc otherDate) {
		return this.fromStart().minus(otherDate.fromStart());
	}

	private long inSeconds(SimulationDateIfc otherDate) {
		if (otherDate instanceof SimulationDate) {
			return ((SimulationDate) otherDate).toSeconds();
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
	public int compareTo(SimulationDateIfc other) {
		if (isBefore(other)) {
			return -1;
		} else if (other.isBefore(this)) {
			return 1;
		}
		return 0;
	}


	public SimulationDateIfc plus(int amount, ChronoUnit unit) {
		return plus(RelativeTime.of(amount, unit));
	}

	public static List<SimulationDateIfc> oneWeek() {
		SimulationDateIfc start = new SimulationDate();
		List<SimulationDateIfc> week = new ArrayList<>();
		for (int day = 0; day < 7; day++) {
			week.add(start.plusDays(day));
		}
		return week;
	}

	public static SimulationDateIfc future() {
		return new SimulationDate(RelativeTime.ofDays(4000));
	}

}
