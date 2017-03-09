package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.yetAnotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class DefaultModifiableJourneyTest {

	private static final int someId = 0;
	private static final int anotherId = 1;
	private static final Time someDay = new Time(LocalDateTime.of(2011, 10, 17, 0, 0));
	private static final Time anotherDay = new Time(LocalDateTime.of(2011, 10, 18, 0, 0));
	private Connection first;
	private Connection second;
	private Connection last;

	@Before
	public void initialise() throws Exception {
		first = connection().startsAt(someStop()).endsAt(anotherStop()).build();
		second = connection().startsAt(anotherStop()).endsAt(otherStop()).build();
		last = connection().startsAt(otherStop()).endsAt(yetAnotherStop()).build();
	}

	private void addSeveralConnections() {
		ModifiableJourney journey = journey().build();
		journey.add(first);
		journey.add(second);
		journey.add(last);
	}
	
	@Test
	public void updatesPositionOfConnectionsInJourney() throws Exception {
		addSeveralConnections();
		
		assertThat(first.positionInJourney(), is(0));
		assertThat(second.positionInJourney(), is(1));
		assertThat(last.positionInJourney(), is(2));
	}
	
	@Test
	public void equalsAndHashCode() throws Exception {
		Journey oneJourney = someJourney();
		Journey sameJourney = someJourney();
		Journey anotherJourney = anotherJourney();
		Journey oneJourneyAtAnotherDay = someJourneyAtAnotherDay();
		Journey anotherJourneyAtAnotherDay = anotherJourneyAtAnotherDay();

		assertThat(oneJourney, is(equalTo(sameJourney)));
		assertThat(sameJourney, is(equalTo(oneJourney)));
		assertThat(oneJourney.hashCode(), is(equalTo(sameJourney.hashCode())));

		assertThat(oneJourney, is(equalTo(oneJourney)));
		assertThat(oneJourney.hashCode(), is(equalTo(oneJourney.hashCode())));

		assertThat(oneJourney, is(not(equalTo(anotherJourney))));
		assertThat(anotherJourney, is(not(equalTo(oneJourney))));
		assertThat(oneJourney, is(not(equalTo(oneJourneyAtAnotherDay))));
		assertThat(oneJourney, is(not(equalTo(anotherJourneyAtAnotherDay))));
		assertThat(anotherJourney, is(not(equalTo(oneJourneyAtAnotherDay))));
		assertThat(anotherJourney, is(not(equalTo(anotherJourneyAtAnotherDay))));
	}

	private static Journey someJourney() {
		return journey().withId(someId).at(someDay).build();
	}

	private static Journey anotherJourney() {
		return journey().withId(anotherId).at(someDay).build();
	}

	private static Journey someJourneyAtAnotherDay() {
		return journey().withId(someId).at(anotherDay).build();
	}

	private static Journey anotherJourneyAtAnotherDay() {
		return journey().withId(anotherId).at(anotherDay).build();
	}

}
