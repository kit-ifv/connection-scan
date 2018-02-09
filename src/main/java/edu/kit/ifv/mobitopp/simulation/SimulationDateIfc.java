package edu.kit.ifv.mobitopp.simulation;

import java.time.temporal.ChronoUnit;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;

public interface SimulationDateIfc extends Comparable<SimulationDateIfc> {

	SimulationDateIfc infinite = SimulationDate.future();
	
	int getDay();
	int getHour();
	int getMinute();
	int getSecond();
	long toSeconds();

	SimulationDateIfc previousDay();
	SimulationDateIfc nextDay();

	DayOfWeek weekDay();

	boolean isBefore(SimulationDateIfc otherDate);
	boolean isBeforeOrEqualTo(SimulationDateIfc otherDate);
	boolean isAfter(SimulationDateIfc otherDate);
	boolean isAfterOrEqualTo(SimulationDateIfc otherDate);
	boolean isMidnight();

	SimulationDateIfc plus(long amount, ChronoUnit unit);
	SimulationDateIfc plus(RelativeTime increment);
	SimulationDateIfc plusDays(int increment);
	SimulationDateIfc plusHours(int increment);
	SimulationDateIfc plusMinutes(int increment);
	SimulationDateIfc plusSeconds(int increment);
	SimulationDateIfc minus(RelativeTime decrement);

	SimulationDateIfc startOfDay();
	SimulationDateIfc newTime(int hour, int minute, int second);

	RelativeTime differenceTo(SimulationDateIfc otherDate);
	
	RelativeTime fromStart();

}
