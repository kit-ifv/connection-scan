package edu.kit.ifv.mobitopp.publictransport.model;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class Time implements Comparable<Time> {

	private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
	public static final Time infinite = new Time(LocalDateTime.of(4000, 1, 1, 0, 0));

	private static final int timeFieldWidth = 2;
	private static final int yearFieldWidth = 4;
	private static final int nanoSeconds = 0;

	private final long seconds;

	public Time(LocalDateTime time) {
		this(time.toEpochSecond(zoneOffset));
	}

	private Time(long seconds) {
		super();
		this.seconds = seconds;
	}

	public long toSeconds() {
		return seconds;
	}

	public static Time ofSeconds(long seconds) {
		return new Time(LocalDateTime.ofEpochSecond(seconds, nanoSeconds, zoneOffset));
	}

	public LocalDateTime time() {
		return LocalDateTime.ofEpochSecond(seconds, nanoSeconds, zoneOffset);
	}

	@Override
	public int compareTo(Time other) {
		return Long.compare(seconds, other.seconds);
	}

	public boolean isBefore(Time other) {
		return seconds < other.seconds;
	}

	public boolean isBeforeOrEqualTo(Time other) {
		return seconds <= other.seconds;
	}

	public boolean isAfter(Time other) {
		return seconds > other.seconds;
	}

	public boolean isAfterOrEqualTo(Time other) {
		return seconds >= other.seconds;
	}

	public RelativeTime differenceTo(Time other) {
		long duration = seconds - other.seconds;
		return RelativeTime.of(duration, SECONDS);
	}

	public Time plus(RelativeTime relativeTime) {
		return new Time(seconds + relativeTime.seconds());
	}

	public Time minus(RelativeTime relativeTime) {
		return new Time(seconds - relativeTime.seconds());
	}

	public Time plus(int amount, ChronoUnit unit) {
		return plus(RelativeTime.of(amount, unit));
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Time other = (Time) obj;
		if (seconds != other.seconds) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Time [seconds=" + seconds + ", formatted=" + prettyPrint().format(time()) + "]";
	}

	private static DateTimeFormatter prettyPrint() {
		String separator = "-";
		return new DateTimeFormatterBuilder()
				.appendValue(ChronoField.YEAR, yearFieldWidth)
				.appendLiteral(separator)
				.appendValue(ChronoField.MONTH_OF_YEAR, timeFieldWidth)
				.appendLiteral(separator)
				.appendValue(ChronoField.DAY_OF_MONTH, timeFieldWidth)
				.appendLiteral(separator)
				.appendValue(ChronoField.HOUR_OF_DAY, timeFieldWidth)
				.appendLiteral(separator)
				.appendValue(ChronoField.MINUTE_OF_HOUR, timeFieldWidth)
				.appendLiteral(separator)
				.appendValue(ChronoField.SECOND_OF_MINUTE, timeFieldWidth)
				.toFormatter();
	}

}
