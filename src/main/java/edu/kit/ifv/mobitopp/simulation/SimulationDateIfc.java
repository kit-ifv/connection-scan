package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public interface SimulationDateIfc extends Comparable<SimulationDateIfc> {

	int getDay();
	int getHour();
	int getMinute();
	int getSecond();

	SimulationDateIfc previousDay();
	SimulationDateIfc nextDay();

	DayOfWeek weekDay();

	boolean isBefore(SimulationDateIfc otherDate);
	boolean isBeforeOrEqualTo(SimulationDateIfc otherDate);
	boolean isAfter(SimulationDateIfc otherDate);
	boolean isAfterOrEqualTo(SimulationDateIfc otherDate);
	boolean equals(SimulationDateIfc otherDate);
	boolean isMidnight();

	SimulationDateIfc plus(RelativeTime increment);
	SimulationDateIfc plusDays(int increment);
	SimulationDateIfc plusHours(int increment);
	SimulationDateIfc plusMinutes(int increment);
	SimulationDateIfc plusSeconds(int increment);
	SimulationDateIfc minus(RelativeTime decrement);

	SimulationDateIfc startOfDay();
	SimulationDateIfc newTime(int hour, int minute, int second);

	RelativeTime differenceTo(SimulationDateIfc otherDate);
	
	Time toTime();
	RelativeTime fromStart();

}
