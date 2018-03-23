package edu.kit.ifv.mobitopp.time;

import java.util.EnumSet;

public enum DayOfWeek {

	MONDAY(0),
	TUESDAY(1),
	WEDNESDAY(2),
	THURSDAY(3),
	FRIDAY(4),
	SATURDAY(5),
	SUNDAY(6);

	private final int numeric;

	private DayOfWeek(int numeric) {
		this.numeric = numeric;
	}

	public int getTypeAsInt() {
		return this.numeric;
	}

	public static DayOfWeek getTypeFromInt(int numeric) {
		for (DayOfWeek day : EnumSet.allOf(DayOfWeek.class)) {
			if (day.getTypeAsInt() == numeric) {
				return day;
			}
		}
		throw new AssertionError("invalid code: " + numeric);
	}

}
