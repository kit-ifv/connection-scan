package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class Data {

	private static final int secondsOfDay = 86400;
	private static final int day = 1;
	private static final int month = 1;
	private static final int year = 1;

	public static Time time(int hour, int minute) {
		return new Time(LocalDateTime.of(year, month, day, hour, minute));
	}

	public static Time second(int seconds) {
		LocalDate date = LocalDate.ofYearDay(0, asStartDays(seconds));
		LocalTime time = LocalTime.ofSecondOfDay(asSecondsAtDay(seconds));
		return new Time(LocalDateTime.of(date, time));
	}

	private static int asSecondsAtDay(int seconds) {
		return seconds % secondsOfDay;
	}

	private static int asStartDays(int seconds) {
		return seconds / secondsOfDay + 1;
	}

	public static Point2D coordinate(float x, float y) {
		return new Point2D.Float(x, y);
	}

	public static Time oneMinuteEarlier() {
		return someTime().minus(RelativeTime.of(1, MINUTES));
	}

	public static Time someTime() {
		return time(0, 0);
	}

	public static Time oneMinuteLater() {
		return someTime().plus(RelativeTime.of(1, MINUTES));
	}

	public static Time twoMinutesLater() {
		return oneMinuteLater().plus(RelativeTime.of(1, MINUTES));
	}

	public static Time threeMinutesLater() {
		return twoMinutesLater().plus(RelativeTime.of(1, MINUTES));
	}

	public static Time fourMinutesLater() {
		return threeMinutesLater().plus(RelativeTime.of(1, MINUTES));
	}

	public static Stop someStop() {
		return stop()
				.withId(0)
				.withName("some stop")
				.with(coordinate(0, 0))
				.build();
	}

	public static Stop anotherStop() {
		return stop()
				.withId(1)
				.withName("another stop")
				.with(coordinate(1, 2))
				.build();
	}

	public static Stop otherStop() {
		return stop()
				.withId(2)
				.withName("other stop")
				.with(coordinate(3, 4))
				.build();
	}

	public static Stop yetAnotherStop() {
		return stop()
				.withId(3)
				.withName("yet another stop")
				.with(coordinate(5, 6))
				.build();
	}

	public static Connection fromSomeToAnother() {
		return connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	public static Connection laterFromSomeToAnother() {
		return connection()
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	public static Connection fromOtherToAnother() {
		return connection()
				.startsAt(otherStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	public static Connection fromAnotherToOther() {
		return connection()
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

}
