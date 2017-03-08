package edu.kit.ifv.mobitopp.publictransport.model;

public class Connection {

	private static final int footId = -1;

	private final int id;
	private final Stop start;
	private final Stop end;
	private final Time arrival;
	private final Time departure;
	private final RoutePoints points;
	private final Journey journey;
	private int positionInJourney;
	private Connection next;

	private Connection(
			int id, Stop start, Stop end, Time departure, Time arrival, Journey journey,
			RoutePoints points) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.departure = departure;
		this.arrival = arrival;
		this.points = points;
		this.journey = journey;
	}

	public Connection(Connection other) {
		id = other.id();
		start = other.start();
		end = other.start();
		departure = other.departure();
		arrival = other.arrival();
		points = other.points();
		journey = other.journey();
	}

	public static Connection from(
			int id, Stop start, Stop end, Time departure, Time arrival, Journey journey,
			RoutePoints route) {
		RoutePoints containsStart = route.startsWith(start);
		RoutePoints containsStartAndEnd = containsStart.endsWith(end);
		return new Connection(id, start, end, departure, arrival, journey, containsStartAndEnd);
	}

	public static Connection byFootFrom(Stop stop, Stop neighbour, Time departure, Time arrival) {
		RoutePoints route = new RoutePoints();
		route.add(stop.coordinate());
		route.add(neighbour.coordinate());
		return Connection.from(footId, stop, neighbour, departure, arrival, FootJourney.footJourney, route);
	}

	public static Connection byFootFrom(Stop stop, Stop neighbour, Time departure) {
		Time walkTime = stop.arrivalAt(neighbour, departure).get();
		return byFootFrom(stop, neighbour, departure, walkTime);
	}

	public int id() {
		return id;
	}

	public Stop start() {
		return start;
	}

	public Stop end() {
		return end;
	}

	public RoutePoints points() {
		return points;
	}

	public Time departure() {
		return departure;
	}

	public Time arrival() {
		return arrival;
	}

	public RelativeTime duration() {
		return departure.durationTo(arrival);
	}

	public Journey journey() {
		return journey;
	}
	
	public Connection next() {
		return next;
	}
	
	void setNext(Connection next) {
		this.next = next;
	}
	
	public int positionInJourney() {
		return positionInJourney;
	}
	
	void setPositionInJourney(int position) {
		positionInJourney = position;
	}

	public boolean isValid() {
		return differentStartAndEnd() && departsBeforeOrAtSameTimeAsItArrives();
	}

	private boolean differentStartAndEnd() {
		return !start.equals(end);
	}

	private boolean departsBeforeOrAtSameTimeAsItArrives() {
		return departure.compareTo(arrival) <= 0;
	}

	public boolean departsBefore(Time time) {
		return departure.isBefore(time);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrival == null) ? 0 : arrival.hashCode());
		result = prime * result + ((departure == null) ? 0 : departure.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((journey == null) ? 0 : journey.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		Connection other = (Connection) obj;
		if (arrival == null) {
			if (other.arrival != null) {
				return false;
			}
		} else if (!arrival.equals(other.arrival)) {
			return false;
		}
		if (departure == null) {
			if (other.departure != null) {
				return false;
			}
		} else if (!departure.equals(other.departure)) {
			return false;
		}
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (journey == null) {
			if (other.journey != null) {
				return false;
			}
		} else if (!journey.equals(other.journey)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Connection [" + System.lineSeparator() + "id=" + id + System.lineSeparator()
				+ "start=" + start + System.lineSeparator() + "end=" + end + System.lineSeparator()
				+ "arrival=" + arrival + System.lineSeparator() + "departure=" + departure
				+ System.lineSeparator() + "trip=" + journey + System.lineSeparator() + "points="
				+ points + "]";
	}

}
