package edu.kit.ifv.mobitopp.publictransport.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RoutePoints {

	private final List<Point2D> points;

	public RoutePoints() {
		this(new ArrayList<>());
	}

	RoutePoints(List<Point2D> points) {
		super();
		this.points = new ArrayList<>(points);
	}

	/**
	 * Returns an unmodifiable list of {@link Point2D}.
	 * 
	 * @return an unmodifiable list of {@link Point2D}
	 */
	public List<Point2D> toList() {
		return Collections.unmodifiableList(points);
	}

	public void add(Point2D coordinate) {
		points.add(coordinate);
	}

	public void forEach(Consumer<Point2D> consumer) {
		points.forEach(consumer);
	}

	RoutePoints startsWith(Stop start) {
		Point2D startLocation = start.coordinate();
		if (points.isEmpty()) {
			return new RoutePoints(Collections.singletonList(startLocation));
		}
		ArrayList<Point2D> containsStart = new ArrayList<>();
		if (startsNotAt(startLocation)) {
			containsStart.add(startLocation);
		}
		containsStart.addAll(points);
		return new RoutePoints(containsStart);
	}

	private boolean startsNotAt(Point2D startLocation) {
		return !startLocation.equals(firstLocation());
	}

	private Point2D firstLocation() {
		return points.get(0);
	}

	RoutePoints endsWith(Stop end) {
		Point2D endLocation = end.coordinate();
		if (points.isEmpty()) {
			return new RoutePoints(Collections.singletonList(endLocation));
		}
		ArrayList<Point2D> containsEnd = new ArrayList<>(points);
		if (endsNotAt(endLocation)) {
			containsEnd.add(endLocation);
		}
		return new RoutePoints(containsEnd);
	}

	private boolean endsNotAt(Point2D endLocation) {
		return !endLocation.equals(lastLocation());
	}

	private Point2D lastLocation() {
		return points.get(points.size() - 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
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
		RoutePoints other = (RoutePoints) obj;
		if (points == null) {
			if (other.points != null) {
				return false;
			}
		} else if (!points.equals(other.points)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RoutePoints [points=" + points + "]";
	}

}
