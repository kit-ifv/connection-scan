package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.simulation.SimulationDateIfc;

public class ArriveAt extends TypeSafeMatcher<Connection> {

	private final SimulationDateIfc arrival;

	public ArriveAt(SimulationDateIfc arrival) {
		super();
		this.arrival = arrival;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("arrives at ");
		description.appendValue(arrival);
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return arrival.equals(connection.arrival());
	}

	@Override
	protected void describeMismatchSafely(Connection item, Description mismatchDescription) {
		mismatchDescription.appendText("arrives at ");
		mismatchDescription.appendValue(item.arrival());
	}
}